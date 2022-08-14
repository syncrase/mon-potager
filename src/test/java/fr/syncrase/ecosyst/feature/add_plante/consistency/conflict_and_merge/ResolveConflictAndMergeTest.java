package fr.syncrase.ecosyst.feature.add_plante.consistency.conflict_and_merge;

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
public class ResolveConflictAndMergeTest {

    @Autowired
    CronquistWriter cronquistWriter;

    @Autowired
    CronquistReader cronquistReader;

    @Autowired
    private ClassificationConsistencyService classificationConsistencyService;

    @AfterEach
    void tearDown() {
        cronquistWriter.removeAll();
    }

    @Test
    public void checkConsistency_rankSavedInTheWrongPlace() throws ClassificationReconstructionException, MoreThanOneResultException, InconsistencyResolverException {

        // Règne 	Plantae
        //Division 	Angiospermae
        //Classe 	Lilianae        ←- Lilianae est un superordre
        //Ordre 	Alismatales
        //Famille 	Alismataceae
        //Genre 	Helanthium
        //Espèce    Helanthium bolivianum
        //wiki = "https://fr.wikipedia.org/wiki/Helanthium_bolivianum";
        CronquistClassificationBranch classification = ClassificationBranchMockRepository.HELANTHIUM_BOLIVIANUM.getClassification();
        CronquistClassificationBranch helanthiumBolivianumClassification = cronquistWriter.saveClassification(classification);

        // Règne 	    Plantae
        //Classe 	    Equisetopsida   <- rentre en conflit avec l'autre
        //Sous-classe 	Magnoliidae
        //Super-ordre 	Lilianae
        //Ordre 	    Asparagales
        //Famille 	    Asparagaceae
        //Genre 	    Agave
        //Espèce        Agave lechuguilla
        //String wiki = "https://fr.wikipedia.org/wiki/Agave_lechuguilla";
        CronquistClassificationBranch agave_lechuguillaClassification = ClassificationBranchMockRepository.AGAVE_LECHUGUILLA.getClassification();
        agave_lechuguillaClassification.clearRank(CronquistTaxonomicRank.CLASSE);
        ClassificationConflict agaveLechuguillaConflicts = classificationConsistencyService.getSynchronizedClassificationAndConflicts(agave_lechuguillaClassification);
        assertsAfterAgaveSynchronization(agaveLechuguillaConflicts);

        ClassificationConflict resolvedAgaveLechuguillaConflicts = classificationConsistencyService.resolveInconsistencyInDatabase(agaveLechuguillaConflicts);
        assertsAfterAgaveResolution(helanthiumBolivianumClassification, resolvedAgaveLechuguillaConflicts);

        // Impossible de régler le conflit, car impossible de scraper Lilianae de Wikipédia (Cronquist indisponible)
    }

    private void assertAfterAgaveSynchronizationAfterResolution(@NotNull ClassificationConflict synchronizedResolvedAgaveLechuguillaConflicts) {
        Assertions.assertEquals(1, synchronizedResolvedAgaveLechuguillaConflicts.getConflictedClassifications().size(), "Il doit y avoir un conflit");
    }

    private void assertsAfterAgaveResolution(@NotNull CronquistClassificationBranch helanthiumBolivianumClassification, @NotNull ClassificationConflict resolvedAgaveLechuguillaConflicts) throws MoreThanOneResultException {
        Assertions.assertEquals(0, resolvedAgaveLechuguillaConflicts.getConflictedClassifications().size(), "Il doit y avoir un conflit");
        CronquistClassificationBranch resolvedHelanthiumBolivianumClassification = cronquistReader.findExistingClassification(helanthiumBolivianumClassification.getLowestRank());
        Assertions.assertNotNull(resolvedHelanthiumBolivianumClassification, "La classification doit exister en base");
    }

    private void assertsAfterAgaveSynchronization(@NotNull ClassificationConflict agaveLechuguillaConflicts) {
        Assertions.assertEquals(1, agaveLechuguillaConflicts.getConflictedClassifications().size(), "Il doit y avoir un conflit");
        Assertions.assertNotNull(agaveLechuguillaConflicts.getNewClassification(), "La classification à enregistrer doit exister");
        Optional<ConflictualRank> first = agaveLechuguillaConflicts.getConflictedClassifications().stream().findFirst();
        if (first.isPresent()) {
            Assertions.assertEquals(CronquistTaxonomicRank.SUPERORDRE, first.get().getScraped().getRank(), "Le superordre de la nouvelle classification doit être en conflit");
            Assertions.assertEquals(CronquistTaxonomicRank.CLASSE, first.get().getExisting().getRank(), "La classe de la classification existante doit être en conflit");
            Assertions.assertEquals("Lilianae", first.get().getScraped().getNom(), "Le nom de la nouvelle classification doit être Lilianae");
            Assertions.assertEquals("Lilianae", first.get().getExisting().getNom(), "La nom de la classification existante doit être Lilianae");
        } else {
            fail();
        }
    }

}
