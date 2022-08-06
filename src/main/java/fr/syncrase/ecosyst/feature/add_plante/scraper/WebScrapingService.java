package fr.syncrase.ecosyst.feature.add_plante.scraper;

import fr.syncrase.ecosyst.domain.NomVernaculaire;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedPlant;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.WikipediaResolver;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.NonExistentWikiPageException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.WikipediaScraper;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.PlantNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service for scrap {@link Plante} on the internet
 */
@Service
public class WebScrapingService {

    private final Logger log = LoggerFactory.getLogger(WebScrapingService.class);

    public ScrapedPlant scrapPlant(String name) throws IOException, NonExistentWikiPageException, PlantNotFoundException {

        // Find the url describing this plant name
        log.info("Trying to resolve the wikipedia url for {}", name);
        WikipediaResolver wikipediaResolver = new WikipediaResolver();
        String url = wikipediaResolver.resolveUrlForThisPlant(name);

        // Scrap data from this URL
        CronquistClassificationBranch cronquistClassificationBranch = null;
        if (url != null) {
            log.info("Found url is {}", url);
            WikipediaScraper wikipediaScraper = new WikipediaScraper();
            cronquistClassificationBranch = wikipediaScraper.extractClassificationFromWiki(url);
        } else {
            throw new PlantNotFoundException(name);
        }

        assert cronquistClassificationBranch != null;
        ScrapedPlant plante = new ScrapedPlant()
            .addNomsVernaculaires(new NomVernaculaire().nom(name))
            .cronquistClassificationBranch(cronquistClassificationBranch);
        return plante;
    }

}
