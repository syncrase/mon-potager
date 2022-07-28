package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Feuillage;
import fr.syncrase.ecosyst.repository.FeuillageRepository;
import fr.syncrase.ecosyst.service.FeuillageQueryService;
import fr.syncrase.ecosyst.service.FeuillageService;
import fr.syncrase.ecosyst.service.criteria.FeuillageCriteria;
import fr.syncrase.ecosyst.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Feuillage}.
 */
@RestController
@RequestMapping("/api")
public class FeuillageResource {

    private final Logger log = LoggerFactory.getLogger(FeuillageResource.class);

    private static final String ENTITY_NAME = "feuillage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FeuillageService feuillageService;

    private final FeuillageRepository feuillageRepository;

    private final FeuillageQueryService feuillageQueryService;

    public FeuillageResource(
        FeuillageService feuillageService,
        FeuillageRepository feuillageRepository,
        FeuillageQueryService feuillageQueryService
    ) {
        this.feuillageService = feuillageService;
        this.feuillageRepository = feuillageRepository;
        this.feuillageQueryService = feuillageQueryService;
    }

    /**
     * {@code POST  /feuillages} : Create a new feuillage.
     *
     * @param feuillage the feuillage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new feuillage, or with status {@code 400 (Bad Request)} if the feuillage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/feuillages")
    public ResponseEntity<Feuillage> createFeuillage(@RequestBody Feuillage feuillage) throws URISyntaxException {
        log.debug("REST request to save Feuillage : {}", feuillage);
        if (feuillage.getId() != null) {
            throw new BadRequestAlertException("A new feuillage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Feuillage result = feuillageService.save(feuillage);
        return ResponseEntity
            .created(new URI("/api/feuillages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /feuillages/:id} : Updates an existing feuillage.
     *
     * @param id the id of the feuillage to save.
     * @param feuillage the feuillage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feuillage,
     * or with status {@code 400 (Bad Request)} if the feuillage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the feuillage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/feuillages/{id}")
    public ResponseEntity<Feuillage> updateFeuillage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Feuillage feuillage
    ) throws URISyntaxException {
        log.debug("REST request to update Feuillage : {}, {}", id, feuillage);
        if (feuillage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feuillage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!feuillageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Feuillage result = feuillageService.update(feuillage);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, feuillage.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /feuillages/:id} : Partial updates given fields of an existing feuillage, field will ignore if it is null
     *
     * @param id the id of the feuillage to save.
     * @param feuillage the feuillage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feuillage,
     * or with status {@code 400 (Bad Request)} if the feuillage is not valid,
     * or with status {@code 404 (Not Found)} if the feuillage is not found,
     * or with status {@code 500 (Internal Server Error)} if the feuillage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/feuillages/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Feuillage> partialUpdateFeuillage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Feuillage feuillage
    ) throws URISyntaxException {
        log.debug("REST request to partial update Feuillage partially : {}, {}", id, feuillage);
        if (feuillage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feuillage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!feuillageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Feuillage> result = feuillageService.partialUpdate(feuillage);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, feuillage.getId().toString())
        );
    }

    /**
     * {@code GET  /feuillages} : get all the feuillages.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of feuillages in body.
     */
    @GetMapping("/feuillages")
    public ResponseEntity<List<Feuillage>> getAllFeuillages(
        FeuillageCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Feuillages by criteria: {}", criteria);
        Page<Feuillage> page = feuillageQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /feuillages/count} : count all the feuillages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/feuillages/count")
    public ResponseEntity<Long> countFeuillages(FeuillageCriteria criteria) {
        log.debug("REST request to count Feuillages by criteria: {}", criteria);
        return ResponseEntity.ok().body(feuillageQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /feuillages/:id} : get the "id" feuillage.
     *
     * @param id the id of the feuillage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the feuillage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/feuillages/{id}")
    public ResponseEntity<Feuillage> getFeuillage(@PathVariable Long id) {
        log.debug("REST request to get Feuillage : {}", id);
        Optional<Feuillage> feuillage = feuillageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(feuillage);
    }

    /**
     * {@code DELETE  /feuillages/:id} : delete the "id" feuillage.
     *
     * @param id the id of the feuillage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/feuillages/{id}")
    public ResponseEntity<Void> deleteFeuillage(@PathVariable Long id) {
        log.debug("REST request to delete Feuillage : {}", id);
        feuillageService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
