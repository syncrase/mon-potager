package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Thorne;
import fr.syncrase.ecosyst.repository.ThorneRepository;
import fr.syncrase.ecosyst.service.ThorneQueryService;
import fr.syncrase.ecosyst.service.ThorneService;
import fr.syncrase.ecosyst.service.criteria.ThorneCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Thorne}.
 */
@RestController
@RequestMapping("/api")
public class ThorneResource {

    private final Logger log = LoggerFactory.getLogger(ThorneResource.class);

    private static final String ENTITY_NAME = "thorne";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ThorneService thorneService;

    private final ThorneRepository thorneRepository;

    private final ThorneQueryService thorneQueryService;

    public ThorneResource(ThorneService thorneService, ThorneRepository thorneRepository, ThorneQueryService thorneQueryService) {
        this.thorneService = thorneService;
        this.thorneRepository = thorneRepository;
        this.thorneQueryService = thorneQueryService;
    }

    /**
     * {@code POST  /thornes} : Create a new thorne.
     *
     * @param thorne the thorne to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new thorne, or with status {@code 400 (Bad Request)} if the thorne has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/thornes")
    public ResponseEntity<Thorne> createThorne(@RequestBody Thorne thorne) throws URISyntaxException {
        log.debug("REST request to save Thorne : {}", thorne);
        if (thorne.getId() != null) {
            throw new BadRequestAlertException("A new thorne cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Thorne result = thorneService.save(thorne);
        return ResponseEntity
            .created(new URI("/api/thornes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /thornes/:id} : Updates an existing thorne.
     *
     * @param id the id of the thorne to save.
     * @param thorne the thorne to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated thorne,
     * or with status {@code 400 (Bad Request)} if the thorne is not valid,
     * or with status {@code 500 (Internal Server Error)} if the thorne couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/thornes/{id}")
    public ResponseEntity<Thorne> updateThorne(@PathVariable(value = "id", required = false) final Long id, @RequestBody Thorne thorne)
        throws URISyntaxException {
        log.debug("REST request to update Thorne : {}, {}", id, thorne);
        if (thorne.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, thorne.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!thorneRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Thorne result = thorneService.update(thorne);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, thorne.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /thornes/:id} : Partial updates given fields of an existing thorne, field will ignore if it is null
     *
     * @param id the id of the thorne to save.
     * @param thorne the thorne to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated thorne,
     * or with status {@code 400 (Bad Request)} if the thorne is not valid,
     * or with status {@code 404 (Not Found)} if the thorne is not found,
     * or with status {@code 500 (Internal Server Error)} if the thorne couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/thornes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Thorne> partialUpdateThorne(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Thorne thorne
    ) throws URISyntaxException {
        log.debug("REST request to partial update Thorne partially : {}, {}", id, thorne);
        if (thorne.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, thorne.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!thorneRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Thorne> result = thorneService.partialUpdate(thorne);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, thorne.getId().toString())
        );
    }

    /**
     * {@code GET  /thornes} : get all the thornes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of thornes in body.
     */
    @GetMapping("/thornes")
    public ResponseEntity<List<Thorne>> getAllThornes(
        ThorneCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Thornes by criteria: {}", criteria);
        Page<Thorne> page = thorneQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /thornes/count} : count all the thornes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/thornes/count")
    public ResponseEntity<Long> countThornes(ThorneCriteria criteria) {
        log.debug("REST request to count Thornes by criteria: {}", criteria);
        return ResponseEntity.ok().body(thorneQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /thornes/:id} : get the "id" thorne.
     *
     * @param id the id of the thorne to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the thorne, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/thornes/{id}")
    public ResponseEntity<Thorne> getThorne(@PathVariable Long id) {
        log.debug("REST request to get Thorne : {}", id);
        Optional<Thorne> thorne = thorneService.findOne(id);
        return ResponseUtil.wrapOrNotFound(thorne);
    }

    /**
     * {@code DELETE  /thornes/:id} : delete the "id" thorne.
     *
     * @param id the id of the thorne to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/thornes/{id}")
    public ResponseEntity<Void> deleteThorne(@PathVariable Long id) {
        log.debug("REST request to delete Thorne : {}", id);
        thorneService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
