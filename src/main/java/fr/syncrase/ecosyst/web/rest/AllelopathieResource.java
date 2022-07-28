package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Allelopathie;
import fr.syncrase.ecosyst.repository.AllelopathieRepository;
import fr.syncrase.ecosyst.service.AllelopathieQueryService;
import fr.syncrase.ecosyst.service.AllelopathieService;
import fr.syncrase.ecosyst.service.criteria.AllelopathieCriteria;
import fr.syncrase.ecosyst.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Allelopathie}.
 */
@RestController
@RequestMapping("/api")
public class AllelopathieResource {

    private final Logger log = LoggerFactory.getLogger(AllelopathieResource.class);

    private static final String ENTITY_NAME = "allelopathie";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AllelopathieService allelopathieService;

    private final AllelopathieRepository allelopathieRepository;

    private final AllelopathieQueryService allelopathieQueryService;

    public AllelopathieResource(
        AllelopathieService allelopathieService,
        AllelopathieRepository allelopathieRepository,
        AllelopathieQueryService allelopathieQueryService
    ) {
        this.allelopathieService = allelopathieService;
        this.allelopathieRepository = allelopathieRepository;
        this.allelopathieQueryService = allelopathieQueryService;
    }

    /**
     * {@code POST  /allelopathies} : Create a new allelopathie.
     *
     * @param allelopathie the allelopathie to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new allelopathie, or with status {@code 400 (Bad Request)} if the allelopathie has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/allelopathies")
    public ResponseEntity<Allelopathie> createAllelopathie(@Valid @RequestBody Allelopathie allelopathie) throws URISyntaxException {
        log.debug("REST request to save Allelopathie : {}", allelopathie);
        if (allelopathie.getId() != null) {
            throw new BadRequestAlertException("A new allelopathie cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Allelopathie result = allelopathieService.save(allelopathie);
        return ResponseEntity
            .created(new URI("/api/allelopathies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /allelopathies/:id} : Updates an existing allelopathie.
     *
     * @param id the id of the allelopathie to save.
     * @param allelopathie the allelopathie to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated allelopathie,
     * or with status {@code 400 (Bad Request)} if the allelopathie is not valid,
     * or with status {@code 500 (Internal Server Error)} if the allelopathie couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/allelopathies/{id}")
    public ResponseEntity<Allelopathie> updateAllelopathie(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Allelopathie allelopathie
    ) throws URISyntaxException {
        log.debug("REST request to update Allelopathie : {}, {}", id, allelopathie);
        if (allelopathie.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, allelopathie.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!allelopathieRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Allelopathie result = allelopathieService.update(allelopathie);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, allelopathie.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /allelopathies/:id} : Partial updates given fields of an existing allelopathie, field will ignore if it is null
     *
     * @param id the id of the allelopathie to save.
     * @param allelopathie the allelopathie to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated allelopathie,
     * or with status {@code 400 (Bad Request)} if the allelopathie is not valid,
     * or with status {@code 404 (Not Found)} if the allelopathie is not found,
     * or with status {@code 500 (Internal Server Error)} if the allelopathie couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/allelopathies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Allelopathie> partialUpdateAllelopathie(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Allelopathie allelopathie
    ) throws URISyntaxException {
        log.debug("REST request to partial update Allelopathie partially : {}, {}", id, allelopathie);
        if (allelopathie.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, allelopathie.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!allelopathieRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Allelopathie> result = allelopathieService.partialUpdate(allelopathie);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, allelopathie.getId().toString())
        );
    }

    /**
     * {@code GET  /allelopathies} : get all the allelopathies.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of allelopathies in body.
     */
    @GetMapping("/allelopathies")
    public ResponseEntity<List<Allelopathie>> getAllAllelopathies(
        AllelopathieCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Allelopathies by criteria: {}", criteria);
        Page<Allelopathie> page = allelopathieQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /allelopathies/count} : count all the allelopathies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/allelopathies/count")
    public ResponseEntity<Long> countAllelopathies(AllelopathieCriteria criteria) {
        log.debug("REST request to count Allelopathies by criteria: {}", criteria);
        return ResponseEntity.ok().body(allelopathieQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /allelopathies/:id} : get the "id" allelopathie.
     *
     * @param id the id of the allelopathie to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the allelopathie, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/allelopathies/{id}")
    public ResponseEntity<Allelopathie> getAllelopathie(@PathVariable Long id) {
        log.debug("REST request to get Allelopathie : {}", id);
        Optional<Allelopathie> allelopathie = allelopathieService.findOne(id);
        return ResponseUtil.wrapOrNotFound(allelopathie);
    }

    /**
     * {@code DELETE  /allelopathies/:id} : delete the "id" allelopathie.
     *
     * @param id the id of the allelopathie to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/allelopathies/{id}")
    public ResponseEntity<Void> deleteAllelopathie(@PathVariable Long id) {
        log.debug("REST request to delete Allelopathie : {}", id);
        allelopathieService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
