package fr.syncrase.ecosyst.feature.add_plante;

import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedPlant;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exceptions.NonExistentWikiPageException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exceptions.PlantNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * REST controller for managing the web scrapping.
 */
@RestController
@RequestMapping("/api")
public class WebScrapingResource {

    private final Logger log = LoggerFactory.getLogger(WebScrapingResource.class);

    private final WebScrapingService webScrapingService;

    public WebScrapingResource(WebScrapingService webScrapingService) {
        this.webScrapingService = webScrapingService;
    }

    /**
     * {@code GET  /plantes/search} : Scrap information on the internet (Wikipédia) about the plante and send back the resulting plant
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} resulting plante.
     */
    @GetMapping("/plantes/scrap")
    public ResponseEntity<ScrapedPlant> scrapPlant(@RequestParam String name)  {
        log.debug("REST request to look for {} on the internet", name);
        ScrapedPlant plante = null;
        try {
            plante = webScrapingService.scrapPlant(name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NonExistentWikiPageException | PlantNotFoundException e) {
            log.warn("{}: Unable to find a wiki page for {}", e.getClass(), name);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(plante);
    }

}
