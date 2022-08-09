package fr.syncrase.ecosyst.feature.add_plante;

import fr.syncrase.ecosyst.domain.NomVernaculaire;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConflict;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConsistencyService;
import fr.syncrase.ecosyst.feature.add_plante.consistency.InconsistencyResolverException;
import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedPlant;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistWriter;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.WebScrapingService;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.NonExistentWikiPageException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.PlantNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * REST controller for managing the web scrapping.
 */
@RestController
@RequestMapping("/api")
public class AddPlanteResource {

    private final Logger log = LoggerFactory.getLogger(AddPlanteResource.class);

    private final WebScrapingService webScrapingService;
    private final ClassificationConsistencyService classificationConsistencyService;

    private final CronquistWriter cronquistWriter;

    public AddPlanteResource(WebScrapingService webScrapingService, ClassificationConsistencyService classificationConsistencyService, CronquistWriter cronquistWriter) {
        this.webScrapingService = webScrapingService;
        this.classificationConsistencyService = classificationConsistencyService;
        this.cronquistWriter = cronquistWriter;
    }

    /**
     * {@code GET  /plantes/search} : Scrap information on the internet (Wikipédia) about the plante and send back the resulting plant
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} resulting plante.
     */
    @GetMapping("/plantes/scrap")
    public ResponseEntity<ScrapedPlant> scrapPlant(@RequestParam String name) {
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

    /**
     * {@code GET  /plantes/search} : Scrap information on the internet (Wikipédia) about the plante and send back the resulting plant
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} resulting plante.
     */
    @PostMapping("/plantes/save")
    public ResponseEntity<ScrapedPlant> savePlant(@RequestBody @NotNull ScrapedPlant plante) {
        log.debug("REST request to save Plante : {}", plante.getNomsVernaculaires().stream().map(NomVernaculaire::getNom).collect(Collectors.joining(", ")));


        /*
         * Accès en read only
         */
        CronquistClassificationBranch toSaveCronquistClassification = new CronquistClassificationBranch(plante.getCronquistClassificationBranch());
        ClassificationConflict conflicts;
        try {
            conflicts = classificationConsistencyService.getSynchronizedClassificationAndConflicts(toSaveCronquistClassification);
        } catch (ClassificationReconstructionException e) {
            log.warn("{}: Unable to construct a classification for {}", e.getClass(), plante.getCronquistClassificationBranch().last().getNom());
            return ResponseEntity.internalServerError().build();
        } catch (MoreThanOneResultException e) {
            log.warn("{}: the rank name {} seems to be in an inconsistent state in the database. More than one was found.", e.getClass(), plante.getCronquistClassificationBranch().last().getNom());
            return ResponseEntity.internalServerError().build();
        }

        /*
         * Modification de la base pour qu'il n'y ait plus de conflit possible
         */
        ClassificationConflict resolvedConflicts = null;
        if (conflicts.getConflictedClassifications().size() == 0) {
            try {
                resolvedConflicts = classificationConsistencyService.resolveInconsistency(conflicts);
                resolvedConflicts = classificationConsistencyService.getSynchronizedClassificationAndConflicts(resolvedConflicts.getNewClassification());
            } catch (InconsistencyResolverException | MoreThanOneResultException e) {
                log.warn("{}: Unable to construct a classification for {}", e.getClass(), plante.getCronquistClassificationBranch().last().getNom());
                return ResponseEntity.internalServerError().build();
            } catch (ClassificationReconstructionException e) {
                throw new RuntimeException(e);
            }
        } else {
            resolvedConflicts = conflicts;
        }



        /*
         * Enregistrement de la nouvelle classification
         */
        if (resolvedConflicts.getConflictedClassifications().size() == 0) {
            CronquistClassificationBranch savedClassification = cronquistWriter.saveClassification(conflicts.getNewClassification());
            plante.setCronquistClassificationBranch(savedClassification);
        }
        //else {
        //    // Il y a encore un conflit qui n'a pas été résolu. Je le renvoie à l'utilisateur
        //}
        return ResponseEntity.ok().body(plante);

    }

}
