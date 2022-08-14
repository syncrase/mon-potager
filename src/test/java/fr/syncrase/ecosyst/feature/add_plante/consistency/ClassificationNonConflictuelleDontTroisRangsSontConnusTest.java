package fr.syncrase.ecosyst.feature.add_plante.consistency;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchMockRepository;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistWriter;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MonolithApp.class)
public class ClassificationNonConflictuelleDontTroisRangsSontConnusTest {

    @Autowired
    CronquistWriter cronquistWriter;

    @Autowired
    private ClassificationConsistencyService classificationConsistencyService;

    @AfterEach
    void tearDown() {
        cronquistWriter.removeAll();
    }


    @Test
    public void checkConsistencyDUneClassificationNonConflictuelleDontTroisRangsSontConnus() throws ClassificationReconstructionException, MoreThanOneResultException {


        // Règne 	Plantae
        //Sous-règne 	Tracheobionta
        //Division 	Magnoliophyta
        //Classe 	Magnoliopsida
        //Sous-classe 	Dilleniidae
        //Ordre 	Nepenthales
        //Famille 	Droseraceae
        //Genre Aldrovanda
        CronquistClassificationBranch cronquistClassificationBranch = cronquistWriter.saveClassification(ClassificationBranchMockRepository.ALLIUM.getClassification());

        ClassificationConflict conflicts = classificationConsistencyService.getSynchronizedClassificationAndConflicts(ClassificationBranchMockRepository.ALDROVANDA.getClassification());

        Assertions.assertEquals(0, conflicts.getConflictedClassifications().size(), "La classification conflictuel ne doit contenir aucun conflit");

        Assertions.assertNotNull(conflicts.getNewClassification().getRang(CronquistTaxonomicRank.REGNE).getId(), "Le règne doit avoir été récupéré de la base de données");
        Assertions.assertEquals("Plantae", conflicts.getNewClassification().getRang(CronquistTaxonomicRank.REGNE).getNom(), "Le règne doit être Plantae");

        Assertions.assertNotNull(conflicts.getNewClassification().getRang(CronquistTaxonomicRank.SOUSREGNE).getId(), "Le sous-règne doit avoir été récupéré de la base de données");
        Assertions.assertEquals("Tracheobionta", conflicts.getNewClassification().getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Le sous-règne doit être Tracheobionta");

        Assertions.assertNotNull(conflicts.getNewClassification().getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getId(), "L'embranchement doit avoir été récupéré de la base de données");
        Assertions.assertEquals("Magnoliophyta", conflicts.getNewClassification().getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "L'embranchement doit être Magnoliophyta");

    }

}
