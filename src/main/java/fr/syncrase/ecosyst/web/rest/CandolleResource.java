package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Candolle;
import fr.syncrase.ecosyst.repository.CandolleRepository;
import fr.syncrase.ecosyst.service.CandolleQueryService;
import fr.syncrase.ecosyst.service.CandolleService;
import fr.syncrase.ecosyst.service.criteria.CandolleCriteria;
import fr.syncrase.ecosyst.web.rest.errors.BadRequestAlertException;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Candolle}.
 */
@RestController
@RequestMapping("/api")
public class CandolleResource {

    private final Logger log = LoggerFactory.getLogger(CandolleResource.class);

    private static final String ENTITY_NAME = "candolle";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CandolleService candolleService;

    private final CandolleRepository candolleRepository;

    private final CandolleQueryService candolleQueryService;

    public CandolleResource(
        CandolleService candolleService,
        CandolleRepository candolleRepository,
        CandolleQueryService candolleQueryService
    ) {
        this.candolleService = candolleService;
        this.candolleRepository = candolleRepository;
        this.candolleQueryService = candolleQueryService;
    }

    /**
     * {@code POST  /candolles} : Create a new candolle.
     *
     * @param candolle the candolle to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new candolle, or with status {@code 400 (Bad Request)} if the candolle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/candolles")
    public ResponseEntity<Candolle> createCandolle(@RequestBody Candolle candolle) throws URISyntaxException {
        log.debug("REST request to save Candolle : {}", candolle);
        if (candolle.getId() != null) {
            throw new BadRequestAlertException("A new candolle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Candolle result = candolleService.save(candolle);
        return ResponseEntity
            .created(new URI("/api/candolles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /candolles/:id} : Updates an existing candolle.
     *
     * @param id the id of the candolle to save.
     * @param candolle the candolle to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candolle,
     * or with status {@code 400 (Bad Request)} if the candolle is not valid,
     * or with status {@code 500 (Internal Server Error)} if the candolle couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/candolles/{id}")
    public ResponseEntity<Candolle> updateCandolle(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Candolle candolle
    ) throws URISyntaxException {
        log.debug("REST request to update Candolle : {}, {}", id, candolle);
        if (candolle.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, candolle.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!candolleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Candolle result = candolleService.update(candolle);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, candolle.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /candolles/:id} : Partial updates given fields of an existing candolle, field will ignore if it is null
     *
     * @param id the id of the candolle to save.
     * @param candolle the candolle to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candolle,
     * or with status {@code 400 (Bad Request)} if the candolle is not valid,
     * or with status {@code 404 (Not Found)} if the candolle is not found,
     * or with status {@code 500 (Internal Server Error)} if the candolle couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/candolles/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Candolle> partialUpdateCandolle(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Candolle candolle
    ) throws URISyntaxException {
        log.debug("REST request to partial update Candolle partially : {}, {}", id, candolle);
        if (candolle.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, candolle.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!candolleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Candolle> result = candolleService.partialUpdate(candolle);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, candolle.getId().toString())
        );
    }

    /**
     * {@code GET  /candolles} : get all the candolles.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of candolles in body.
     */
    @GetMapping("/candolles")
    public ResponseEntity<List<Candolle>> getAllCandolles(
        CandolleCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Candolles by criteria: {}", criteria);
        Page<Candolle> page = candolleQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /candolles/count} : count all the candolles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/candolles/count")
    public ResponseEntity<Long> countCandolles(CandolleCriteria criteria) {
        log.debug("REST request to count Candolles by criteria: {}", criteria);
        return ResponseEntity.ok().body(candolleQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /candolles/:id} : get the "id" candolle.
     *
     * @param id the id of the candolle to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the candolle, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/candolles/{id}")
    public ResponseEntity<Candolle> getCandolle(@PathVariable Long id) {
        log.debug("REST request to get Candolle : {}", id);
        Optional<Candolle> candolle = candolleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(candolle);
    }

    /**
     * {@code DELETE  /candolles/:id} : delete the "id" candolle.
     *
     * @param id the id of the candolle to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/candolles/{id}")
    public ResponseEntity<Void> deleteCandolle(@PathVariable Long id) {
        log.debug("REST request to delete Candolle : {}", id);
        candolleService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
