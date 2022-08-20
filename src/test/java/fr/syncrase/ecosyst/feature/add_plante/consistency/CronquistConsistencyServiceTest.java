package fr.syncrase.ecosyst.feature.add_plante.consistency;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchMockRepository;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MonolithApp.class)
class CronquistConsistencyServiceTest {

    @Autowired
    private CronquistConsistencyService cronquistConsistencyService;

    @Test
    void checkConsistencyWithNoConflictAndAllRanksUnknown() throws ClassificationReconstructionException, MoreThanOneResultException {

        CronquistClassificationBranch classification = ClassificationBranchMockRepository.ALLIUM.getClassification();
        ClassificationConflict conflicts = cronquistConsistencyService.getSynchronizedClassificationAndConflicts(classification);
        Assertions.assertNotNull(conflicts, "La classification conflictuel doit exister");
        Assertions.assertEquals(0, conflicts.getConflictedClassifications().size(), "La classification conflictuel ne doit contenir aucun conflit");
        Assertions.assertEquals(27, conflicts.getNewClassification().size(), "La classification à insérer doit posséder 27 éléments");
        Assertions.assertEquals("allium", conflicts.getNewClassification().getRang(CronquistTaxonomicRank.GENRE).getNom(), "Le genre doit être Allium");
        Assertions.assertEquals("liliaceae", conflicts.getNewClassification().getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "La famille doit être Liliaceae");
        Assertions.assertEquals("liliales", conflicts.getNewClassification().getRang(CronquistTaxonomicRank.ORDRE).getNom(), "La famille doit être Liliales");
        Assertions.assertEquals("liliidae", conflicts.getNewClassification().getRang(CronquistTaxonomicRank.SOUSCLASSE).getNom(), "La famille doit être Liliidae");
        Assertions.assertEquals("liliopsida", conflicts.getNewClassification().getRang(CronquistTaxonomicRank.CLASSE).getNom(), "La famille doit être Liliopsida");
        Assertions.assertEquals("magnoliophyta", conflicts.getNewClassification().getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "La famille doit être Magnoliophyta");
        Assertions.assertEquals("tracheobionta", conflicts.getNewClassification().getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "La famille doit être Tracheobionta");
        Assertions.assertEquals("plantae", conflicts.getNewClassification().getRang(CronquistTaxonomicRank.REGNE).getNom(), "La famille doit être Plantae");

    }

}
