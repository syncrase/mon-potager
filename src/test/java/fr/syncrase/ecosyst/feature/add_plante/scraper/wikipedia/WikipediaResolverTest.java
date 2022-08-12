package fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static fr.syncrase.ecosyst.feature.add_plante.scraper.WebScrapingService.WIKI_SEARCH_URL;

class WikipediaResolverTest {

    WikipediaResolver wr = new WikipediaResolver();

    @Test
    void resolveUrlForThisPlant_allium() {
        boolean allium = wr.checkIfPageExists(WIKI_SEARCH_URL + "allium");
        Assertions.assertTrue(allium, "L'url doit être retrouvé");
    }

    @Test
    void resolveUrlForThisPlant_hamamelidales() {
        boolean allium = wr.checkIfPageExists(WIKI_SEARCH_URL + "Hamamelidales");
        Assertions.assertTrue(allium, "L'url doit être retrouvé");
    }

    @Test
    void checkIfPageExists_Lilianae() {
        boolean allium = wr.checkIfPageExists(WIKI_SEARCH_URL + "Lilianae");
        Assertions.assertTrue(allium, "L'url doit être retrouvé");
    }
}
