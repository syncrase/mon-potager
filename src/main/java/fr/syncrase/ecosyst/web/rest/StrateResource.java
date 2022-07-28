package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Strate;
import fr.syncrase.ecosyst.repository.StrateRepository;
import fr.syncrase.ecosyst.service.StrateQueryService;
import fr.syncrase.ecosyst.service.StrateService;
import fr.syncrase.ecosyst.service.criteria.StrateCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Strate}.
 */
@RestController
@RequestMapping("/api")
public class StrateResource {

    private final Logger log = LoggerFactory.getLogger(StrateResource.class);

    private static final String ENTITY_NAME = "strate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StrateService strateService;

    private final StrateRepository strateRepository;

    private final StrateQueryService strateQueryService;

    public StrateResource(StrateService strateService, StrateRepository strateRepository, StrateQueryService strateQueryService) {
        this.strateService = strateService;
        this.strateRepository = strateRepository;
        this.strateQueryService = strateQueryService;
    }

    /**
     * {@code POST  /strates} : Create a new strate.
     *
     * @param strate the strate to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new strate, or with status {@code 400 (Bad Request)} if the strate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/strates")
    public ResponseEntity<Strate> createStrate(@RequestBody Strate strate) throws URISyntaxException {
        log.debug("REST request to save Strate : {}", strate);
        if (strate.getId() != null) {
            throw new BadRequestAlertException("A new strate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Strate result = strateService.save(strate);
        return ResponseEntity
            .created(new URI("/api/strates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /strates/:id} : Updates an existing strate.
     *
     * @param id the id of the strate to save.
     * @param strate the strate to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated strate,
     * or with status {@code 400 (Bad Request)} if the strate is not valid,
     * or with status {@code 500 (Internal Server Error)} if the strate couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/strates/{id}")
    public ResponseEntity<Strate> updateStrate(@PathVariable(value = "id", required = false) final Long id, @RequestBody Strate strate)
        throws URISyntaxException {
        log.debug("REST request to update Strate : {}, {}", id, strate);
        if (strate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, strate.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!strateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Strate result = strateService.update(strate);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, strate.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /strates/:id} : Partial updates given fields of an existing strate, field will ignore if it is null
     *
     * @param id the id of the strate to save.
     * @param strate the strate to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated strate,
     * or with status {@code 400 (Bad Request)} if the strate is not valid,
     * or with status {@code 404 (Not Found)} if the strate is not found,
     * or with status {@code 500 (Internal Server Error)} if the strate couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/strates/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Strate> partialUpdateStrate(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Strate strate
    ) throws URISyntaxException {
        log.debug("REST request to partial update Strate partially : {}, {}", id, strate);
        if (strate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, strate.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!strateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Strate> result = strateService.partialUpdate(strate);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, strate.getId().toString())
        );
    }

    /**
     * {@code GET  /strates} : get all the strates.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of strates in body.
     */
    @GetMapping("/strates")
    public ResponseEntity<List<Strate>> getAllStrates(
        StrateCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Strates by criteria: {}", criteria);
        Page<Strate> page = strateQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /strates/count} : count all the strates.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/strates/count")
    public ResponseEntity<Long> countStrates(StrateCriteria criteria) {
        log.debug("REST request to count Strates by criteria: {}", criteria);
        return ResponseEntity.ok().body(strateQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /strates/:id} : get the "id" strate.
     *
     * @param id the id of the strate to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the strate, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/strates/{id}")
    public ResponseEntity<Strate> getStrate(@PathVariable Long id) {
        log.debug("REST request to get Strate : {}", id);
        Optional<Strate> strate = strateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(strate);
    }

    /**
     * {@code DELETE  /strates/:id} : delete the "id" strate.
     *
     * @param id the id of the strate to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/strates/{id}")
    public ResponseEntity<Void> deleteStrate(@PathVariable Long id) {
        log.debug("REST request to delete Strate : {}", id);
        strateService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
