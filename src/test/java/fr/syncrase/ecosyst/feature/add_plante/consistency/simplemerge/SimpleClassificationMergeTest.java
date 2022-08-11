package fr.syncrase.ecosyst.feature.add_plante.consistency.simplemerge;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConflict;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConsistencyService;
import fr.syncrase.ecosyst.feature.add_plante.consistency.InconsistencyResolverException;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchRepository;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistWriter;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.SortedSet;

@SpringBootTest(classes = MonolithApp.class)
public class SimpleClassificationMergeTest {

    @Autowired
    CronquistWriter cronquistWriter;

    @Autowired
    private ClassificationConsistencyService classificationConsistencyService;

    @AfterEach
    void tearDown() {
        cronquistWriter.removeAll();
    }

    @Test
    public void checkConsistency_mergeWithoutNameConflict() throws ClassificationReconstructionException, MoreThanOneResultException, InconsistencyResolverException {

        // Règne 	Plantae
        //Sous-règne 	Tracheobionta
        //Division 	Magnoliophyta
        //Classe 	Magnoliopsida
        //Sous-classe 	Rosidae
        //Ordre 	Santalales
        //Famille 	Santalaceae
        //Genre Arjona
        CronquistClassificationBranch arjonaClassification = enregistrementDeLaClassificationArjona();

        // Règne 	Plantae
        //Sous-règne 	(+Tracheobionta déduit du précédent)
        //Division 	Magnoliophyta
        //Classe 	Magnoliopsida
        //Sous-classe 	(+Rosidae déduis du suivant)
        //Ordre 	Sapindales
        //Famille 	Sapindaceae
        //Genre Atalaya
        // La Atalaya appartient à la sous-classe des Rosidae, mais on ne le sait pas à partir des informations reçues. On le découvre quand on enregistre Cossinia
        CronquistClassificationBranch atalayaClassification = synchronisationEtEnregistrementDeLaClassificationAtalaya();

        // Règne 	Plantae
        //Sous-règne 	Tracheobionta
        //Division 	Magnoliophyta
        //Classe 	Magnoliopsida
        //Sous-classe 	Rosidae
        //Ordre 	Sapindales
        //Famille 	Sapindaceae
        //Genre Cossinia
        // La synchronisation de Cossinia relève un conflit au niveau de la sous-classe de sa classification.
        // Ce conflit fait l'objet d'une résolution se faisant par le merge des segments de classification (déplacement d'enfant et suppression de classification ascendante).
        synchronisationPuisMergeEtSynchronisationPostMergePuisEnregistrementDeLaClassificationCossiniaPinnata(arjonaClassification, atalayaClassification);
    }

    private void synchronisationPuisMergeEtSynchronisationPostMergePuisEnregistrementDeLaClassificationCossiniaPinnata(
        @NotNull CronquistClassificationBranch arjonaClassification,
        @NotNull CronquistClassificationBranch atalayaClassification
    )
        throws ClassificationReconstructionException, MoreThanOneResultException, InconsistencyResolverException {
        ClassificationConflict cossiniaPinnataConflicts = classificationConsistencyService.getSynchronizedClassificationAndConflicts(ClassificationBranchRepository.COSSINIA_PINNATA.getClassification());
        Assertions.assertEquals(1, cossiniaPinnataConflicts.getConflictedClassifications().size(), "Il doit y avoir un conflit");
        Assertions.assertNotNull(cossiniaPinnataConflicts.getNewClassification(), "La classification faisant l'objet des conflits ne doit pas être null");

        ClassificationConflict resolvedCossiniaPinnataConflicts = classificationConsistencyService.resolveInconsistency(cossiniaPinnataConflicts);// La base est mise à jour pour accepter la nouvelle classification
        Assertions.assertEquals(0, resolvedCossiniaPinnataConflicts.getConflictedClassifications().size(), "Il ne doit plus y avoir de conflit");
        Assertions.assertNotNull(resolvedCossiniaPinnataConflicts.getNewClassification(), "La classification faisant l'objet des conflits ne doit pas être null");

        SortedSet<CronquistRank> mustBeRemovedRanks = atalayaClassification.subSet(atalayaClassification.getRang(CronquistTaxonomicRank.ORDRE), atalayaClassification.getRang(CronquistTaxonomicRank.SUPERCLASSE));
        // TODO se construire des mocks offrant plusieurs rangs de liaisons ascendants et descendant afin de bien tester la suppression des rang et le changement d'enfant au bon endroit de la classification => 2 rang de liaison ascendant et autant en descendant
        for (CronquistRank r : mustBeRemovedRanks) {
            Assertions.assertNull(cronquistReader.findExistingRank(r), "Le rang " + r + " doit avoir été supprimé");
        }

        Set<CronquistRank> childrenOfArjona = cronquistReader.findChildrenOf(arjonaClassification.getRang(CronquistTaxonomicRank.SOUSCLASSE).getId());
        Set<CronquistRank> previouslySavedChildrenOfAtalaya = atalayaClassification.getRang(CronquistTaxonomicRank.SOUSCLASSE).getChildren();
        Assertions.assertTrue(
            childrenOfArjona.containsAll(
                previouslySavedChildrenOfAtalaya
            ),
            "Les enfants du rang de liaison mergé avec Rosidae doivent tous avoir été ajouté aux enfants de la sous-classe");

        Assertions.assertNull(
            resolvedCossiniaPinnataConflicts.getNewClassification().getRang(CronquistTaxonomicRank.SOUSCLASSE).getId(),
            "Rosidae (de la classification en conflit) ne doit pas posséder d'ID");
        ClassificationConflict resolvedCossiniaPinnataConflictsAfterConsistencyCheck = classificationConsistencyService.getSynchronizedClassificationAndConflicts(ClassificationBranchRepository.COSSINIA_PINNATA.getClassification());
        Assertions.assertNotNull(
            resolvedCossiniaPinnataConflictsAfterConsistencyCheck.getNewClassification().getRang(CronquistTaxonomicRank.SOUSCLASSE).getId(),
            "Rosidae (de la classification en conflit) doit dorénavant posséder un ID");
    }

    private CronquistClassificationBranch synchronisationEtEnregistrementDeLaClassificationAtalaya() throws ClassificationReconstructionException, MoreThanOneResultException {
        ClassificationConflict atalayaConflicts = classificationConsistencyService.getSynchronizedClassificationAndConflicts(ClassificationBranchRepository.ATALAYA.getClassification());
        Assertions.assertEquals(0, atalayaConflicts.getConflictedClassifications().size(), "Il ne doit pas y avoir de conflit");
        CronquistRank atalayaSousRegne = atalayaConflicts.getNewClassification().getRang(CronquistTaxonomicRank.SOUSREGNE);
        Assertions.assertEquals("Tracheobionta", atalayaSousRegne.getNom(), "La classification atalaya doit posséder le sous-règne Tracheobionta mais est égal à " + atalayaSousRegne);
        return cronquistWriter.saveClassification(atalayaConflicts.getNewClassification());
    }

    private CronquistClassificationBranch enregistrementDeLaClassificationArjona() {
        return cronquistWriter.saveClassification(ClassificationBranchRepository.ARJONA.getClassification());
    }

}
