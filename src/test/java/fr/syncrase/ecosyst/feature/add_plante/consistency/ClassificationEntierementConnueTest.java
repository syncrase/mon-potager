package fr.syncrase.ecosyst.feature.add_plante.consistency;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchRepository;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistWriter;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void tearDown() {
        cronquistWriter.removeAll();
    }

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
        ClassificationConflict conflicts = classificationConsistencyService.getSynchronizedClassificationAndConflicts(ClassificationBranchRepository.ALLIUM.getClassification());
        Assertions.assertEquals(0, conflicts.getConflictedClassifications().size(), "La classification conflictuel ne doit contenir aucun conflit");
        Assertions.assertNotNull(conflicts.getNewClassification().getRang(CronquistTaxonomicRank.REGNE).getId(), "Le règne doit avoir été récupéré de la base de données");
        Assertions.assertEquals("Plantae", conflicts.getNewClassification().getRang(CronquistTaxonomicRank.REGNE).getNom(), "Le règne doit être Plantae");
        Assertions.assertNotNull(conflicts.getNewClassification().getRang(CronquistTaxonomicRank.SOUSREGNE).getId(), "Le sous-règne doit avoir été récupéré de la base de données");
        Assertions.assertEquals("Tracheobionta", conflicts.getNewClassification().getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Le sous-règne doit être Tracheobionta");
        Assertions.assertNotNull(conflicts.getNewClassification().getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getId(), "L'embranchement doit avoir été récupéré de la base de données");
        Assertions.assertEquals("Magnoliophyta", conflicts.getNewClassification().getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "L'embranchement doit être Magnoliophyta");


        /*
         * Enregistrement une seconde fois de cet autre objet
         */
        CronquistClassificationBranch secondCronquistClassificationBranch = cronquistWriter.saveClassification(conflicts.getNewClassification());

        Assertions.assertEquals(firstCronquistClassificationBranch.size(), secondCronquistClassificationBranch.size(), "Les deux classifications doivent avoir la même taille");
        Iterator<CronquistRank> iterator1 = firstCronquistClassificationBranch.iterator();
        Iterator<CronquistRank> iterator2 = secondCronquistClassificationBranch.iterator();
        while (iterator1.hasNext() && iterator2.hasNext()) {
            CronquistRank next1 = iterator1.next();
            CronquistRank next2 = iterator2.next();
            Assertions.assertEquals(next1.getId(), next2.getId(), "Les IDs de chacun des éléments de la classification doivent être égaux (" + next1 + " diffère de " + next2 + ")");
            Assertions.assertEquals(next1.getRank(), next2.getRank(), "Les deux rangs doivent être du même rang taxonomique (" + next1 + " diffère de " + next2 + ")");
            Assertions.assertEquals(next1.getParent(), next2.getParent(), "Les deux rangs doivent posséder le même parent (" + next1 + " diffère de " + next2 + ")");
            Assertions.assertEquals(next1.getChildren(), next2.getChildren(), "Les deux rangs doivent posséder les mêmes enfants (" + next1 + " diffère de " + next2 + ")");
            Assertions.assertEquals(next1.getNom(), next2.getNom(), "Les deux rangs doivent posséder le même nom (" + next1 + " diffère de " + next2 + ")");
        }

    }

}
