package fr.syncrase.ecosyst.feature.add_plante.consistency;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomikRanks;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchRepository;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MonolithApp.class)
class ClassificationConsistencyServiceTest {

    @Autowired
    private ClassificationConsistencyService classificationConsistencyService;

    @Test
    void checkConsistencyWithNoConflictAndAllRanksUnknown() throws ClassificationReconstructionException, MoreThanOneResultException {

        ClassificationConflict conflicts = classificationConsistencyService.checkConsistency(ClassificationBranchRepository.ALLIUM.getClassification());
        Assertions.assertNotNull(conflicts, "La classification conflictuel doit exister");
        Assertions.assertEquals(0, conflicts.getConflictedClassifications().size(), "La classification conflictuel ne doit contenir aucun conflit");
        Assertions.assertEquals(27, conflicts.getNewClassification().size(), "La classification à insérer doit posséder 27 éléments");
        Assertions.assertEquals("Allium", conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.GENRE).getNom(), "Le genre doit être Allium");
        Assertions.assertEquals("Liliaceae", conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.FAMILLE).getNom(), "La famille doit être Liliaceae");
        Assertions.assertEquals("Liliales", conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.ORDRE).getNom(), "La famille doit être Liliales");
        Assertions.assertEquals("Liliidae", conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.SOUSCLASSE).getNom(), "La famille doit être Liliidae");
        Assertions.assertEquals("Liliopsida", conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.CLASSE).getNom(), "La famille doit être Liliopsida");
        Assertions.assertEquals("Magnoliophyta", conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.EMBRANCHEMENT).getNom(), "La famille doit être Magnoliophyta");
        Assertions.assertEquals("Tracheobionta", conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.SOUSREGNE).getNom(), "La famille doit être Tracheobionta");
        Assertions.assertEquals("Plantae", conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.REGNE).getNom(), "La famille doit être Plantae");
    }

}
