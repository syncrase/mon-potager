package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.repository.CronquistRankRepository;
import fr.syncrase.ecosyst.service.CronquistRankQueryService;
import fr.syncrase.ecosyst.service.CronquistRankService;
import fr.syncrase.ecosyst.service.criteria.CronquistRankCriteria;
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.CronquistRank}.
 */
@RestController
@RequestMapping("/api")
public class CronquistRankResource {

    private final Logger log = LoggerFactory.getLogger(CronquistRankResource.class);

    private static final String ENTITY_NAME = "cronquistRank";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CronquistRankService cronquistRankService;

    private final CronquistRankRepository cronquistRankRepository;

    private final CronquistRankQueryService cronquistRankQueryService;

    public CronquistRankResource(
        CronquistRankService cronquistRankService,
        CronquistRankRepository cronquistRankRepository,
        CronquistRankQueryService cronquistRankQueryService
    ) {
        this.cronquistRankService = cronquistRankService;
        this.cronquistRankRepository = cronquistRankRepository;
        this.cronquistRankQueryService = cronquistRankQueryService;
    }

    /**
     * {@code POST  /cronquist-ranks} : Create a new cronquistRank.
     *
     * @param cronquistRank the cronquistRank to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cronquistRank, or with status {@code 400 (Bad Request)} if the cronquistRank has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cronquist-ranks")
    public ResponseEntity<CronquistRank> createCronquistRank(@Valid @RequestBody CronquistRank cronquistRank) throws URISyntaxException {
        log.debug("REST request to save CronquistRank : {}", cronquistRank);
        if (cronquistRank.getId() != null) {
            throw new BadRequestAlertException("A new cronquistRank cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CronquistRank result = cronquistRankService.save(cronquistRank);
        return ResponseEntity
            .created(new URI("/api/cronquist-ranks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cronquist-ranks/:id} : Updates an existing cronquistRank.
     *
     * @param id the id of the cronquistRank to save.
     * @param cronquistRank the cronquistRank to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cronquistRank,
     * or with status {@code 400 (Bad Request)} if the cronquistRank is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cronquistRank couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cronquist-ranks/{id}")
    public ResponseEntity<CronquistRank> updateCronquistRank(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CronquistRank cronquistRank
    ) throws URISyntaxException {
        log.debug("REST request to update CronquistRank : {}, {}", id, cronquistRank);
        if (cronquistRank.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cronquistRank.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cronquistRankRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CronquistRank result = cronquistRankService.update(cronquistRank);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cronquistRank.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /cronquist-ranks/:id} : Partial updates given fields of an existing cronquistRank, field will ignore if it is null
     *
     * @param id the id of the cronquistRank to save.
     * @param cronquistRank the cronquistRank to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cronquistRank,
     * or with status {@code 400 (Bad Request)} if the cronquistRank is not valid,
     * or with status {@code 404 (Not Found)} if the cronquistRank is not found,
     * or with status {@code 500 (Internal Server Error)} if the cronquistRank couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/cronquist-ranks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CronquistRank> partialUpdateCronquistRank(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CronquistRank cronquistRank
    ) throws URISyntaxException {
        log.debug("REST request to partial update CronquistRank partially : {}, {}", id, cronquistRank);
        if (cronquistRank.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cronquistRank.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cronquistRankRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CronquistRank> result = cronquistRankService.partialUpdate(cronquistRank);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cronquistRank.getId().toString())
        );
    }

    /**
     * {@code GET  /cronquist-ranks} : get all the cronquistRanks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cronquistRanks in body.
     */
    @GetMapping("/cronquist-ranks")
    public ResponseEntity<List<CronquistRank>> getAllCronquistRanks(
        CronquistRankCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get CronquistRanks by criteria: {}", criteria);
        Page<CronquistRank> page = cronquistRankQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cronquist-ranks/count} : count all the cronquistRanks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/cronquist-ranks/count")
    public ResponseEntity<Long> countCronquistRanks(CronquistRankCriteria criteria) {
        log.debug("REST request to count CronquistRanks by criteria: {}", criteria);
        return ResponseEntity.ok().body(cronquistRankQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cronquist-ranks/:id} : get the "id" cronquistRank.
     *
     * @param id the id of the cronquistRank to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cronquistRank, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cronquist-ranks/{id}")
    public ResponseEntity<CronquistRank> getCronquistRank(@PathVariable Long id) {
        log.debug("REST request to get CronquistRank : {}", id);
        Optional<CronquistRank> cronquistRank = cronquistRankService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cronquistRank);
    }

    /**
     * {@code DELETE  /cronquist-ranks/:id} : delete the "id" cronquistRank.
     *
     * @param id the id of the cronquistRank to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cronquist-ranks/{id}")
    public ResponseEntity<Void> deleteCronquistRank(@PathVariable Long id) {
        log.debug("REST request to delete CronquistRank : {}", id);
        cronquistRankService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
