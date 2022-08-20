package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Dahlgren;
import fr.syncrase.ecosyst.repository.DahlgrenRepository;
import fr.syncrase.ecosyst.service.DahlgrenQueryService;
import fr.syncrase.ecosyst.service.DahlgrenService;
import fr.syncrase.ecosyst.service.criteria.DahlgrenCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Dahlgren}.
 */
@RestController
@RequestMapping("/api")
public class DahlgrenResource {

    private final Logger log = LoggerFactory.getLogger(DahlgrenResource.class);

    private static final String ENTITY_NAME = "dahlgren";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DahlgrenService dahlgrenService;

    private final DahlgrenRepository dahlgrenRepository;

    private final DahlgrenQueryService dahlgrenQueryService;

    public DahlgrenResource(
        DahlgrenService dahlgrenService,
        DahlgrenRepository dahlgrenRepository,
        DahlgrenQueryService dahlgrenQueryService
    ) {
        this.dahlgrenService = dahlgrenService;
        this.dahlgrenRepository = dahlgrenRepository;
        this.dahlgrenQueryService = dahlgrenQueryService;
    }

    /**
     * {@code POST  /dahlgrens} : Create a new dahlgren.
     *
     * @param dahlgren the dahlgren to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dahlgren, or with status {@code 400 (Bad Request)} if the dahlgren has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/dahlgrens")
    public ResponseEntity<Dahlgren> createDahlgren(@RequestBody Dahlgren dahlgren) throws URISyntaxException {
        log.debug("REST request to save Dahlgren : {}", dahlgren);
        if (dahlgren.getId() != null) {
            throw new BadRequestAlertException("A new dahlgren cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Dahlgren result = dahlgrenService.save(dahlgren);
        return ResponseEntity
            .created(new URI("/api/dahlgrens/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /dahlgrens/:id} : Updates an existing dahlgren.
     *
     * @param id the id of the dahlgren to save.
     * @param dahlgren the dahlgren to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dahlgren,
     * or with status {@code 400 (Bad Request)} if the dahlgren is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dahlgren couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/dahlgrens/{id}")
    public ResponseEntity<Dahlgren> updateDahlgren(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Dahlgren dahlgren
    ) throws URISyntaxException {
        log.debug("REST request to update Dahlgren : {}, {}", id, dahlgren);
        if (dahlgren.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dahlgren.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dahlgrenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Dahlgren result = dahlgrenService.update(dahlgren);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dahlgren.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /dahlgrens/:id} : Partial updates given fields of an existing dahlgren, field will ignore if it is null
     *
     * @param id the id of the dahlgren to save.
     * @param dahlgren the dahlgren to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dahlgren,
     * or with status {@code 400 (Bad Request)} if the dahlgren is not valid,
     * or with status {@code 404 (Not Found)} if the dahlgren is not found,
     * or with status {@code 500 (Internal Server Error)} if the dahlgren couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/dahlgrens/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Dahlgren> partialUpdateDahlgren(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Dahlgren dahlgren
    ) throws URISyntaxException {
        log.debug("REST request to partial update Dahlgren partially : {}, {}", id, dahlgren);
        if (dahlgren.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dahlgren.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dahlgrenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Dahlgren> result = dahlgrenService.partialUpdate(dahlgren);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dahlgren.getId().toString())
        );
    }

    /**
     * {@code GET  /dahlgrens} : get all the dahlgrens.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dahlgrens in body.
     */
    @GetMapping("/dahlgrens")
    public ResponseEntity<List<Dahlgren>> getAllDahlgrens(
        DahlgrenCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Dahlgrens by criteria: {}", criteria);
        Page<Dahlgren> page = dahlgrenQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /dahlgrens/count} : count all the dahlgrens.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/dahlgrens/count")
    public ResponseEntity<Long> countDahlgrens(DahlgrenCriteria criteria) {
        log.debug("REST request to count Dahlgrens by criteria: {}", criteria);
        return ResponseEntity.ok().body(dahlgrenQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /dahlgrens/:id} : get the "id" dahlgren.
     *
     * @param id the id of the dahlgren to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dahlgren, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/dahlgrens/{id}")
    public ResponseEntity<Dahlgren> getDahlgren(@PathVariable Long id) {
        log.debug("REST request to get Dahlgren : {}", id);
        Optional<Dahlgren> dahlgren = dahlgrenService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dahlgren);
    }

    /**
     * {@code DELETE  /dahlgrens/:id} : delete the "id" dahlgren.
     *
     * @param id the id of the dahlgren to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/dahlgrens/{id}")
    public ResponseEntity<Void> deleteDahlgren(@PathVariable Long id) {
        log.debug("REST request to delete Dahlgren : {}", id);
        dahlgrenService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
