package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Wettstein;
import fr.syncrase.ecosyst.repository.WettsteinRepository;
import fr.syncrase.ecosyst.service.WettsteinQueryService;
import fr.syncrase.ecosyst.service.WettsteinService;
import fr.syncrase.ecosyst.service.criteria.WettsteinCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Wettstein}.
 */
@RestController
@RequestMapping("/api")
public class WettsteinResource {

    private final Logger log = LoggerFactory.getLogger(WettsteinResource.class);

    private static final String ENTITY_NAME = "wettstein";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WettsteinService wettsteinService;

    private final WettsteinRepository wettsteinRepository;

    private final WettsteinQueryService wettsteinQueryService;

    public WettsteinResource(
        WettsteinService wettsteinService,
        WettsteinRepository wettsteinRepository,
        WettsteinQueryService wettsteinQueryService
    ) {
        this.wettsteinService = wettsteinService;
        this.wettsteinRepository = wettsteinRepository;
        this.wettsteinQueryService = wettsteinQueryService;
    }

    /**
     * {@code POST  /wettsteins} : Create a new wettstein.
     *
     * @param wettstein the wettstein to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new wettstein, or with status {@code 400 (Bad Request)} if the wettstein has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/wettsteins")
    public ResponseEntity<Wettstein> createWettstein(@RequestBody Wettstein wettstein) throws URISyntaxException {
        log.debug("REST request to save Wettstein : {}", wettstein);
        if (wettstein.getId() != null) {
            throw new BadRequestAlertException("A new wettstein cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Wettstein result = wettsteinService.save(wettstein);
        return ResponseEntity
            .created(new URI("/api/wettsteins/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /wettsteins/:id} : Updates an existing wettstein.
     *
     * @param id the id of the wettstein to save.
     * @param wettstein the wettstein to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wettstein,
     * or with status {@code 400 (Bad Request)} if the wettstein is not valid,
     * or with status {@code 500 (Internal Server Error)} if the wettstein couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/wettsteins/{id}")
    public ResponseEntity<Wettstein> updateWettstein(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Wettstein wettstein
    ) throws URISyntaxException {
        log.debug("REST request to update Wettstein : {}, {}", id, wettstein);
        if (wettstein.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wettstein.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!wettsteinRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Wettstein result = wettsteinService.update(wettstein);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wettstein.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /wettsteins/:id} : Partial updates given fields of an existing wettstein, field will ignore if it is null
     *
     * @param id the id of the wettstein to save.
     * @param wettstein the wettstein to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wettstein,
     * or with status {@code 400 (Bad Request)} if the wettstein is not valid,
     * or with status {@code 404 (Not Found)} if the wettstein is not found,
     * or with status {@code 500 (Internal Server Error)} if the wettstein couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/wettsteins/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Wettstein> partialUpdateWettstein(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Wettstein wettstein
    ) throws URISyntaxException {
        log.debug("REST request to partial update Wettstein partially : {}, {}", id, wettstein);
        if (wettstein.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wettstein.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!wettsteinRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Wettstein> result = wettsteinService.partialUpdate(wettstein);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wettstein.getId().toString())
        );
    }

    /**
     * {@code GET  /wettsteins} : get all the wettsteins.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of wettsteins in body.
     */
    @GetMapping("/wettsteins")
    public ResponseEntity<List<Wettstein>> getAllWettsteins(
        WettsteinCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Wettsteins by criteria: {}", criteria);
        Page<Wettstein> page = wettsteinQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /wettsteins/count} : count all the wettsteins.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/wettsteins/count")
    public ResponseEntity<Long> countWettsteins(WettsteinCriteria criteria) {
        log.debug("REST request to count Wettsteins by criteria: {}", criteria);
        return ResponseEntity.ok().body(wettsteinQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /wettsteins/:id} : get the "id" wettstein.
     *
     * @param id the id of the wettstein to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the wettstein, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/wettsteins/{id}")
    public ResponseEntity<Wettstein> getWettstein(@PathVariable Long id) {
        log.debug("REST request to get Wettstein : {}", id);
        Optional<Wettstein> wettstein = wettsteinService.findOne(id);
        return ResponseUtil.wrapOrNotFound(wettstein);
    }

    /**
     * {@code DELETE  /wettsteins/:id} : delete the "id" wettstein.
     *
     * @param id the id of the wettstein to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/wettsteins/{id}")
    public ResponseEntity<Void> deleteWettstein(@PathVariable Long id) {
        log.debug("REST request to delete Wettstein : {}", id);
        wettsteinService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
