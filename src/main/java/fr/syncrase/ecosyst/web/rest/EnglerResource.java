package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Engler;
import fr.syncrase.ecosyst.repository.EnglerRepository;
import fr.syncrase.ecosyst.service.EnglerQueryService;
import fr.syncrase.ecosyst.service.EnglerService;
import fr.syncrase.ecosyst.service.criteria.EnglerCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Engler}.
 */
@RestController
@RequestMapping("/api")
public class EnglerResource {

    private final Logger log = LoggerFactory.getLogger(EnglerResource.class);

    private static final String ENTITY_NAME = "engler";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnglerService englerService;

    private final EnglerRepository englerRepository;

    private final EnglerQueryService englerQueryService;

    public EnglerResource(EnglerService englerService, EnglerRepository englerRepository, EnglerQueryService englerQueryService) {
        this.englerService = englerService;
        this.englerRepository = englerRepository;
        this.englerQueryService = englerQueryService;
    }

    /**
     * {@code POST  /englers} : Create a new engler.
     *
     * @param engler the engler to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new engler, or with status {@code 400 (Bad Request)} if the engler has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/englers")
    public ResponseEntity<Engler> createEngler(@RequestBody Engler engler) throws URISyntaxException {
        log.debug("REST request to save Engler : {}", engler);
        if (engler.getId() != null) {
            throw new BadRequestAlertException("A new engler cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Engler result = englerService.save(engler);
        return ResponseEntity
            .created(new URI("/api/englers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /englers/:id} : Updates an existing engler.
     *
     * @param id the id of the engler to save.
     * @param engler the engler to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated engler,
     * or with status {@code 400 (Bad Request)} if the engler is not valid,
     * or with status {@code 500 (Internal Server Error)} if the engler couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/englers/{id}")
    public ResponseEntity<Engler> updateEngler(@PathVariable(value = "id", required = false) final Long id, @RequestBody Engler engler)
        throws URISyntaxException {
        log.debug("REST request to update Engler : {}, {}", id, engler);
        if (engler.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, engler.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!englerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Engler result = englerService.update(engler);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, engler.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /englers/:id} : Partial updates given fields of an existing engler, field will ignore if it is null
     *
     * @param id the id of the engler to save.
     * @param engler the engler to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated engler,
     * or with status {@code 400 (Bad Request)} if the engler is not valid,
     * or with status {@code 404 (Not Found)} if the engler is not found,
     * or with status {@code 500 (Internal Server Error)} if the engler couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/englers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Engler> partialUpdateEngler(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Engler engler
    ) throws URISyntaxException {
        log.debug("REST request to partial update Engler partially : {}, {}", id, engler);
        if (engler.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, engler.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!englerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Engler> result = englerService.partialUpdate(engler);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, engler.getId().toString())
        );
    }

    /**
     * {@code GET  /englers} : get all the englers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of englers in body.
     */
    @GetMapping("/englers")
    public ResponseEntity<List<Engler>> getAllEnglers(
        EnglerCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Englers by criteria: {}", criteria);
        Page<Engler> page = englerQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /englers/count} : count all the englers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/englers/count")
    public ResponseEntity<Long> countEnglers(EnglerCriteria criteria) {
        log.debug("REST request to count Englers by criteria: {}", criteria);
        return ResponseEntity.ok().body(englerQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /englers/:id} : get the "id" engler.
     *
     * @param id the id of the engler to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the engler, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/englers/{id}")
    public ResponseEntity<Engler> getEngler(@PathVariable Long id) {
        log.debug("REST request to get Engler : {}", id);
        Optional<Engler> engler = englerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(engler);
    }

    /**
     * {@code DELETE  /englers/:id} : delete the "id" engler.
     *
     * @param id the id of the engler to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/englers/{id}")
    public ResponseEntity<Void> deleteEngler(@PathVariable Long id) {
        log.debug("REST request to delete Engler : {}", id);
        englerService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
