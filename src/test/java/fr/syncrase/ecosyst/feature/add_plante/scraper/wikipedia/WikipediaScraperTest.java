package fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomikRanks;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WikipediaScraperTest {

    WikipediaScraper wikipediaScraper;

    public WikipediaScraperTest() {
        this.wikipediaScraper = new WikipediaScraper();
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void extractClassificationFromWiki() {
    }

    @Test
    public void uneErreurEvidenteDansLaClassificationNEstPasPriseEnCompte() {
        // Le genre contient Gentianaceae mais c'est une famille, le genre est ignoré (à voir avec le scraper)
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Monodiella";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        String nomsDuGenreMonodiella = classification.getRang(CronquistTaxonomikRanks.GENRE).getNom();
        Assertions.assertTrue(nomsDuGenreMonodiella.contains("Monodiella"), "Le genre monodiella doit être 'Monodiella' pas 'Gentianaceae'");
        CronquistRank lowestRank = classification.getLowestRank();
        Assertions.assertSame(lowestRank.getRank(), CronquistTaxonomikRanks.GENRE, "Le premier rang doit être un genre");
        Assertions.assertNotNull(lowestRank.getNom(), "Le premier rang doit être un rang significatif");
    }
}
