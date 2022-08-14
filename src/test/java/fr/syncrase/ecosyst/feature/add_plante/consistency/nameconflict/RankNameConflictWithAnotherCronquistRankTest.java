package fr.syncrase.ecosyst.feature.add_plante.consistency.nameconflict;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConflict;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConsistencyService;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ConflictualRank;
import fr.syncrase.ecosyst.feature.add_plante.consistency.InconsistencyResolverException;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchMockRepository;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistReader;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistWriter;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = MonolithApp.class)
public class RankNameConflictWithAnotherCronquistRankTest {

    @Autowired
    CronquistReader cronquistReader;

    @Autowired
    CronquistWriter cronquistWriter;

    @Autowired
    private ClassificationConsistencyService classificationConsistencyService;

    @AfterEach
    void tearDown() {
        cronquistWriter.removeAll();
    }


    @Test
    public void checkConsistency_conflictWithAnotherCronquistRank() throws ClassificationReconstructionException, MoreThanOneResultException, InconsistencyResolverException {

        /*
         * Règne 	Plantae
         * Sous-règne 	doit être ajouté > Tracheobionta
         * Division 	doit être ajouté > Magnoliophyta
         * Classe 	Equisetopsida (conflit avec Magnoliopsida : doit être remplacé)
         * Sous-classe 	Magnoliidae ← conflit avec une autre sous-classe
         * Super-ordre 	Rosanae
         * Ordre 	Sapindales
         * Famille 	Aceraceae
         * Genre 	Acer
         * Espèce Acer sempervirens
         */
        CronquistClassificationBranch erableDeCreteClassificationMock = ClassificationBranchMockRepository.ERABLE_DE_CRETE.getClassification();
        CronquistClassificationBranch erableDeCreteClassification = cronquistWriter.saveClassification(erableDeCreteClassificationMock);

        /*
         * Règne 	Plantae
         * Sous-règne 	Tracheobionta
         * Division 	Magnoliophyta
         * Classe 	Magnoliopsida (conflit avec Equisetopsida qui doit être remplacé)
         * Sous-classe 	Rosidae ← conflit avec une autre sous-classe
         * Super-ordre 	doit être ajouté > Rosanae
         * Ordre 	Sapindales
         * Famille 	Aceraceae
         * Genre 	Acer
         * Espèce Acer miyabei
         */
        CronquistClassificationBranch erableDeMiyabeClassification = ClassificationBranchMockRepository.ERABLE_DE_MIYABE.getClassification();
        Assertions.assertNull(erableDeMiyabeClassification.getRang(CronquistTaxonomicRank.SUPERORDRE).getNom(), "Le superordre n'existe pas initialement");

        ClassificationConflict erableMiyabeConflicts = classificationConsistencyService.getSynchronizedClassificationAndConflicts(erableDeMiyabeClassification);
        assertAfterFirstSynchronization(erableMiyabeConflicts);

        ClassificationConflict resolvedErableMiyabeConflicts = classificationConsistencyService.resolveInconsistencyInDatabase(erableMiyabeConflicts);
        assertsAfterResolution(erableDeCreteClassification, resolvedErableMiyabeConflicts);

        // Impossible de sauvegarder car un conflit persiste
    }

    private void assertAfterFirstSynchronization(@NotNull ClassificationConflict erableMiyabeConflicts) {
        Assertions.assertEquals("Rosanae", erableMiyabeConflicts.getNewClassification().getRang(CronquistTaxonomicRank.SUPERORDRE).getNom(), "Le super-ordre doit être rajouté lors de la synchronisation avec la base");

        Assertions.assertEquals(2, erableMiyabeConflicts.getConflictedClassifications().size(), "Il doit exister deux conflits");

        Optional<ConflictualRank> sousClasseConflict = erableMiyabeConflicts.getConflictedClassifications().stream()
            .filter(conflictualRank1 -> conflictualRank1.getExisting().getRank().equals(CronquistTaxonomicRank.SOUSCLASSE))
            .findFirst();
        if (sousClasseConflict.isPresent()) {
            Assertions.assertEquals("Magnoliidae", sousClasseConflict.get().getExisting().getNom());
            Assertions.assertEquals("Rosidae", sousClasseConflict.get().getScraped().getNom());
        } else {
            fail("Un conflit doit exister au niveau de la sous-classe");
        }

        Optional<ConflictualRank> classeConflict = erableMiyabeConflicts.getConflictedClassifications().stream()
            .filter(conflictualRank1 -> conflictualRank1.getExisting().getRank().equals(CronquistTaxonomicRank.CLASSE))
            .findFirst();
        if (classeConflict.isPresent()) {
            Assertions.assertEquals("Equisetopsida", classeConflict.get().getExisting().getNom());
            Assertions.assertEquals("Magnoliopsida", classeConflict.get().getScraped().getNom());
        } else {
            fail("Un conflit doit exister au niveau de la classe");
        }

        Assertions.assertEquals("Tracheobionta", erableMiyabeConflicts.getNewClassification().getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Le sous-règne doit avoir été ajouté lors de la synchronisation");
        Assertions.assertEquals("Magnoliophyta", erableMiyabeConflicts.getNewClassification().getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "L'embranchement doit avoir été ajouté lors de la synchronisation");
        Assertions.assertNotNull(erableMiyabeConflicts.getNewClassification().getRang(CronquistTaxonomicRank.SOUSREGNE).getId(), "Le sous-règne doit déjà posséder l'identifiant du rang de liaison qu'il va remplacer");
        Assertions.assertNotNull(erableMiyabeConflicts.getNewClassification().getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getId(), "L'embranchement doit posséder l'identifiant du rang de liaison qu'il va remplace");
    }

    private void assertsAfterResolution(CronquistClassificationBranch erableDeCreteClassification, @NotNull ClassificationConflict resolvedErableMiyabeConflicts) throws MoreThanOneResultException {
        Assertions.assertEquals(1, resolvedErableMiyabeConflicts.getConflictedClassifications().size(), "Il doit rester un conflit insoluble");
        Optional<ConflictualRank> remainingClasseConflict = resolvedErableMiyabeConflicts.getConflictedClassifications().stream().findFirst();
        if (remainingClasseConflict.isPresent()) {
            Assertions.assertEquals("Magnoliidae", remainingClasseConflict.get().getExisting().getNom());
            Assertions.assertEquals("Rosidae", remainingClasseConflict.get().getScraped().getNom());
        } else {
            fail("Le conflit sur la sous classe doit toujours exister");
        }

        CronquistClassificationBranch erableDeCreteUpdatedClassification = cronquistReader.findExistingClassification(erableDeCreteClassification.getLowestRank());
        Assertions.assertNotNull(erableDeCreteUpdatedClassification, "La classification erable de crete doit exister");
        Assertions.assertEquals("Magnoliopsida", erableDeCreteUpdatedClassification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Equisetopsida doit avoir été remplacée par Magnoliopsida");
    }


}
