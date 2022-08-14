package fr.syncrase.ecosyst.feature.add_plante.repository;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchMockRepository;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void tearDown() {
        cronquistWriter.removeAll();
    }

    @Test
    void findExistingPartOfThisClassification() throws ClassificationReconstructionException, MoreThanOneResultException {
    }

    @Test
    void findExistingClassification() {
    }

    @Test
    void findExistingRank_doNotReturnAnInexistantRank() throws MoreThanOneResultException {
        CronquistClassificationBranch alliumClassification = cronquistWriter.saveClassification(ClassificationBranchMockRepository.ALLIUM.getClassification());

        CronquistRank lowestRank = ClassificationBranchMockRepository.ALDROVANDA.getClassification().getLowestRank();
        @Nullable CronquistRank existingClassification = cronquistReader.findExistingRank(lowestRank);

        Assertions.assertNull(existingClassification, lowestRank + " ne doit pas être retourné car n'a pas été enregistré");


        existingClassification = cronquistReader.findExistingRank(ClassificationBranchMockRepository.ALLIUM.getClassification().getLowestRank());
        Assertions.assertNotNull(existingClassification, "La classification qui vient juste d'être enregistrée doit être disponible en base");

    }

    @Test
    void queryForCronquistRank() {
    }
}
