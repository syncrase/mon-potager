package fr.syncrase.ecosyst.feature.add_plante;

import fr.syncrase.ecosyst.domain.NomVernaculaire;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedPlant;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.WikipediaResolver;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exceptions.NonExistentWikiPageException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.WikipediaCrawler;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exceptions.PlantNotFoundException;
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
        WikipediaResolver wikipediaResolver = new WikipediaResolver();
        String url = wikipediaResolver.resolveUrlForThisPlant(name);

        // Scrap data from this URL
        CronquistClassificationBranch cronquistClassificationBranch = null;
        if (url != null) {
            WikipediaCrawler wikipediaCrawler = new WikipediaCrawler();
            cronquistClassificationBranch = wikipediaCrawler.extractClassificationFromWiki(url);
        } else {
            throw new PlantNotFoundException(name);
        }

        assert cronquistClassificationBranch != null;
        ScrapedPlant plante = new ScrapedPlant()
            .addNomsVernaculaires(new NomVernaculaire().nom(name))
            .lowestClassificationRanks(cronquistClassificationBranch.getClassificationSet());
        return plante;
    }

}
