package fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia;

import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.NonExistentWikiPageException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WikipediaResolverTest {

    WikipediaResolver wr = new WikipediaResolver();

    @Test
    void resolveUrlForThisPlant_allium() throws NonExistentWikiPageException {
        String allium = wr.resolveUrlForThisPlant("allium");
        Assertions.assertNotNull(allium, "L'url doit être retrouvé");
        Assertions.assertEquals("https://fr.wikipedia.org/wiki/Allium", allium, "L'url n'est pas la bonne");
    }

    @Test
    void resolveUrlForThisPlant_hamamelidales() throws NonExistentWikiPageException {
        String allium = wr.resolveUrlForThisPlant("Hamamelidales");
        Assertions.assertNotNull(allium, "L'url doit être retrouvé");
        Assertions.assertEquals("https://fr.wikipedia.org/wiki/Hamamelidales", allium, "L'url n'est pas la bonne");
    }
}
