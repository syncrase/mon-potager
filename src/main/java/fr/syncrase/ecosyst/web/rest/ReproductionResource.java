package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Reproduction;
import fr.syncrase.ecosyst.repository.ReproductionRepository;
import fr.syncrase.ecosyst.service.ReproductionQueryService;
import fr.syncrase.ecosyst.service.ReproductionService;
import fr.syncrase.ecosyst.service.criteria.ReproductionCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Reproduction}.
 */
@RestController
@RequestMapping("/api")
public class ReproductionResource {

    private final Logger log = LoggerFactory.getLogger(ReproductionResource.class);

    private static final String ENTITY_NAME = "reproduction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReproductionService reproductionService;

    private final ReproductionRepository reproductionRepository;

    private final ReproductionQueryService reproductionQueryService;

    public ReproductionResource(
        ReproductionService reproductionService,
        ReproductionRepository reproductionRepository,
        ReproductionQueryService reproductionQueryService
    ) {
        this.reproductionService = reproductionService;
        this.reproductionRepository = reproductionRepository;
        this.reproductionQueryService = reproductionQueryService;
    }

    /**
     * {@code POST  /reproductions} : Create a new reproduction.
     *
     * @param reproduction the reproduction to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reproduction, or with status {@code 400 (Bad Request)} if the reproduction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/reproductions")
    public ResponseEntity<Reproduction> createReproduction(@RequestBody Reproduction reproduction) throws URISyntaxException {
        log.debug("REST request to save Reproduction : {}", reproduction);
        if (reproduction.getId() != null) {
            throw new BadRequestAlertException("A new reproduction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Reproduction result = reproductionService.save(reproduction);
        return ResponseEntity
            .created(new URI("/api/reproductions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /reproductions/:id} : Updates an existing reproduction.
     *
     * @param id the id of the reproduction to save.
     * @param reproduction the reproduction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reproduction,
     * or with status {@code 400 (Bad Request)} if the reproduction is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reproduction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reproductions/{id}")
    public ResponseEntity<Reproduction> updateReproduction(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Reproduction reproduction
    ) throws URISyntaxException {
        log.debug("REST request to update Reproduction : {}, {}", id, reproduction);
        if (reproduction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reproduction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reproductionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Reproduction result = reproductionService.update(reproduction);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reproduction.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /reproductions/:id} : Partial updates given fields of an existing reproduction, field will ignore if it is null
     *
     * @param id the id of the reproduction to save.
     * @param reproduction the reproduction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reproduction,
     * or with status {@code 400 (Bad Request)} if the reproduction is not valid,
     * or with status {@code 404 (Not Found)} if the reproduction is not found,
     * or with status {@code 500 (Internal Server Error)} if the reproduction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/reproductions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Reproduction> partialUpdateReproduction(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Reproduction reproduction
    ) throws URISyntaxException {
        log.debug("REST request to partial update Reproduction partially : {}, {}", id, reproduction);
        if (reproduction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reproduction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reproductionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Reproduction> result = reproductionService.partialUpdate(reproduction);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reproduction.getId().toString())
        );
    }

    /**
     * {@code GET  /reproductions} : get all the reproductions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reproductions in body.
     */
    @GetMapping("/reproductions")
    public ResponseEntity<List<Reproduction>> getAllReproductions(
        ReproductionCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Reproductions by criteria: {}", criteria);
        Page<Reproduction> page = reproductionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /reproductions/count} : count all the reproductions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/reproductions/count")
    public ResponseEntity<Long> countReproductions(ReproductionCriteria criteria) {
        log.debug("REST request to count Reproductions by criteria: {}", criteria);
        return ResponseEntity.ok().body(reproductionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /reproductions/:id} : get the "id" reproduction.
     *
     * @param id the id of the reproduction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reproduction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/reproductions/{id}")
    public ResponseEntity<Reproduction> getReproduction(@PathVariable Long id) {
        log.debug("REST request to get Reproduction : {}", id);
        Optional<Reproduction> reproduction = reproductionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reproduction);
    }

    /**
     * {@code DELETE  /reproductions/:id} : delete the "id" reproduction.
     *
     * @param id the id of the reproduction to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/reproductions/{id}")
    public ResponseEntity<Void> deleteReproduction(@PathVariable Long id) {
        log.debug("REST request to delete Reproduction : {}", id);
        reproductionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
