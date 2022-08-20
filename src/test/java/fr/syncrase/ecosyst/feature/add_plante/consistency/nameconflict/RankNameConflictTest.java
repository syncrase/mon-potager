package fr.syncrase.ecosyst.feature.add_plante.consistency.nameconflict;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConflict;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ConflictualRank;
import fr.syncrase.ecosyst.feature.add_plante.consistency.CronquistConsistencyService;
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
public class RankNameConflictTest {

    @Autowired
    CronquistReader cronquistReader;

    @Autowired
    CronquistWriter cronquistWriter;

    @Autowired
    private CronquistConsistencyService cronquistConsistencyService;

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
        //Ordre 	Saxifragales (conflit : ce nom n'existe pas en cronquist)
        //Famille 	Hamamelidaceae
        //Genre Corylopsis
        CronquistClassificationBranch corylopsisClassification = cronquistWriter.save(ClassificationBranchMockRepository.CORYLOPSIS.getClassification());

        // Règne 	Plantae
        //Sous-règne 	Tracheobionta
        //Division 	Magnoliophyta
        //Classe 	Magnoliopsida
        //Sous-classe 	Hamamelidae
        //Ordre 	Hamamelidales
        //Famille 	Hamamelidaceae
        //Genre Distylium
        ClassificationConflict distyliumConflicts = cronquistConsistencyService.getSynchronizedClassificationAndConflicts(ClassificationBranchMockRepository.DISTYLIUM.getClassification());
        assertsAfterSynchronization(distyliumConflicts);

        ClassificationConflict resolvedDistyliumConflicts = cronquistConsistencyService.resolveInconsistencyInDatabase(distyliumConflicts);
        assertsAfterResolution(corylopsisClassification, resolvedDistyliumConflicts);

        // Resynchronisation avec la base
        ClassificationConflict synchronizedResolvedDistyliumConflicts = cronquistConsistencyService.getSynchronizedClassificationAndConflicts(resolvedDistyliumConflicts.getNewClassification());
        Assertions.assertEquals(0, synchronizedResolvedDistyliumConflicts.getConflictedClassifications().size(), "Le conflit doit avoir été résolu");
        Assertions.assertEquals("hamamelidales", synchronizedResolvedDistyliumConflicts.getNewClassification().getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Le nom attendu de l'ordre est \"Hamamelidales\"");
        Assertions.assertNotNull(synchronizedResolvedDistyliumConflicts.getNewClassification().getRang(CronquistTaxonomicRank.ORDRE).getId(), "L'id de Saxifragales doit se retrouver dans Hamamelidales pour que le mise à jour du nom soit faite");


    }

    private void assertsAfterSynchronization(@NotNull ClassificationConflict distyliumConflicts) {
        Assertions.assertEquals(1, distyliumConflicts.getConflictedClassifications().size(), "Il doit exister un conflit");
        Optional<ConflictualRank> conflictualRank = distyliumConflicts.getConflictedClassifications().stream().findFirst();
        if (conflictualRank.isPresent()) {
            Assertions.assertEquals(conflictualRank.get().getExisting().getRank(), conflictualRank.get().getScraped().getRank(), "Les rangs en conflit doivent être du même rang taxonomique");
            Assertions.assertEquals(CronquistTaxonomicRank.ORDRE, conflictualRank.get().getScraped().getRank(), "Ce sont les ordres qui sont censés entrer en conflit");
        } else {
            fail();
        }
    }

    private void assertsAfterResolution(@NotNull CronquistClassificationBranch corylopsisClassification, @NotNull ClassificationConflict resolvedDistyliumConflicts) throws MoreThanOneResultException {
        Assertions.assertEquals(0, resolvedDistyliumConflicts.getConflictedClassifications().size(), "Le conflit doit avoir été résolu");

        // Vérification que corylopsisClassification a changé après la résolution du conflit
        CronquistClassificationBranch corylopsisClassificationAfterResolvingNameConflict = cronquistReader.findExistingClassification(corylopsisClassification.getLowestRank());
        Assertions.assertNotNull(corylopsisClassificationAfterResolvingNameConflict, "La classification doit exister");
        Assertions.assertEquals("hamamelidales", corylopsisClassificationAfterResolvingNameConflict.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Le nom attendu de l'ordre est \"Hamamelidales\"");
    }
}
