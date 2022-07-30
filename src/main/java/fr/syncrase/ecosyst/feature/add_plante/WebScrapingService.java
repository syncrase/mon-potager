package fr.syncrase.ecosyst.feature.add_plante;

import fr.syncrase.ecosyst.domain.NomVernaculaire;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.WikipediaCrawler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
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

    public Plante scrapPlant(String name) throws IOException {
        // Find the url describing this plant name
        String url = getTheWikipediaUrl(name);
        WikipediaCrawler wikipediaCrawler = new WikipediaCrawler();
        CronquistClassificationBranch cronquistClassificationBranch = wikipediaCrawler.extractClassificationFromWiki(url);

        return new Plante().addNomsVernaculaires(new NomVernaculaire().nom(name));
    }

    @Contract(pure = true)
    private @NotNull String getTheWikipediaUrl(String name) {
        return "https://fr.wikipedia.org/wiki/Corylopsis";
    }
}
