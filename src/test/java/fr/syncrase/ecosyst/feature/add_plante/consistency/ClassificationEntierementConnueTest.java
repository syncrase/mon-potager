package fr.syncrase.ecosyst.feature.add_plante.consistency;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomikRanks;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchRepository;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistWriter;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MonolithApp.class)
public class ClassificationEntierementConnueTest {

    @Autowired
    CronquistWriter cronquistWriter;

    @Autowired
    private ClassificationConsistencyService classificationConsistencyService;


    @Test
    public void checkConsistencyDUneClassificationEnregistreeDeuxFois() throws ClassificationReconstructionException, MoreThanOneResultException {
        // Règne 	Plantae
        //Sous-règne 	Tracheobionta
        //Division 	Magnoliophyta
        //Classe 	Magnoliopsida
        //Sous-classe 	Dilleniidae
        //Ordre 	Nepenthales
        //Famille 	Droseraceae
        //Genre Aldrovanda
        // Enregistrement une première fois
        cronquistWriter.saveClassification(ClassificationBranchRepository.ALLIUM.getClassification());

        ClassificationConflict conflicts = classificationConsistencyService.checkConsistency(ClassificationBranchRepository.ALLIUM.getClassification());
        CronquistClassificationBranch cronquistClassificationBranch = cronquistWriter.saveClassification(conflicts.getNewClassification());


        Assertions.assertEquals(0, conflicts.getConflictedClassifications().size(), "La classification conflictuel ne doit contenir aucun conflit");
        Assertions.assertNotNull(conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.REGNE).getId(), "Le règne doit avoir été récupéré de la base de données");
        Assertions.assertEquals("Plantae", conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.REGNE).getNom(), "Le règne doit être Plantae");
        Assertions.assertNotNull(conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.SOUSREGNE).getId(), "Le sous-règne doit avoir été récupéré de la base de données");
        Assertions.assertEquals("Tracheobionta", conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.SOUSREGNE).getNom(), "Le sous-règne doit être Tracheobionta");
        Assertions.assertNotNull(conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.EMBRANCHEMENT).getId(), "L'embranchement doit avoir été récupéré de la base de données");
        Assertions.assertEquals("Magnoliophyta", conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.EMBRANCHEMENT).getNom(), "L'embranchement doit être Magnoliophyta");

    }

}
