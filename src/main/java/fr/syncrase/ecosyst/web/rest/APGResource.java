package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.APG;
import fr.syncrase.ecosyst.repository.APGRepository;
import fr.syncrase.ecosyst.service.APGQueryService;
import fr.syncrase.ecosyst.service.APGService;
import fr.syncrase.ecosyst.service.criteria.APGCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.APG}.
 */
@RestController
@RequestMapping("/api")
public class APGResource {

    private final Logger log = LoggerFactory.getLogger(APGResource.class);

    private static final String ENTITY_NAME = "aPG";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final APGService aPGService;

    private final APGRepository aPGRepository;

    private final APGQueryService aPGQueryService;

    public APGResource(APGService aPGService, APGRepository aPGRepository, APGQueryService aPGQueryService) {
        this.aPGService = aPGService;
        this.aPGRepository = aPGRepository;
        this.aPGQueryService = aPGQueryService;
    }

    /**
     * {@code POST  /apgs} : Create a new aPG.
     *
     * @param aPG the aPG to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aPG, or with status {@code 400 (Bad Request)} if the aPG has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/apgs")
    public ResponseEntity<APG> createAPG(@RequestBody APG aPG) throws URISyntaxException {
        log.debug("REST request to save APG : {}", aPG);
        if (aPG.getId() != null) {
            throw new BadRequestAlertException("A new aPG cannot already have an ID", ENTITY_NAME, "idexists");
        }
        APG result = aPGService.save(aPG);
        return ResponseEntity
            .created(new URI("/api/apgs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /apgs/:id} : Updates an existing aPG.
     *
     * @param id the id of the aPG to save.
     * @param aPG the aPG to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aPG,
     * or with status {@code 400 (Bad Request)} if the aPG is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aPG couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/apgs/{id}")
    public ResponseEntity<APG> updateAPG(@PathVariable(value = "id", required = false) final Long id, @RequestBody APG aPG)
        throws URISyntaxException {
        log.debug("REST request to update APG : {}, {}", id, aPG);
        if (aPG.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aPG.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aPGRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        APG result = aPGService.update(aPG);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, aPG.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /apgs/:id} : Partial updates given fields of an existing aPG, field will ignore if it is null
     *
     * @param id the id of the aPG to save.
     * @param aPG the aPG to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aPG,
     * or with status {@code 400 (Bad Request)} if the aPG is not valid,
     * or with status {@code 404 (Not Found)} if the aPG is not found,
     * or with status {@code 500 (Internal Server Error)} if the aPG couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/apgs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<APG> partialUpdateAPG(@PathVariable(value = "id", required = false) final Long id, @RequestBody APG aPG)
        throws URISyntaxException {
        log.debug("REST request to partial update APG partially : {}, {}", id, aPG);
        if (aPG.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aPG.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aPGRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<APG> result = aPGService.partialUpdate(aPG);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, aPG.getId().toString())
        );
    }

    /**
     * {@code GET  /apgs} : get all the aPGS.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aPGS in body.
     */
    @GetMapping("/apgs")
    public ResponseEntity<List<APG>> getAllAPGS(APGCriteria criteria, @org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get APGS by criteria: {}", criteria);
        Page<APG> page = aPGQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /apgs/count} : count all the aPGS.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/apgs/count")
    public ResponseEntity<Long> countAPGS(APGCriteria criteria) {
        log.debug("REST request to count APGS by criteria: {}", criteria);
        return ResponseEntity.ok().body(aPGQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /apgs/:id} : get the "id" aPG.
     *
     * @param id the id of the aPG to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aPG, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/apgs/{id}")
    public ResponseEntity<APG> getAPG(@PathVariable Long id) {
        log.debug("REST request to get APG : {}", id);
        Optional<APG> aPG = aPGService.findOne(id);
        return ResponseUtil.wrapOrNotFound(aPG);
    }

    /**
     * {@code DELETE  /apgs/:id} : delete the "id" aPG.
     *
     * @param id the id of the aPG to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/apgs/{id}")
    public ResponseEntity<Void> deleteAPG(@PathVariable Long id) {
        log.debug("REST request to delete APG : {}", id);
        aPGService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
