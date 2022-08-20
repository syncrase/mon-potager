package fr.syncrase.ecosyst.feature.add_plante.mocks;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedPlant;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistWriter;
import fr.syncrase.ecosyst.feature.add_plante.scraper.WebScrapingService;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.NonExistentWikiPageException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.PlantNotFoundException;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = MonolithApp.class)
public class JsonMockGeneratorTest {

    @Autowired
    private WebScrapingService webScrapingService;// Just for generate json

    @Autowired
    CronquistWriter cronquistWriter;

    @Test
    void getJsonObject() throws IOException, NonExistentWikiPageException, PlantNotFoundException {
        @Nullable ScrapedPlant plante = webScrapingService.scrapPlant("%C3%89rable_de_Miyabe");
        // TODO extract as json in order to reuse it without send any request
        if (plante != null) {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String classificationSet = ow.writeValueAsString(plante.getCronquistClassificationBranch().getClassificationSet());

            TreeSet<CronquistRank> classification = new ObjectMapper().readValue(classificationSet,
                new TypeReference<TreeSet<CronquistRank>>() {
                });
            classification.removeIf(cronquistRank -> true);
        } else {
            fail();
        }
    }

}
