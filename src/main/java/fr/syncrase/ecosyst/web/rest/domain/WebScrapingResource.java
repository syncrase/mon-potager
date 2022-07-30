package fr.syncrase.ecosyst.web.rest.domain;

import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.service.scraper.WebScrapingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        Plante plante = webScrapingService.scrapPlantData(name);
        return ResponseEntity.ok().body(plante);
    }

}
