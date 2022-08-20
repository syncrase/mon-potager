package fr.syncrase.ecosyst.feature.add_plante.scraper;

import fr.syncrase.ecosyst.domain.NomVernaculaire;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.domain.Reference;
import fr.syncrase.ecosyst.domain.Url;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedPlant;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.WikipediaResolver;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.WikipediaScraper;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.NonExistentWikiPageException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.PlantNotFoundException;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for scrap {@link Plante} on the internet
 */
@Service
public class WebScrapingService {

    public static final String WIKI_SEARCH_URL = "https://fr.wikipedia.org/wiki/";
    private final Logger log = LoggerFactory.getLogger(WebScrapingService.class);

    public ScrapedPlant scrapPlant(String name) throws NonExistentWikiPageException, PlantNotFoundException {

        // Find the wikiUrl describing this plant name
        log.info("Trying to resolve the wikipedia wikiUrl for {}", name);
        WikipediaResolver wikipediaResolver = new WikipediaResolver();

        log.info("Search for {} on Wikipedia", name);
        String wikiUrl = WIKI_SEARCH_URL + name;
        Document pageContainingClassification = null;
        if (wikipediaResolver.checkIfPageExists(wikiUrl)) {
            pageContainingClassification = wikipediaResolver.getDocumentIfContainingClassification(wikiUrl);
        }

        // Scrap data from this URL
        log.info("Found wikiUrl is ({}). Trying to extract classification from it", wikiUrl);
        CronquistClassificationBranch cronquistClassificationBranch = getCronquistClassificationFromDocument(pageContainingClassification);
        if (cronquistClassificationBranch != null) {

            ScrapedPlant scrapedPlant = new ScrapedPlant()
                .cronquistClassificationBranch(cronquistClassificationBranch);
            scrapedPlant.getPlante().addNomsVernaculaires(new NomVernaculaire().nom(name));
            scrapedPlant.getPlante().addReferences(new Reference().url(new Url().url(wikiUrl)));
            return scrapedPlant;
        }
        throw new PlantNotFoundException("Impossible de scraper la plante " + name);
    }

    @Nullable
    private CronquistClassificationBranch getCronquistClassificationFromDocument(Document pageContainingClassification) {
        CronquistClassificationBranch cronquistClassificationBranch;
        if (pageContainingClassification != null) {
            WikipediaScraper wikipediaScraper = new WikipediaScraper();
            cronquistClassificationBranch = wikipediaScraper.extractCronquistClassificationFromWiki(pageContainingClassification);
        } else {
            log.info("Wiki page not found");
            return null;
        }
        return cronquistClassificationBranch;
    }

}
