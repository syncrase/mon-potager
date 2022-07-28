package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.domain.Temperature;
import fr.syncrase.ecosyst.repository.TemperatureRepository;
import fr.syncrase.ecosyst.service.TemperatureQueryService;
import fr.syncrase.ecosyst.service.TemperatureService;
import fr.syncrase.ecosyst.service.criteria.TemperatureCriteria;
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
 * REST controller for managing {@link fr.syncrase.ecosyst.domain.Temperature}.
 */
@RestController
@RequestMapping("/api")
public class TemperatureResource {

    private final Logger log = LoggerFactory.getLogger(TemperatureResource.class);

    private static final String ENTITY_NAME = "temperature";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TemperatureService temperatureService;

    private final TemperatureRepository temperatureRepository;

    private final TemperatureQueryService temperatureQueryService;

    public TemperatureResource(
        TemperatureService temperatureService,
        TemperatureRepository temperatureRepository,
        TemperatureQueryService temperatureQueryService
    ) {
        this.temperatureService = temperatureService;
        this.temperatureRepository = temperatureRepository;
        this.temperatureQueryService = temperatureQueryService;
    }

    /**
     * {@code POST  /temperatures} : Create a new temperature.
     *
     * @param temperature the temperature to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new temperature, or with status {@code 400 (Bad Request)} if the temperature has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/temperatures")
    public ResponseEntity<Temperature> createTemperature(@RequestBody Temperature temperature) throws URISyntaxException {
        log.debug("REST request to save Temperature : {}", temperature);
        if (temperature.getId() != null) {
            throw new BadRequestAlertException("A new temperature cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Temperature result = temperatureService.save(temperature);
        return ResponseEntity
            .created(new URI("/api/temperatures/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /temperatures/:id} : Updates an existing temperature.
     *
     * @param id the id of the temperature to save.
     * @param temperature the temperature to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated temperature,
     * or with status {@code 400 (Bad Request)} if the temperature is not valid,
     * or with status {@code 500 (Internal Server Error)} if the temperature couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/temperatures/{id}")
    public ResponseEntity<Temperature> updateTemperature(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Temperature temperature
    ) throws URISyntaxException {
        log.debug("REST request to update Temperature : {}, {}", id, temperature);
        if (temperature.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, temperature.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!temperatureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Temperature result = temperatureService.update(temperature);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, temperature.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /temperatures/:id} : Partial updates given fields of an existing temperature, field will ignore if it is null
     *
     * @param id the id of the temperature to save.
     * @param temperature the temperature to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated temperature,
     * or with status {@code 400 (Bad Request)} if the temperature is not valid,
     * or with status {@code 404 (Not Found)} if the temperature is not found,
     * or with status {@code 500 (Internal Server Error)} if the temperature couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/temperatures/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Temperature> partialUpdateTemperature(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Temperature temperature
    ) throws URISyntaxException {
        log.debug("REST request to partial update Temperature partially : {}, {}", id, temperature);
        if (temperature.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, temperature.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!temperatureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Temperature> result = temperatureService.partialUpdate(temperature);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, temperature.getId().toString())
        );
    }

    /**
     * {@code GET  /temperatures} : get all the temperatures.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of temperatures in body.
     */
    @GetMapping("/temperatures")
    public ResponseEntity<List<Temperature>> getAllTemperatures(
        TemperatureCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Temperatures by criteria: {}", criteria);
        Page<Temperature> page = temperatureQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /temperatures/count} : count all the temperatures.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/temperatures/count")
    public ResponseEntity<Long> countTemperatures(TemperatureCriteria criteria) {
        log.debug("REST request to count Temperatures by criteria: {}", criteria);
        return ResponseEntity.ok().body(temperatureQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /temperatures/:id} : get the "id" temperature.
     *
     * @param id the id of the temperature to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the temperature, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/temperatures/{id}")
    public ResponseEntity<Temperature> getTemperature(@PathVariable Long id) {
        log.debug("REST request to get Temperature : {}", id);
        Optional<Temperature> temperature = temperatureService.findOne(id);
        return ResponseUtil.wrapOrNotFound(temperature);
    }

    /**
     * {@code DELETE  /temperatures/:id} : delete the "id" temperature.
     *
     * @param id the id of the temperature to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/temperatures/{id}")
    public ResponseEntity<Void> deleteTemperature(@PathVariable Long id) {
        log.debug("REST request to delete Temperature : {}", id);
        temperatureService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
