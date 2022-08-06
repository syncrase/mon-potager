package fr.syncrase.ecosyst.feature.add_plante.consistency;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomikRanks;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchRepository;
import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedPlant;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.WebScrapingService;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.NonExistentWikiPageException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.PlantNotFoundException;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.TreeSet;

@SpringBootTest(classes = MonolithApp.class)
class ClassificationConsistencyServiceTest {

    @Autowired
    private ClassificationConsistencyService classificationConsistencyService;

    @Autowired
    private WebScrapingService webScrapingService;// Just for generate json


    @Test
    void getJsonObject() throws IOException, NonExistentWikiPageException, PlantNotFoundException {
        @Nullable ScrapedPlant plante = webScrapingService.scrapPlant("allium");
        // TODO extract as json in order to reuse it without send any request
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String classificationSet = ow.writeValueAsString(plante.getCronquistClassificationBranch().getClassificationSet());

        TreeSet<CronquistRank> classification = new ObjectMapper().readValue(classificationSet, new TypeReference<TreeSet<CronquistRank>>() {
        });
        classification = null;
    }


    @Test
    void checkConsistency() throws ClassificationReconstructionException, MoreThanOneResultException, IOException {

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
