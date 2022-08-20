package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Reference;
import fr.syncrase.ecosyst.repository.ReferenceRepository;
import fr.syncrase.ecosyst.service.ReferenceQueryService;
import fr.syncrase.ecosyst.service.ReferenceService;
import fr.syncrase.ecosyst.service.criteria.ReferenceCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Reference}.
 */
@RestController
@RequestMapping("/api")
public class ReferenceResource {

    private final Logger log = LoggerFactory.getLogger(ReferenceResource.class);

    private static final String ENTITY_NAME = "reference";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReferenceService referenceService;

    private final ReferenceRepository referenceRepository;

    private final ReferenceQueryService referenceQueryService;

    public ReferenceResource(
        ReferenceService referenceService,
        ReferenceRepository referenceRepository,
        ReferenceQueryService referenceQueryService
    ) {
        this.referenceService = referenceService;
        this.referenceRepository = referenceRepository;
        this.referenceQueryService = referenceQueryService;
    }

    /**
     * {@code POST  /references} : Create a new reference.
     *
     * @param reference the reference to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reference, or with status {@code 400 (Bad Request)} if the reference has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/references")
    public ResponseEntity<Reference> createReference(@Valid @RequestBody Reference reference) throws URISyntaxException {
        log.debug("REST request to save Reference : {}", reference);
        if (reference.getId() != null) {
            throw new BadRequestAlertException("A new reference cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Reference result = referenceService.save(reference);
        return ResponseEntity
            .created(new URI("/api/references/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /references/:id} : Updates an existing reference.
     *
     * @param id the id of the reference to save.
     * @param reference the reference to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reference,
     * or with status {@code 400 (Bad Request)} if the reference is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reference couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/references/{id}")
    public ResponseEntity<Reference> updateReference(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Reference reference
    ) throws URISyntaxException {
        log.debug("REST request to update Reference : {}, {}", id, reference);
        if (reference.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reference.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!referenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Reference result = referenceService.update(reference);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reference.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /references/:id} : Partial updates given fields of an existing reference, field will ignore if it is null
     *
     * @param id the id of the reference to save.
     * @param reference the reference to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reference,
     * or with status {@code 400 (Bad Request)} if the reference is not valid,
     * or with status {@code 404 (Not Found)} if the reference is not found,
     * or with status {@code 500 (Internal Server Error)} if the reference couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/references/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Reference> partialUpdateReference(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Reference reference
    ) throws URISyntaxException {
        log.debug("REST request to partial update Reference partially : {}, {}", id, reference);
        if (reference.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reference.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!referenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Reference> result = referenceService.partialUpdate(reference);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reference.getId().toString())
        );
    }

    /**
     * {@code GET  /references} : get all the references.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of references in body.
     */
    @GetMapping("/references")
    public ResponseEntity<List<Reference>> getAllReferences(
        ReferenceCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get References by criteria: {}", criteria);
        Page<Reference> page = referenceQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /references/count} : count all the references.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/references/count")
    public ResponseEntity<Long> countReferences(ReferenceCriteria criteria) {
        log.debug("REST request to count References by criteria: {}", criteria);
        return ResponseEntity.ok().body(referenceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /references/:id} : get the "id" reference.
     *
     * @param id the id of the reference to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reference, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/references/{id}")
    public ResponseEntity<Reference> getReference(@PathVariable Long id) {
        log.debug("REST request to get Reference : {}", id);
        Optional<Reference> reference = referenceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reference);
    }

    /**
     * {@code DELETE  /references/:id} : delete the "id" reference.
     *
     * @param id the id of the reference to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/references/{id}")
    public ResponseEntity<Void> deleteReference(@PathVariable Long id) {
        log.debug("REST request to delete Reference : {}", id);
        referenceService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
