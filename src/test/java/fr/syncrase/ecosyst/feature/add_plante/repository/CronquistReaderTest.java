package fr.syncrase.ecosyst.feature.add_plante.repository;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchRepository;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MonolithApp.class)
class CronquistReaderTest {


    @Autowired
    CronquistWriter cronquistWriter;

    @Autowired
    CronquistReader cronquistReader;

    @Test
    void findExistingPartOfThisClassification() throws ClassificationReconstructionException, MoreThanOneResultException {
    }

    @Test
    void findExistingClassification() {
    }

    @Test
    void findExistingRank_doNotReturnAnInexistantRank() throws MoreThanOneResultException {
        CronquistClassificationBranch cronquistClassificationBranch = cronquistWriter.saveClassification(ClassificationBranchRepository.ALLIUM.getClassification());

        CronquistRank lowestRank = ClassificationBranchRepository.ALDROVANDA.getClassification().getLowestRank();
        @Nullable CronquistRank existingClassification = cronquistReader.findExistingRank(lowestRank);

        Assertions.assertNull(existingClassification, "Aucun ne doit être retourné");
    }

    @Test
    void queryForCronquistRank() {
    }
}
