package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Takhtajan;
import fr.syncrase.ecosyst.repository.TakhtajanRepository;
import fr.syncrase.ecosyst.service.TakhtajanQueryService;
import fr.syncrase.ecosyst.service.TakhtajanService;
import fr.syncrase.ecosyst.service.criteria.TakhtajanCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Takhtajan}.
 */
@RestController
@RequestMapping("/api")
public class TakhtajanResource {

    private final Logger log = LoggerFactory.getLogger(TakhtajanResource.class);

    private static final String ENTITY_NAME = "takhtajan";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TakhtajanService takhtajanService;

    private final TakhtajanRepository takhtajanRepository;

    private final TakhtajanQueryService takhtajanQueryService;

    public TakhtajanResource(
        TakhtajanService takhtajanService,
        TakhtajanRepository takhtajanRepository,
        TakhtajanQueryService takhtajanQueryService
    ) {
        this.takhtajanService = takhtajanService;
        this.takhtajanRepository = takhtajanRepository;
        this.takhtajanQueryService = takhtajanQueryService;
    }

    /**
     * {@code POST  /takhtajans} : Create a new takhtajan.
     *
     * @param takhtajan the takhtajan to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new takhtajan, or with status {@code 400 (Bad Request)} if the takhtajan has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/takhtajans")
    public ResponseEntity<Takhtajan> createTakhtajan(@RequestBody Takhtajan takhtajan) throws URISyntaxException {
        log.debug("REST request to save Takhtajan : {}", takhtajan);
        if (takhtajan.getId() != null) {
            throw new BadRequestAlertException("A new takhtajan cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Takhtajan result = takhtajanService.save(takhtajan);
        return ResponseEntity
            .created(new URI("/api/takhtajans/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /takhtajans/:id} : Updates an existing takhtajan.
     *
     * @param id the id of the takhtajan to save.
     * @param takhtajan the takhtajan to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated takhtajan,
     * or with status {@code 400 (Bad Request)} if the takhtajan is not valid,
     * or with status {@code 500 (Internal Server Error)} if the takhtajan couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/takhtajans/{id}")
    public ResponseEntity<Takhtajan> updateTakhtajan(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Takhtajan takhtajan
    ) throws URISyntaxException {
        log.debug("REST request to update Takhtajan : {}, {}", id, takhtajan);
        if (takhtajan.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, takhtajan.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!takhtajanRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Takhtajan result = takhtajanService.update(takhtajan);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, takhtajan.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /takhtajans/:id} : Partial updates given fields of an existing takhtajan, field will ignore if it is null
     *
     * @param id the id of the takhtajan to save.
     * @param takhtajan the takhtajan to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated takhtajan,
     * or with status {@code 400 (Bad Request)} if the takhtajan is not valid,
     * or with status {@code 404 (Not Found)} if the takhtajan is not found,
     * or with status {@code 500 (Internal Server Error)} if the takhtajan couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/takhtajans/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Takhtajan> partialUpdateTakhtajan(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Takhtajan takhtajan
    ) throws URISyntaxException {
        log.debug("REST request to partial update Takhtajan partially : {}, {}", id, takhtajan);
        if (takhtajan.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, takhtajan.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!takhtajanRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Takhtajan> result = takhtajanService.partialUpdate(takhtajan);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, takhtajan.getId().toString())
        );
    }

    /**
     * {@code GET  /takhtajans} : get all the takhtajans.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of takhtajans in body.
     */
    @GetMapping("/takhtajans")
    public ResponseEntity<List<Takhtajan>> getAllTakhtajans(
        TakhtajanCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Takhtajans by criteria: {}", criteria);
        Page<Takhtajan> page = takhtajanQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /takhtajans/count} : count all the takhtajans.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/takhtajans/count")
    public ResponseEntity<Long> countTakhtajans(TakhtajanCriteria criteria) {
        log.debug("REST request to count Takhtajans by criteria: {}", criteria);
        return ResponseEntity.ok().body(takhtajanQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /takhtajans/:id} : get the "id" takhtajan.
     *
     * @param id the id of the takhtajan to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the takhtajan, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/takhtajans/{id}")
    public ResponseEntity<Takhtajan> getTakhtajan(@PathVariable Long id) {
        log.debug("REST request to get Takhtajan : {}", id);
        Optional<Takhtajan> takhtajan = takhtajanService.findOne(id);
        return ResponseUtil.wrapOrNotFound(takhtajan);
    }

    /**
     * {@code DELETE  /takhtajans/:id} : delete the "id" takhtajan.
     *
     * @param id the id of the takhtajan to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/takhtajans/{id}")
    public ResponseEntity<Void> deleteTakhtajan(@PathVariable Long id) {
        log.debug("REST request to delete Takhtajan : {}", id);
        takhtajanService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
