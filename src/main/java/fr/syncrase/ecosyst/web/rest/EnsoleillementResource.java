package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Ensoleillement;
import fr.syncrase.ecosyst.repository.EnsoleillementRepository;
import fr.syncrase.ecosyst.service.EnsoleillementQueryService;
import fr.syncrase.ecosyst.service.EnsoleillementService;
import fr.syncrase.ecosyst.service.criteria.EnsoleillementCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Ensoleillement}.
 */
@RestController
@RequestMapping("/api")
public class EnsoleillementResource {

    private final Logger log = LoggerFactory.getLogger(EnsoleillementResource.class);

    private static final String ENTITY_NAME = "ensoleillement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnsoleillementService ensoleillementService;

    private final EnsoleillementRepository ensoleillementRepository;

    private final EnsoleillementQueryService ensoleillementQueryService;

    public EnsoleillementResource(
        EnsoleillementService ensoleillementService,
        EnsoleillementRepository ensoleillementRepository,
        EnsoleillementQueryService ensoleillementQueryService
    ) {
        this.ensoleillementService = ensoleillementService;
        this.ensoleillementRepository = ensoleillementRepository;
        this.ensoleillementQueryService = ensoleillementQueryService;
    }

    /**
     * {@code POST  /ensoleillements} : Create a new ensoleillement.
     *
     * @param ensoleillement the ensoleillement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ensoleillement, or with status {@code 400 (Bad Request)} if the ensoleillement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ensoleillements")
    public ResponseEntity<Ensoleillement> createEnsoleillement(@RequestBody Ensoleillement ensoleillement) throws URISyntaxException {
        log.debug("REST request to save Ensoleillement : {}", ensoleillement);
        if (ensoleillement.getId() != null) {
            throw new BadRequestAlertException("A new ensoleillement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Ensoleillement result = ensoleillementService.save(ensoleillement);
        return ResponseEntity
            .created(new URI("/api/ensoleillements/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ensoleillements/:id} : Updates an existing ensoleillement.
     *
     * @param id the id of the ensoleillement to save.
     * @param ensoleillement the ensoleillement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ensoleillement,
     * or with status {@code 400 (Bad Request)} if the ensoleillement is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ensoleillement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ensoleillements/{id}")
    public ResponseEntity<Ensoleillement> updateEnsoleillement(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Ensoleillement ensoleillement
    ) throws URISyntaxException {
        log.debug("REST request to update Ensoleillement : {}, {}", id, ensoleillement);
        if (ensoleillement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ensoleillement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ensoleillementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Ensoleillement result = ensoleillementService.update(ensoleillement);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ensoleillement.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ensoleillements/:id} : Partial updates given fields of an existing ensoleillement, field will ignore if it is null
     *
     * @param id the id of the ensoleillement to save.
     * @param ensoleillement the ensoleillement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ensoleillement,
     * or with status {@code 400 (Bad Request)} if the ensoleillement is not valid,
     * or with status {@code 404 (Not Found)} if the ensoleillement is not found,
     * or with status {@code 500 (Internal Server Error)} if the ensoleillement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ensoleillements/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Ensoleillement> partialUpdateEnsoleillement(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Ensoleillement ensoleillement
    ) throws URISyntaxException {
        log.debug("REST request to partial update Ensoleillement partially : {}, {}", id, ensoleillement);
        if (ensoleillement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ensoleillement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ensoleillementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Ensoleillement> result = ensoleillementService.partialUpdate(ensoleillement);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ensoleillement.getId().toString())
        );
    }

    /**
     * {@code GET  /ensoleillements} : get all the ensoleillements.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ensoleillements in body.
     */
    @GetMapping("/ensoleillements")
    public ResponseEntity<List<Ensoleillement>> getAllEnsoleillements(
        EnsoleillementCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Ensoleillements by criteria: {}", criteria);
        Page<Ensoleillement> page = ensoleillementQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ensoleillements/count} : count all the ensoleillements.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/ensoleillements/count")
    public ResponseEntity<Long> countEnsoleillements(EnsoleillementCriteria criteria) {
        log.debug("REST request to count Ensoleillements by criteria: {}", criteria);
        return ResponseEntity.ok().body(ensoleillementQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ensoleillements/:id} : get the "id" ensoleillement.
     *
     * @param id the id of the ensoleillement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ensoleillement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ensoleillements/{id}")
    public ResponseEntity<Ensoleillement> getEnsoleillement(@PathVariable Long id) {
        log.debug("REST request to get Ensoleillement : {}", id);
        Optional<Ensoleillement> ensoleillement = ensoleillementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ensoleillement);
    }

    /**
     * {@code DELETE  /ensoleillements/:id} : delete the "id" ensoleillement.
     *
     * @param id the id of the ensoleillement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ensoleillements/{id}")
    public ResponseEntity<Void> deleteEnsoleillement(@PathVariable Long id) {
        log.debug("REST request to delete Ensoleillement : {}", id);
        ensoleillementService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
