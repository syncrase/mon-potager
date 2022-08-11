package fr.syncrase.ecosyst.feature.add_plante.consistency.nameconflict;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConflict;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConsistencyService;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ConflictualRank;
import fr.syncrase.ecosyst.feature.add_plante.consistency.InconsistencyResolverException;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchRepository;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistReader;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistWriter;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = MonolithApp.class)
public class RankNameConflictTest {

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
    public void checkConsistency_conflictOnTheOrder() throws ClassificationReconstructionException, MoreThanOneResultException, InconsistencyResolverException {

        // Règne 	Plantae
        //Sous-règne 	Tracheobionta
        //Division 	Magnoliophyta
        //Classe 	Magnoliopsida
        //Sous-classe 	Hamamelidae
        //Ordre 	Saxifragales
        //Famille 	Hamamelidaceae
        //Genre Corylopsis
        CronquistClassificationBranch corylopsisClassification = cronquistWriter.saveClassification(ClassificationBranchRepository.CORYLOPSIS.getClassification());

        // Règne 	Plantae
        //Sous-règne 	Tracheobionta
        //Division 	Magnoliophyta
        //Classe 	Magnoliopsida
        //Sous-classe 	Hamamelidae
        //Ordre 	Hamamelidales
        //Famille 	Hamamelidaceae
        //Genre Distylium
        ClassificationConflict distyliumConflicts = classificationConsistencyService.getSynchronizedClassificationAndConflicts(ClassificationBranchRepository.DISTYLIUM.getClassification());

        Assertions.assertEquals(1, distyliumConflicts.getConflictedClassifications().size(), "Il doit exister un conflit");
        Optional<ConflictualRank> conflictualRank = distyliumConflicts.getConflictedClassifications().stream().findFirst();
        if (conflictualRank.isPresent()) {
            Assertions.assertEquals(conflictualRank.get().getExisting().getRank(), conflictualRank.get().getScraped().getRank(), "Les rangs en conflit doivent être du même rang taxonomique");
            Assertions.assertEquals(CronquistTaxonomicRank.ORDRE, conflictualRank.get().getScraped().getRank(), "Ce sont les ordres qui sont censés entrer en conflit");
        } else {
            fail();
        }

        ClassificationConflict resolvedDistyliumConflicts = classificationConsistencyService.resolveInconsistency(distyliumConflicts);
        Assertions.assertEquals(0, resolvedDistyliumConflicts.getConflictedClassifications().size(), "Le conflit doit avoir été résolu");

        // Resynchronisation avec la base
        ClassificationConflict synchronizedResolvedDistyliumConflicts = classificationConsistencyService.getSynchronizedClassificationAndConflicts(resolvedDistyliumConflicts.getNewClassification());
        Assertions.assertEquals(0, synchronizedResolvedDistyliumConflicts.getConflictedClassifications().size(), "Le conflit doit avoir été résolu");
        Assertions.assertEquals("Hamamelidales", synchronizedResolvedDistyliumConflicts.getNewClassification().getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Le nom attendu de l'ordre est \"Hamamelidales\"");

        // Vérification que corylopsisClassification a changé après la résolution du conflit
        CronquistClassificationBranch corylopsisClassificationAfterResolvingNameConflict = cronquistReader.findExistingClassification(corylopsisClassification.getLowestRank());
        Assertions.assertNotNull(corylopsisClassificationAfterResolvingNameConflict, "La classification doit exister");
        Assertions.assertEquals("Hamamelidales", corylopsisClassificationAfterResolvingNameConflict.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Le nom attendu de l'ordre est \"Hamamelidales\"");

    }
}
