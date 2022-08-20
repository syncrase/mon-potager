package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.BenthamHooker;
import fr.syncrase.ecosyst.repository.BenthamHookerRepository;
import fr.syncrase.ecosyst.service.BenthamHookerQueryService;
import fr.syncrase.ecosyst.service.BenthamHookerService;
import fr.syncrase.ecosyst.service.criteria.BenthamHookerCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.BenthamHooker}.
 */
@RestController
@RequestMapping("/api")
public class BenthamHookerResource {

    private final Logger log = LoggerFactory.getLogger(BenthamHookerResource.class);

    private static final String ENTITY_NAME = "benthamHooker";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BenthamHookerService benthamHookerService;

    private final BenthamHookerRepository benthamHookerRepository;

    private final BenthamHookerQueryService benthamHookerQueryService;

    public BenthamHookerResource(
        BenthamHookerService benthamHookerService,
        BenthamHookerRepository benthamHookerRepository,
        BenthamHookerQueryService benthamHookerQueryService
    ) {
        this.benthamHookerService = benthamHookerService;
        this.benthamHookerRepository = benthamHookerRepository;
        this.benthamHookerQueryService = benthamHookerQueryService;
    }

    /**
     * {@code POST  /bentham-hookers} : Create a new benthamHooker.
     *
     * @param benthamHooker the benthamHooker to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new benthamHooker, or with status {@code 400 (Bad Request)} if the benthamHooker has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/bentham-hookers")
    public ResponseEntity<BenthamHooker> createBenthamHooker(@RequestBody BenthamHooker benthamHooker) throws URISyntaxException {
        log.debug("REST request to save BenthamHooker : {}", benthamHooker);
        if (benthamHooker.getId() != null) {
            throw new BadRequestAlertException("A new benthamHooker cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BenthamHooker result = benthamHookerService.save(benthamHooker);
        return ResponseEntity
            .created(new URI("/api/bentham-hookers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /bentham-hookers/:id} : Updates an existing benthamHooker.
     *
     * @param id the id of the benthamHooker to save.
     * @param benthamHooker the benthamHooker to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated benthamHooker,
     * or with status {@code 400 (Bad Request)} if the benthamHooker is not valid,
     * or with status {@code 500 (Internal Server Error)} if the benthamHooker couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bentham-hookers/{id}")
    public ResponseEntity<BenthamHooker> updateBenthamHooker(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BenthamHooker benthamHooker
    ) throws URISyntaxException {
        log.debug("REST request to update BenthamHooker : {}, {}", id, benthamHooker);
        if (benthamHooker.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, benthamHooker.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!benthamHookerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BenthamHooker result = benthamHookerService.update(benthamHooker);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, benthamHooker.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /bentham-hookers/:id} : Partial updates given fields of an existing benthamHooker, field will ignore if it is null
     *
     * @param id the id of the benthamHooker to save.
     * @param benthamHooker the benthamHooker to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated benthamHooker,
     * or with status {@code 400 (Bad Request)} if the benthamHooker is not valid,
     * or with status {@code 404 (Not Found)} if the benthamHooker is not found,
     * or with status {@code 500 (Internal Server Error)} if the benthamHooker couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/bentham-hookers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BenthamHooker> partialUpdateBenthamHooker(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BenthamHooker benthamHooker
    ) throws URISyntaxException {
        log.debug("REST request to partial update BenthamHooker partially : {}, {}", id, benthamHooker);
        if (benthamHooker.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, benthamHooker.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!benthamHookerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BenthamHooker> result = benthamHookerService.partialUpdate(benthamHooker);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, benthamHooker.getId().toString())
        );
    }

    /**
     * {@code GET  /bentham-hookers} : get all the benthamHookers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of benthamHookers in body.
     */
    @GetMapping("/bentham-hookers")
    public ResponseEntity<List<BenthamHooker>> getAllBenthamHookers(
        BenthamHookerCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get BenthamHookers by criteria: {}", criteria);
        Page<BenthamHooker> page = benthamHookerQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /bentham-hookers/count} : count all the benthamHookers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/bentham-hookers/count")
    public ResponseEntity<Long> countBenthamHookers(BenthamHookerCriteria criteria) {
        log.debug("REST request to count BenthamHookers by criteria: {}", criteria);
        return ResponseEntity.ok().body(benthamHookerQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /bentham-hookers/:id} : get the "id" benthamHooker.
     *
     * @param id the id of the benthamHooker to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the benthamHooker, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bentham-hookers/{id}")
    public ResponseEntity<BenthamHooker> getBenthamHooker(@PathVariable Long id) {
        log.debug("REST request to get BenthamHooker : {}", id);
        Optional<BenthamHooker> benthamHooker = benthamHookerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(benthamHooker);
    }

    /**
     * {@code DELETE  /bentham-hookers/:id} : delete the "id" benthamHooker.
     *
     * @param id the id of the benthamHooker to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/bentham-hookers/{id}")
    public ResponseEntity<Void> deleteBenthamHooker(@PathVariable Long id) {
        log.debug("REST request to delete BenthamHooker : {}", id);
        benthamHookerService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
