package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.TypeSemis;
import fr.syncrase.ecosyst.repository.TypeSemisRepository;
import fr.syncrase.ecosyst.service.TypeSemisQueryService;
import fr.syncrase.ecosyst.service.TypeSemisService;
import fr.syncrase.ecosyst.service.criteria.TypeSemisCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.TypeSemis}.
 */
@RestController
@RequestMapping("/api")
public class TypeSemisResource {

    private final Logger log = LoggerFactory.getLogger(TypeSemisResource.class);

    private static final String ENTITY_NAME = "typeSemis";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TypeSemisService typeSemisService;

    private final TypeSemisRepository typeSemisRepository;

    private final TypeSemisQueryService typeSemisQueryService;

    public TypeSemisResource(
        TypeSemisService typeSemisService,
        TypeSemisRepository typeSemisRepository,
        TypeSemisQueryService typeSemisQueryService
    ) {
        this.typeSemisService = typeSemisService;
        this.typeSemisRepository = typeSemisRepository;
        this.typeSemisQueryService = typeSemisQueryService;
    }

    /**
     * {@code POST  /type-semis} : Create a new typeSemis.
     *
     * @param typeSemis the typeSemis to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new typeSemis, or with status {@code 400 (Bad Request)} if the typeSemis has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/type-semis")
    public ResponseEntity<TypeSemis> createTypeSemis(@Valid @RequestBody TypeSemis typeSemis) throws URISyntaxException {
        log.debug("REST request to save TypeSemis : {}", typeSemis);
        if (typeSemis.getId() != null) {
            throw new BadRequestAlertException("A new typeSemis cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TypeSemis result = typeSemisService.save(typeSemis);
        return ResponseEntity
            .created(new URI("/api/type-semis/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /type-semis/:id} : Updates an existing typeSemis.
     *
     * @param id the id of the typeSemis to save.
     * @param typeSemis the typeSemis to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeSemis,
     * or with status {@code 400 (Bad Request)} if the typeSemis is not valid,
     * or with status {@code 500 (Internal Server Error)} if the typeSemis couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/type-semis/{id}")
    public ResponseEntity<TypeSemis> updateTypeSemis(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TypeSemis typeSemis
    ) throws URISyntaxException {
        log.debug("REST request to update TypeSemis : {}, {}", id, typeSemis);
        if (typeSemis.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typeSemis.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!typeSemisRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TypeSemis result = typeSemisService.update(typeSemis);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, typeSemis.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /type-semis/:id} : Partial updates given fields of an existing typeSemis, field will ignore if it is null
     *
     * @param id the id of the typeSemis to save.
     * @param typeSemis the typeSemis to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeSemis,
     * or with status {@code 400 (Bad Request)} if the typeSemis is not valid,
     * or with status {@code 404 (Not Found)} if the typeSemis is not found,
     * or with status {@code 500 (Internal Server Error)} if the typeSemis couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/type-semis/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TypeSemis> partialUpdateTypeSemis(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TypeSemis typeSemis
    ) throws URISyntaxException {
        log.debug("REST request to partial update TypeSemis partially : {}, {}", id, typeSemis);
        if (typeSemis.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typeSemis.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!typeSemisRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TypeSemis> result = typeSemisService.partialUpdate(typeSemis);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, typeSemis.getId().toString())
        );
    }

    /**
     * {@code GET  /type-semis} : get all the typeSemis.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of typeSemis in body.
     */
    @GetMapping("/type-semis")
    public ResponseEntity<List<TypeSemis>> getAllTypeSemis(
        TypeSemisCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TypeSemis by criteria: {}", criteria);
        Page<TypeSemis> page = typeSemisQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /type-semis/count} : count all the typeSemis.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/type-semis/count")
    public ResponseEntity<Long> countTypeSemis(TypeSemisCriteria criteria) {
        log.debug("REST request to count TypeSemis by criteria: {}", criteria);
        return ResponseEntity.ok().body(typeSemisQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /type-semis/:id} : get the "id" typeSemis.
     *
     * @param id the id of the typeSemis to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the typeSemis, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/type-semis/{id}")
    public ResponseEntity<TypeSemis> getTypeSemis(@PathVariable Long id) {
        log.debug("REST request to get TypeSemis : {}", id);
        Optional<TypeSemis> typeSemis = typeSemisService.findOne(id);
        return ResponseUtil.wrapOrNotFound(typeSemis);
    }

    /**
     * {@code DELETE  /type-semis/:id} : delete the "id" typeSemis.
     *
     * @param id the id of the typeSemis to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/type-semis/{id}")
    public ResponseEntity<Void> deleteTypeSemis(@PathVariable Long id) {
        log.debug("REST request to delete TypeSemis : {}", id);
        typeSemisService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
