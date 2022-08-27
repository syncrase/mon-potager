package fr.syncrase.ecosyst.feature.add_plante;

import fr.syncrase.ecosyst.domain.NomVernaculaire;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedPlant;
import fr.syncrase.ecosyst.feature.add_plante.repository.PlanteReader;
import fr.syncrase.ecosyst.feature.add_plante.repository.PlanteWriter;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.UnableToSaveClassificationException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.WebScrapingService;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.NonExistentWikiPageException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.PlantNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing the web scrapping.
 */
@RestController
@RequestMapping("/api")
public class AddPlanteResource {

    private final Logger log = LoggerFactory.getLogger(AddPlanteResource.class);

    private final WebScrapingService webScrapingService;

    private final PlanteReader planteReader;

    private final PlanteWriter planteWriter;

    public AddPlanteResource(
        WebScrapingService webScrapingService,
        PlanteReader planteReader,
        PlanteWriter planteWriter) {
        this.webScrapingService = webScrapingService;
        this.planteReader = planteReader;
        this.planteWriter = planteWriter;
    }

    /**
     * {@code GET  /plantes/search} : First search the provided name in the nom vernaculaire table :
     * <ul>
     *     <li><b>if exists</b> : Search associated plants and return List\<ScrapedPlant\></li>
     *     <li><b>if not exists</b> : Trying to scrap wikipedia
     *     <ul>
     *         <li><b>if scrap succeed</b> : Send back ScrapedPlant</li>
     *         <li><b>if scrap fail</b> : not found status</li>
     *     </ul>
     *     </li>
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} resulting plante.
     */
    @GetMapping("/plantes/search")
    public ResponseEntity<List<ScrapedPlant>> scrapPlant(@RequestParam String name) {
        log.debug("REST request to look for {}", name);
        List<Plante> plantesByCriteria = planteReader.findPlantes(name);
        if (plantesByCriteria.size() == 1) {
            // Eagerly Load plante
            Plante plante = planteReader.eagerLoad(plantesByCriteria.get(0));
            return ResponseEntity.ok().body(List.of(new ScrapedPlant(plante)));
        }
        if (plantesByCriteria.size() > 1) {
            // Map plante to ScrapedPlante
            return ResponseEntity.ok().body(
                plantesByCriteria.stream().map(ScrapedPlant::new).collect(Collectors.toList())
            );
        }

        // plantesByCriteria.size() == 1 : true
        ScrapedPlant plante;
        try {
            plante = webScrapingService.scrapPlant(name);
        } catch (NonExistentWikiPageException | PlantNotFoundException e) {
            log.error("Impossible de scraper la plante {}", name);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(List.of(plante));
    }

    /**
     * {@code GET  /plantes/search} : Scrap information on the internet (Wikipédia) about the plante and send back the resulting plant
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} resulting plante.
     */
    @PostMapping("/plantes/save")
    public ResponseEntity<Plante> savePlant(@RequestBody @NotNull ScrapedPlant plante) {
        log.debug("REST request to save Plante : {}",
            plante.getPlante().getNomsVernaculaires() != null ?
                plante.getPlante().getNomsVernaculaires().stream().map(NomVernaculaire::getNom).collect(Collectors.joining(", ")) :
                "no name"
        );

        Plante savedPlante;
        try {
            savedPlante = planteWriter.saveScrapedPlante(plante.getPlante());
        } catch (UnableToSaveClassificationException e) {
            log.warn("Impossible de sauvegarder la classification");
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(savedPlante);

    }


}
