package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.repository.PlanteRepository;
import fr.syncrase.ecosyst.service.PlanteQueryService;
import fr.syncrase.ecosyst.service.PlanteService;
import fr.syncrase.ecosyst.service.criteria.PlanteCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Plante}.
 */
@RestController
@RequestMapping("/api")
public class PlanteResource {

    private final Logger log = LoggerFactory.getLogger(PlanteResource.class);

    private static final String ENTITY_NAME = "plante";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlanteService planteService;

    private final PlanteRepository planteRepository;

    private final PlanteQueryService planteQueryService;

    public PlanteResource(PlanteService planteService, PlanteRepository planteRepository, PlanteQueryService planteQueryService) {
        this.planteService = planteService;
        this.planteRepository = planteRepository;
        this.planteQueryService = planteQueryService;
    }

    /**
     * {@code POST  /plantes} : Create a new plante.
     *
     * @param plante the plante to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new plante, or with status {@code 400 (Bad Request)} if the plante has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/plantes")
    public ResponseEntity<Plante> createPlante(@RequestBody Plante plante) throws URISyntaxException {
        log.debug("REST request to save Plante : {}", plante);
        if (plante.getId() != null) {
            throw new BadRequestAlertException("A new plante cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Plante result = planteService.save(plante);
        return ResponseEntity
            .created(new URI("/api/plantes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /plantes/:id} : Updates an existing plante.
     *
     * @param id the id of the plante to save.
     * @param plante the plante to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated plante,
     * or with status {@code 400 (Bad Request)} if the plante is not valid,
     * or with status {@code 500 (Internal Server Error)} if the plante couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/plantes/{id}")
    public ResponseEntity<Plante> updatePlante(@PathVariable(value = "id", required = false) final Long id, @RequestBody Plante plante)
        throws URISyntaxException {
        log.debug("REST request to update Plante : {}, {}", id, plante);
        if (plante.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, plante.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!planteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Plante result = planteService.update(plante);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, plante.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /plantes/:id} : Partial updates given fields of an existing plante, field will ignore if it is null
     *
     * @param id the id of the plante to save.
     * @param plante the plante to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated plante,
     * or with status {@code 400 (Bad Request)} if the plante is not valid,
     * or with status {@code 404 (Not Found)} if the plante is not found,
     * or with status {@code 500 (Internal Server Error)} if the plante couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/plantes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Plante> partialUpdatePlante(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Plante plante
    ) throws URISyntaxException {
        log.debug("REST request to partial update Plante partially : {}, {}", id, plante);
        if (plante.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, plante.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!planteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Plante> result = planteService.partialUpdate(plante);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, plante.getId().toString())
        );
    }

    /**
     * {@code GET  /plantes} : get all the plantes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of plantes in body.
     */
    @GetMapping("/plantes")
    public ResponseEntity<List<Plante>> getAllPlantes(
        PlanteCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Plantes by criteria: {}", criteria);
        Page<Plante> page = planteQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /plantes/count} : count all the plantes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/plantes/count")
    public ResponseEntity<Long> countPlantes(PlanteCriteria criteria) {
        log.debug("REST request to count Plantes by criteria: {}", criteria);
        return ResponseEntity.ok().body(planteQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /plantes/:id} : get the "id" plante.
     *
     * @param id the id of the plante to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the plante, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/plantes/{id}")
    public ResponseEntity<Plante> getPlante(@PathVariable Long id) {
        log.debug("REST request to get Plante : {}", id);
        Optional<Plante> plante = planteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(plante);
    }

    /**
     * {@code DELETE  /plantes/:id} : delete the "id" plante.
     *
     * @param id the id of the plante to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/plantes/{id}")
    public ResponseEntity<Void> deletePlante(@PathVariable Long id) {
        log.debug("REST request to delete Plante : {}", id);
        planteService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
