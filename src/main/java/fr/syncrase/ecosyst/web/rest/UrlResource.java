package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Url;
import fr.syncrase.ecosyst.repository.UrlRepository;
import fr.syncrase.ecosyst.service.UrlQueryService;
import fr.syncrase.ecosyst.service.UrlService;
import fr.syncrase.ecosyst.service.criteria.UrlCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Url}.
 */
@RestController
@RequestMapping("/api")
public class UrlResource {

    private final Logger log = LoggerFactory.getLogger(UrlResource.class);

    private static final String ENTITY_NAME = "url";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UrlService urlService;

    private final UrlRepository urlRepository;

    private final UrlQueryService urlQueryService;

    public UrlResource(UrlService urlService, UrlRepository urlRepository, UrlQueryService urlQueryService) {
        this.urlService = urlService;
        this.urlRepository = urlRepository;
        this.urlQueryService = urlQueryService;
    }

    /**
     * {@code POST  /urls} : Create a new url.
     *
     * @param url the url to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new url, or with status {@code 400 (Bad Request)} if the url has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/urls")
    public ResponseEntity<Url> createUrl(@Valid @RequestBody Url url) throws URISyntaxException {
        log.debug("REST request to save Url : {}", url);
        if (url.getId() != null) {
            throw new BadRequestAlertException("A new url cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Url result = urlService.save(url);
        return ResponseEntity
            .created(new URI("/api/urls/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /urls/:id} : Updates an existing url.
     *
     * @param id the id of the url to save.
     * @param url the url to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated url,
     * or with status {@code 400 (Bad Request)} if the url is not valid,
     * or with status {@code 500 (Internal Server Error)} if the url couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/urls/{id}")
    public ResponseEntity<Url> updateUrl(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Url url)
        throws URISyntaxException {
        log.debug("REST request to update Url : {}, {}", id, url);
        if (url.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, url.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!urlRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Url result = urlService.update(url);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, url.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /urls/:id} : Partial updates given fields of an existing url, field will ignore if it is null
     *
     * @param id the id of the url to save.
     * @param url the url to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated url,
     * or with status {@code 400 (Bad Request)} if the url is not valid,
     * or with status {@code 404 (Not Found)} if the url is not found,
     * or with status {@code 500 (Internal Server Error)} if the url couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/urls/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Url> partialUpdateUrl(@PathVariable(value = "id", required = false) final Long id, @NotNull @RequestBody Url url)
        throws URISyntaxException {
        log.debug("REST request to partial update Url partially : {}, {}", id, url);
        if (url.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, url.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!urlRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Url> result = urlService.partialUpdate(url);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, url.getId().toString())
        );
    }

    /**
     * {@code GET  /urls} : get all the urls.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of urls in body.
     */
    @GetMapping("/urls")
    public ResponseEntity<List<Url>> getAllUrls(UrlCriteria criteria, @org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get Urls by criteria: {}", criteria);
        Page<Url> page = urlQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /urls/count} : count all the urls.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/urls/count")
    public ResponseEntity<Long> countUrls(UrlCriteria criteria) {
        log.debug("REST request to count Urls by criteria: {}", criteria);
        return ResponseEntity.ok().body(urlQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /urls/:id} : get the "id" url.
     *
     * @param id the id of the url to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the url, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/urls/{id}")
    public ResponseEntity<Url> getUrl(@PathVariable Long id) {
        log.debug("REST request to get Url : {}", id);
        Optional<Url> url = urlService.findOne(id);
        return ResponseUtil.wrapOrNotFound(url);
    }

    /**
     * {@code DELETE  /urls/:id} : delete the "id" url.
     *
     * @param id the id of the url to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/urls/{id}")
    public ResponseEntity<Void> deleteUrl(@PathVariable Long id) {
        log.debug("REST request to delete Url : {}", id);
        urlService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
