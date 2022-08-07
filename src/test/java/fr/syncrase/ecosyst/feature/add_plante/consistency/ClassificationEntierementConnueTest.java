package fr.syncrase.ecosyst.feature.add_plante.consistency;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.CronquistRank;
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

import java.util.Iterator;

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
        //Classe 	Liliopsida
        //Sous-classe 	Liliidae
        //Ordre 	Liliales
        //Famille 	Liliaceae
        //Genre Allium
        /*
         * Enregistrement une première fois
         */
        CronquistClassificationBranch firstCronquistClassificationBranch = cronquistWriter.saveClassification(ClassificationBranchRepository.ALLIUM.getClassification());

        /*
         * Vérification de la consistance pour un autre objet sémantiquement le même
         */
        ClassificationConflict conflicts = classificationConsistencyService.checkConsistency(ClassificationBranchRepository.ALLIUM.getClassification());
        Assertions.assertEquals(0, conflicts.getConflictedClassifications().size(), "La classification conflictuel ne doit contenir aucun conflit");
        Assertions.assertNotNull(conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.REGNE).getId(), "Le règne doit avoir été récupéré de la base de données");
        Assertions.assertEquals("Plantae", conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.REGNE).getNom(), "Le règne doit être Plantae");
        Assertions.assertNotNull(conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.SOUSREGNE).getId(), "Le sous-règne doit avoir été récupéré de la base de données");
        Assertions.assertEquals("Tracheobionta", conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.SOUSREGNE).getNom(), "Le sous-règne doit être Tracheobionta");
        Assertions.assertNotNull(conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.EMBRANCHEMENT).getId(), "L'embranchement doit avoir été récupéré de la base de données");
        Assertions.assertEquals("Magnoliophyta", conflicts.getNewClassification().getRang(CronquistTaxonomikRanks.EMBRANCHEMENT).getNom(), "L'embranchement doit être Magnoliophyta");


        /*
         * Enregistrement une seconde fois de cet autre objet
         */
        CronquistClassificationBranch secondCronquistClassificationBranch = cronquistWriter.saveClassification(conflicts.getNewClassification());

        Assertions.assertSame(firstCronquistClassificationBranch, secondCronquistClassificationBranch, "Les deux classifications doivent être identiques");
        Assertions.assertEquals(firstCronquistClassificationBranch.size(), secondCronquistClassificationBranch.size(), "Les deux classifications doivent avoir la même taille");
        Iterator<CronquistRank> iterator1 = firstCronquistClassificationBranch.iterator();
        Iterator<CronquistRank> iterator2 = secondCronquistClassificationBranch.iterator();
        while (iterator1.hasNext() && iterator2.hasNext()) {
            CronquistRank next1 = iterator1.next();
            CronquistRank next2 = iterator2.next();
            Assertions.assertEquals(next1.getId(), next2.getId(), "Les IDs de chacun des éléments de la classification doivent être égaux (" + next1 + " diffère de " + next2 + ")");
        }

    }

}
