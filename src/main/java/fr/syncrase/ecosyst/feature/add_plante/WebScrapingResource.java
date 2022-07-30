package fr.syncrase.ecosyst.feature.add_plante;

import fr.syncrase.ecosyst.domain.Plante;
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
    public ResponseEntity<Plante> scrapPlant(@RequestParam String name) {
        log.debug("REST request to look for {} on the internet", name);
        Plante plante = null;
        try {
            plante = webScrapingService.scrapPlant(name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().body(plante);
    }

}
