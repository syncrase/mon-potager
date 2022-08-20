package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Classification;
import fr.syncrase.ecosyst.repository.ClassificationRepository;
import fr.syncrase.ecosyst.service.ClassificationQueryService;
import fr.syncrase.ecosyst.service.ClassificationService;
import fr.syncrase.ecosyst.service.criteria.ClassificationCriteria;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Classification}.
 */
@RestController
@RequestMapping("/api")
public class ClassificationResource {

    private final Logger log = LoggerFactory.getLogger(ClassificationResource.class);

    private static final String ENTITY_NAME = "classification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClassificationService classificationService;

    private final ClassificationRepository classificationRepository;

    private final ClassificationQueryService classificationQueryService;

    public ClassificationResource(
        ClassificationService classificationService,
        ClassificationRepository classificationRepository,
        ClassificationQueryService classificationQueryService
    ) {
        this.classificationService = classificationService;
        this.classificationRepository = classificationRepository;
        this.classificationQueryService = classificationQueryService;
    }

    /**
     * {@code POST  /classifications} : Create a new classification.
     *
     * @param classification the classification to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new classification, or with status {@code 400 (Bad Request)} if the classification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/classifications")
    public ResponseEntity<Classification> createClassification(@RequestBody Classification classification) throws URISyntaxException {
        log.debug("REST request to save Classification : {}", classification);
        if (classification.getId() != null) {
            throw new BadRequestAlertException("A new classification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Classification result = classificationService.save(classification);
        return ResponseEntity
            .created(new URI("/api/classifications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /classifications/:id} : Updates an existing classification.
     *
     * @param id the id of the classification to save.
     * @param classification the classification to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classification,
     * or with status {@code 400 (Bad Request)} if the classification is not valid,
     * or with status {@code 500 (Internal Server Error)} if the classification couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/classifications/{id}")
    public ResponseEntity<Classification> updateClassification(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Classification classification
    ) throws URISyntaxException {
        log.debug("REST request to update Classification : {}, {}", id, classification);
        if (classification.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classification.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!classificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Classification result = classificationService.update(classification);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, classification.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /classifications/:id} : Partial updates given fields of an existing classification, field will ignore if it is null
     *
     * @param id the id of the classification to save.
     * @param classification the classification to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classification,
     * or with status {@code 400 (Bad Request)} if the classification is not valid,
     * or with status {@code 404 (Not Found)} if the classification is not found,
     * or with status {@code 500 (Internal Server Error)} if the classification couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/classifications/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Classification> partialUpdateClassification(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Classification classification
    ) throws URISyntaxException {
        log.debug("REST request to partial update Classification partially : {}, {}", id, classification);
        if (classification.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classification.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!classificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Classification> result = classificationService.partialUpdate(classification);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, classification.getId().toString())
        );
    }

    /**
     * {@code GET  /classifications} : get all the classifications.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of classifications in body.
     */
    @GetMapping("/classifications")
    public ResponseEntity<List<Classification>> getAllClassifications(
        ClassificationCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Classifications by criteria: {}", criteria);
        Page<Classification> page = classificationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /classifications/count} : count all the classifications.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/classifications/count")
    public ResponseEntity<Long> countClassifications(ClassificationCriteria criteria) {
        log.debug("REST request to count Classifications by criteria: {}", criteria);
        return ResponseEntity.ok().body(classificationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /classifications/:id} : get the "id" classification.
     *
     * @param id the id of the classification to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the classification, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/classifications/{id}")
    public ResponseEntity<Classification> getClassification(@PathVariable Long id) {
        log.debug("REST request to get Classification : {}", id);
        Optional<Classification> classification = classificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(classification);
    }

    /**
     * {@code DELETE  /classifications/:id} : delete the "id" classification.
     *
     * @param id the id of the classification to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/classifications/{id}")
    public ResponseEntity<Void> deleteClassification(@PathVariable Long id) {
        log.debug("REST request to delete Classification : {}", id);
        classificationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
