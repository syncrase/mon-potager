package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Sol;
import fr.syncrase.ecosyst.repository.SolRepository;
import fr.syncrase.ecosyst.service.SolQueryService;
import fr.syncrase.ecosyst.service.SolService;
import fr.syncrase.ecosyst.service.criteria.SolCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Sol}.
 */
@RestController
@RequestMapping("/api")
public class SolResource {

    private final Logger log = LoggerFactory.getLogger(SolResource.class);

    private static final String ENTITY_NAME = "sol";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SolService solService;

    private final SolRepository solRepository;

    private final SolQueryService solQueryService;

    public SolResource(SolService solService, SolRepository solRepository, SolQueryService solQueryService) {
        this.solService = solService;
        this.solRepository = solRepository;
        this.solQueryService = solQueryService;
    }

    /**
     * {@code POST  /sols} : Create a new sol.
     *
     * @param sol the sol to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sol, or with status {@code 400 (Bad Request)} if the sol has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sols")
    public ResponseEntity<Sol> createSol(@RequestBody Sol sol) throws URISyntaxException {
        log.debug("REST request to save Sol : {}", sol);
        if (sol.getId() != null) {
            throw new BadRequestAlertException("A new sol cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Sol result = solService.save(sol);
        return ResponseEntity
            .created(new URI("/api/sols/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sols/:id} : Updates an existing sol.
     *
     * @param id the id of the sol to save.
     * @param sol the sol to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sol,
     * or with status {@code 400 (Bad Request)} if the sol is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sol couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sols/{id}")
    public ResponseEntity<Sol> updateSol(@PathVariable(value = "id", required = false) final Long id, @RequestBody Sol sol)
        throws URISyntaxException {
        log.debug("REST request to update Sol : {}, {}", id, sol);
        if (sol.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sol.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!solRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Sol result = solService.update(sol);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sol.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sols/:id} : Partial updates given fields of an existing sol, field will ignore if it is null
     *
     * @param id the id of the sol to save.
     * @param sol the sol to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sol,
     * or with status {@code 400 (Bad Request)} if the sol is not valid,
     * or with status {@code 404 (Not Found)} if the sol is not found,
     * or with status {@code 500 (Internal Server Error)} if the sol couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sols/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Sol> partialUpdateSol(@PathVariable(value = "id", required = false) final Long id, @RequestBody Sol sol)
        throws URISyntaxException {
        log.debug("REST request to partial update Sol partially : {}, {}", id, sol);
        if (sol.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sol.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!solRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Sol> result = solService.partialUpdate(sol);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sol.getId().toString())
        );
    }

    /**
     * {@code GET  /sols} : get all the sols.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sols in body.
     */
    @GetMapping("/sols")
    public ResponseEntity<List<Sol>> getAllSols(SolCriteria criteria, @org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get Sols by criteria: {}", criteria);
        Page<Sol> page = solQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sols/count} : count all the sols.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/sols/count")
    public ResponseEntity<Long> countSols(SolCriteria criteria) {
        log.debug("REST request to count Sols by criteria: {}", criteria);
        return ResponseEntity.ok().body(solQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sols/:id} : get the "id" sol.
     *
     * @param id the id of the sol to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sol, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sols/{id}")
    public ResponseEntity<Sol> getSol(@PathVariable Long id) {
        log.debug("REST request to get Sol : {}", id);
        Optional<Sol> sol = solService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sol);
    }

    /**
     * {@code DELETE  /sols/:id} : delete the "id" sol.
     *
     * @param id the id of the sol to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sols/{id}")
    public ResponseEntity<Void> deleteSol(@PathVariable Long id) {
        log.debug("REST request to delete Sol : {}", id);
        solService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
