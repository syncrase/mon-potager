package fr.syncrase.ecosyst.feature.add_plante.consistency.conflict_and_merge;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConflict;
import fr.syncrase.ecosyst.feature.add_plante.consistency.CronquistConsistencyService;
import fr.syncrase.ecosyst.feature.add_plante.consistency.InconsistencyResolverException;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchMockRepository;
import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedPlant;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistReader;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistWriter;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.WebScrapingService;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.NonExistentWikiPageException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.PlantNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank.DOMAINE;
import static fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank.FAMILLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(classes = MonolithApp.class)
public class DeleteObsoleteClassificationTest {

    @Autowired
    CronquistWriter cronquistWriter;

    @Autowired
    CronquistReader cronquistReader;

    @Autowired
    private CronquistConsistencyService cronquistConsistencyService;

    @Autowired
    private WebScrapingService webScrapingService;

    @AfterEach
    void tearDown() {
        cronquistWriter.removeAll();
    }

    @Test
    public void checkConsistency_wrongNameConflictedRightName_checkParenthood() throws ClassificationReconstructionException, MoreThanOneResultException, InconsistencyResolverException, NonExistentWikiPageException, PlantNotFoundException {
        /*
         * Règne 	    Plantae
         * Sous-règne 	Tracheobionta
         * Division 	Magnoliophyta
         * Classe 	    Liliopsida
         * Sous-classe 	Liliidae
         * Ordre 	    Liliales
         * Famille 	    CeRangNExistePas
         * Genre        Allium
         */
        CronquistClassificationBranch classification = ClassificationBranchMockRepository.ALLIUM.getClassification();
        classification.add(new CronquistRank().rank(FAMILLE).nom("CeRangNExistePas"));
        CronquistClassificationBranch allium0 = cronquistWriter.save(classification);

        List<Long> tousLesIdsDeLaFamilleInclueJusquaLOrdreExclu = allium0.subSet(
            allium0.getRang(FAMILLE),
            allium0.getRang(CronquistTaxonomicRank.ORDRE)
        ).stream().map(CronquistRank::getId).collect(Collectors.toList());
        assertEquals(5, tousLesIdsDeLaFamilleInclueJusquaLOrdreExclu.size(), "Il doit y avoir 5 ids dans la liste des ids à supprimer");
        /*
         * Règne 	    Plantae
         * Sous-règne 	Tracheobionta
         * Division 	Magnoliophyta
         * Classe 	    Liliopsida
         * Sous-classe 	Liliidae
         * Ordre 	    Liliales
         * Famille 	    Liliaceae
         * Genre        Allium
         */
        CronquistClassificationBranch allium1 = ClassificationBranchMockRepository.ALLIUM.getClassification();
        ScrapedPlant mockedLiliaceae = new ScrapedPlant()
            .cronquistClassificationBranch(
                new CronquistClassificationBranch(
                    allium1.subSet(
                        allium1.getRang(FAMILLE), true,
                        allium1.getRang(DOMAINE), true
                    )
                )
            );
        Mockito.when(webScrapingService.scrapPlant("liliaceae"))
            .thenReturn(
                mockedLiliaceae
            );
        ClassificationConflict allium1AfterSynchronization = cronquistConsistencyService.getSynchronizedClassificationAndConflicts(allium1);
        Assertions.assertEquals(1, allium1AfterSynchronization.getConflictedClassifications().size(), "Il doit y avoir un conflit");

        ClassificationConflict allium1AfterConflictResolving = cronquistConsistencyService.resolveInconsistencyInDatabase(allium1AfterSynchronization);
        Assertions.assertEquals(0, allium1AfterConflictResolving.getConflictedClassifications().size(), "Il ne doit plus y avoir de conflit");

        ClassificationConflict allium1AfterReSynchronization = cronquistConsistencyService.getSynchronizedClassificationAndConflicts(allium1AfterConflictResolving.getNewClassification());

        CronquistClassificationBranch savedAllium1 = cronquistWriter.save(allium1AfterReSynchronization.getNewClassification());
        assertParenthoodConsistency(savedAllium1);

    }

    private static void assertParenthoodConsistency(@NotNull CronquistClassificationBranch savedAllium1) {
        for (CronquistRank cronquistRank : savedAllium1) {
            if (!cronquistRank.getRank().equals(CronquistTaxonomicRank.DOMAINE)) {
                Assertions.assertNotNull(cronquistRank.getParent(), cronquistRank + " doit exister un parent");
            }
            if (!cronquistRank.getRank().equals(CronquistTaxonomicRank.SOUSVARIETE)) {
                Assertions.assertNotNull(cronquistRank.getChildren(), cronquistRank + " doit exister un enfant");
            }
        }
    }

}
