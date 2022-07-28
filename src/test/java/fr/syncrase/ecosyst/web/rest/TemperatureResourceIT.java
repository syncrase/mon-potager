package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Temperature;
import fr.syncrase.ecosyst.repository.TemperatureRepository;
import fr.syncrase.ecosyst.service.criteria.TemperatureCriteria;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TemperatureResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TemperatureResourceIT {

    private static final Double DEFAULT_MIN = 1D;
    private static final Double UPDATED_MIN = 2D;
    private static final Double SMALLER_MIN = 1D - 1D;

    private static final Double DEFAULT_MAX = 1D;
    private static final Double UPDATED_MAX = 2D;
    private static final Double SMALLER_MAX = 1D - 1D;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_RUSTICITE = "AAAAAAAAAA";
    private static final String UPDATED_RUSTICITE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/temperatures";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TemperatureRepository temperatureRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTemperatureMockMvc;

    private Temperature temperature;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Temperature createEntity(EntityManager em) {
        Temperature temperature = new Temperature()
            .min(DEFAULT_MIN)
            .max(DEFAULT_MAX)
            .description(DEFAULT_DESCRIPTION)
            .rusticite(DEFAULT_RUSTICITE);
        return temperature;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Temperature createUpdatedEntity(EntityManager em) {
        Temperature temperature = new Temperature()
            .min(UPDATED_MIN)
            .max(UPDATED_MAX)
            .description(UPDATED_DESCRIPTION)
            .rusticite(UPDATED_RUSTICITE);
        return temperature;
    }

    @BeforeEach
    public void initTest() {
        temperature = createEntity(em);
    }

    @Test
    @Transactional
    void createTemperature() throws Exception {
        int databaseSizeBeforeCreate = temperatureRepository.findAll().size();
        // Create the Temperature
        restTemperatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(temperature)))
            .andExpect(status().isCreated());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeCreate + 1);
        Temperature testTemperature = temperatureList.get(temperatureList.size() - 1);
        assertThat(testTemperature.getMin()).isEqualTo(DEFAULT_MIN);
        assertThat(testTemperature.getMax()).isEqualTo(DEFAULT_MAX);
        assertThat(testTemperature.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTemperature.getRusticite()).isEqualTo(DEFAULT_RUSTICITE);
    }

    @Test
    @Transactional
    void createTemperatureWithExistingId() throws Exception {
        // Create the Temperature with an existing ID
        temperature.setId(1L);

        int databaseSizeBeforeCreate = temperatureRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTemperatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(temperature)))
            .andExpect(status().isBadRequest());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTemperatures() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList
        restTemperatureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(temperature.getId().intValue())))
            .andExpect(jsonPath("$.[*].min").value(hasItem(DEFAULT_MIN.doubleValue())))
            .andExpect(jsonPath("$.[*].max").value(hasItem(DEFAULT_MAX.doubleValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].rusticite").value(hasItem(DEFAULT_RUSTICITE)));
    }

    @Test
    @Transactional
    void getTemperature() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get the temperature
        restTemperatureMockMvc
            .perform(get(ENTITY_API_URL_ID, temperature.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(temperature.getId().intValue()))
            .andExpect(jsonPath("$.min").value(DEFAULT_MIN.doubleValue()))
            .andExpect(jsonPath("$.max").value(DEFAULT_MAX.doubleValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.rusticite").value(DEFAULT_RUSTICITE));
    }

    @Test
    @Transactional
    void getTemperaturesByIdFiltering() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        Long id = temperature.getId();

        defaultTemperatureShouldBeFound("id.equals=" + id);
        defaultTemperatureShouldNotBeFound("id.notEquals=" + id);

        defaultTemperatureShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTemperatureShouldNotBeFound("id.greaterThan=" + id);

        defaultTemperatureShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTemperatureShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTemperaturesByMinIsEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where min equals to DEFAULT_MIN
        defaultTemperatureShouldBeFound("min.equals=" + DEFAULT_MIN);

        // Get all the temperatureList where min equals to UPDATED_MIN
        defaultTemperatureShouldNotBeFound("min.equals=" + UPDATED_MIN);
    }

    @Test
    @Transactional
    void getAllTemperaturesByMinIsNotEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where min not equals to DEFAULT_MIN
        defaultTemperatureShouldNotBeFound("min.notEquals=" + DEFAULT_MIN);

        // Get all the temperatureList where min not equals to UPDATED_MIN
        defaultTemperatureShouldBeFound("min.notEquals=" + UPDATED_MIN);
    }

    @Test
    @Transactional
    void getAllTemperaturesByMinIsInShouldWork() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where min in DEFAULT_MIN or UPDATED_MIN
        defaultTemperatureShouldBeFound("min.in=" + DEFAULT_MIN + "," + UPDATED_MIN);

        // Get all the temperatureList where min equals to UPDATED_MIN
        defaultTemperatureShouldNotBeFound("min.in=" + UPDATED_MIN);
    }

    @Test
    @Transactional
    void getAllTemperaturesByMinIsNullOrNotNull() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where min is not null
        defaultTemperatureShouldBeFound("min.specified=true");

        // Get all the temperatureList where min is null
        defaultTemperatureShouldNotBeFound("min.specified=false");
    }

    @Test
    @Transactional
    void getAllTemperaturesByMinIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where min is greater than or equal to DEFAULT_MIN
        defaultTemperatureShouldBeFound("min.greaterThanOrEqual=" + DEFAULT_MIN);

        // Get all the temperatureList where min is greater than or equal to UPDATED_MIN
        defaultTemperatureShouldNotBeFound("min.greaterThanOrEqual=" + UPDATED_MIN);
    }

    @Test
    @Transactional
    void getAllTemperaturesByMinIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where min is less than or equal to DEFAULT_MIN
        defaultTemperatureShouldBeFound("min.lessThanOrEqual=" + DEFAULT_MIN);

        // Get all the temperatureList where min is less than or equal to SMALLER_MIN
        defaultTemperatureShouldNotBeFound("min.lessThanOrEqual=" + SMALLER_MIN);
    }

    @Test
    @Transactional
    void getAllTemperaturesByMinIsLessThanSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where min is less than DEFAULT_MIN
        defaultTemperatureShouldNotBeFound("min.lessThan=" + DEFAULT_MIN);

        // Get all the temperatureList where min is less than UPDATED_MIN
        defaultTemperatureShouldBeFound("min.lessThan=" + UPDATED_MIN);
    }

    @Test
    @Transactional
    void getAllTemperaturesByMinIsGreaterThanSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where min is greater than DEFAULT_MIN
        defaultTemperatureShouldNotBeFound("min.greaterThan=" + DEFAULT_MIN);

        // Get all the temperatureList where min is greater than SMALLER_MIN
        defaultTemperatureShouldBeFound("min.greaterThan=" + SMALLER_MIN);
    }

    @Test
    @Transactional
    void getAllTemperaturesByMaxIsEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where max equals to DEFAULT_MAX
        defaultTemperatureShouldBeFound("max.equals=" + DEFAULT_MAX);

        // Get all the temperatureList where max equals to UPDATED_MAX
        defaultTemperatureShouldNotBeFound("max.equals=" + UPDATED_MAX);
    }

    @Test
    @Transactional
    void getAllTemperaturesByMaxIsNotEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where max not equals to DEFAULT_MAX
        defaultTemperatureShouldNotBeFound("max.notEquals=" + DEFAULT_MAX);

        // Get all the temperatureList where max not equals to UPDATED_MAX
        defaultTemperatureShouldBeFound("max.notEquals=" + UPDATED_MAX);
    }

    @Test
    @Transactional
    void getAllTemperaturesByMaxIsInShouldWork() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where max in DEFAULT_MAX or UPDATED_MAX
        defaultTemperatureShouldBeFound("max.in=" + DEFAULT_MAX + "," + UPDATED_MAX);

        // Get all the temperatureList where max equals to UPDATED_MAX
        defaultTemperatureShouldNotBeFound("max.in=" + UPDATED_MAX);
    }

    @Test
    @Transactional
    void getAllTemperaturesByMaxIsNullOrNotNull() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where max is not null
        defaultTemperatureShouldBeFound("max.specified=true");

        // Get all the temperatureList where max is null
        defaultTemperatureShouldNotBeFound("max.specified=false");
    }

    @Test
    @Transactional
    void getAllTemperaturesByMaxIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where max is greater than or equal to DEFAULT_MAX
        defaultTemperatureShouldBeFound("max.greaterThanOrEqual=" + DEFAULT_MAX);

        // Get all the temperatureList where max is greater than or equal to UPDATED_MAX
        defaultTemperatureShouldNotBeFound("max.greaterThanOrEqual=" + UPDATED_MAX);
    }

    @Test
    @Transactional
    void getAllTemperaturesByMaxIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where max is less than or equal to DEFAULT_MAX
        defaultTemperatureShouldBeFound("max.lessThanOrEqual=" + DEFAULT_MAX);

        // Get all the temperatureList where max is less than or equal to SMALLER_MAX
        defaultTemperatureShouldNotBeFound("max.lessThanOrEqual=" + SMALLER_MAX);
    }

    @Test
    @Transactional
    void getAllTemperaturesByMaxIsLessThanSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where max is less than DEFAULT_MAX
        defaultTemperatureShouldNotBeFound("max.lessThan=" + DEFAULT_MAX);

        // Get all the temperatureList where max is less than UPDATED_MAX
        defaultTemperatureShouldBeFound("max.lessThan=" + UPDATED_MAX);
    }

    @Test
    @Transactional
    void getAllTemperaturesByMaxIsGreaterThanSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where max is greater than DEFAULT_MAX
        defaultTemperatureShouldNotBeFound("max.greaterThan=" + DEFAULT_MAX);

        // Get all the temperatureList where max is greater than SMALLER_MAX
        defaultTemperatureShouldBeFound("max.greaterThan=" + SMALLER_MAX);
    }

    @Test
    @Transactional
    void getAllTemperaturesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where description equals to DEFAULT_DESCRIPTION
        defaultTemperatureShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the temperatureList where description equals to UPDATED_DESCRIPTION
        defaultTemperatureShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTemperaturesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where description not equals to DEFAULT_DESCRIPTION
        defaultTemperatureShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the temperatureList where description not equals to UPDATED_DESCRIPTION
        defaultTemperatureShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTemperaturesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTemperatureShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the temperatureList where description equals to UPDATED_DESCRIPTION
        defaultTemperatureShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTemperaturesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where description is not null
        defaultTemperatureShouldBeFound("description.specified=true");

        // Get all the temperatureList where description is null
        defaultTemperatureShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTemperaturesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where description contains DEFAULT_DESCRIPTION
        defaultTemperatureShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the temperatureList where description contains UPDATED_DESCRIPTION
        defaultTemperatureShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTemperaturesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where description does not contain DEFAULT_DESCRIPTION
        defaultTemperatureShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the temperatureList where description does not contain UPDATED_DESCRIPTION
        defaultTemperatureShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTemperaturesByRusticiteIsEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where rusticite equals to DEFAULT_RUSTICITE
        defaultTemperatureShouldBeFound("rusticite.equals=" + DEFAULT_RUSTICITE);

        // Get all the temperatureList where rusticite equals to UPDATED_RUSTICITE
        defaultTemperatureShouldNotBeFound("rusticite.equals=" + UPDATED_RUSTICITE);
    }

    @Test
    @Transactional
    void getAllTemperaturesByRusticiteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where rusticite not equals to DEFAULT_RUSTICITE
        defaultTemperatureShouldNotBeFound("rusticite.notEquals=" + DEFAULT_RUSTICITE);

        // Get all the temperatureList where rusticite not equals to UPDATED_RUSTICITE
        defaultTemperatureShouldBeFound("rusticite.notEquals=" + UPDATED_RUSTICITE);
    }

    @Test
    @Transactional
    void getAllTemperaturesByRusticiteIsInShouldWork() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where rusticite in DEFAULT_RUSTICITE or UPDATED_RUSTICITE
        defaultTemperatureShouldBeFound("rusticite.in=" + DEFAULT_RUSTICITE + "," + UPDATED_RUSTICITE);

        // Get all the temperatureList where rusticite equals to UPDATED_RUSTICITE
        defaultTemperatureShouldNotBeFound("rusticite.in=" + UPDATED_RUSTICITE);
    }

    @Test
    @Transactional
    void getAllTemperaturesByRusticiteIsNullOrNotNull() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where rusticite is not null
        defaultTemperatureShouldBeFound("rusticite.specified=true");

        // Get all the temperatureList where rusticite is null
        defaultTemperatureShouldNotBeFound("rusticite.specified=false");
    }

    @Test
    @Transactional
    void getAllTemperaturesByRusticiteContainsSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where rusticite contains DEFAULT_RUSTICITE
        defaultTemperatureShouldBeFound("rusticite.contains=" + DEFAULT_RUSTICITE);

        // Get all the temperatureList where rusticite contains UPDATED_RUSTICITE
        defaultTemperatureShouldNotBeFound("rusticite.contains=" + UPDATED_RUSTICITE);
    }

    @Test
    @Transactional
    void getAllTemperaturesByRusticiteNotContainsSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where rusticite does not contain DEFAULT_RUSTICITE
        defaultTemperatureShouldNotBeFound("rusticite.doesNotContain=" + DEFAULT_RUSTICITE);

        // Get all the temperatureList where rusticite does not contain UPDATED_RUSTICITE
        defaultTemperatureShouldBeFound("rusticite.doesNotContain=" + UPDATED_RUSTICITE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTemperatureShouldBeFound(String filter) throws Exception {
        restTemperatureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(temperature.getId().intValue())))
            .andExpect(jsonPath("$.[*].min").value(hasItem(DEFAULT_MIN.doubleValue())))
            .andExpect(jsonPath("$.[*].max").value(hasItem(DEFAULT_MAX.doubleValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].rusticite").value(hasItem(DEFAULT_RUSTICITE)));

        // Check, that the count call also returns 1
        restTemperatureMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTemperatureShouldNotBeFound(String filter) throws Exception {
        restTemperatureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTemperatureMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTemperature() throws Exception {
        // Get the temperature
        restTemperatureMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTemperature() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();

        // Update the temperature
        Temperature updatedTemperature = temperatureRepository.findById(temperature.getId()).get();
        // Disconnect from session so that the updates on updatedTemperature are not directly saved in db
        em.detach(updatedTemperature);
        updatedTemperature.min(UPDATED_MIN).max(UPDATED_MAX).description(UPDATED_DESCRIPTION).rusticite(UPDATED_RUSTICITE);

        restTemperatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTemperature.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTemperature))
            )
            .andExpect(status().isOk());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
        Temperature testTemperature = temperatureList.get(temperatureList.size() - 1);
        assertThat(testTemperature.getMin()).isEqualTo(UPDATED_MIN);
        assertThat(testTemperature.getMax()).isEqualTo(UPDATED_MAX);
        assertThat(testTemperature.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTemperature.getRusticite()).isEqualTo(UPDATED_RUSTICITE);
    }

    @Test
    @Transactional
    void putNonExistingTemperature() throws Exception {
        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();
        temperature.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTemperatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, temperature.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(temperature))
            )
            .andExpect(status().isBadRequest());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTemperature() throws Exception {
        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();
        temperature.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemperatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(temperature))
            )
            .andExpect(status().isBadRequest());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTemperature() throws Exception {
        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();
        temperature.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemperatureMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(temperature)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTemperatureWithPatch() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();

        // Update the temperature using partial update
        Temperature partialUpdatedTemperature = new Temperature();
        partialUpdatedTemperature.setId(temperature.getId());

        partialUpdatedTemperature.min(UPDATED_MIN).description(UPDATED_DESCRIPTION);

        restTemperatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTemperature.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTemperature))
            )
            .andExpect(status().isOk());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
        Temperature testTemperature = temperatureList.get(temperatureList.size() - 1);
        assertThat(testTemperature.getMin()).isEqualTo(UPDATED_MIN);
        assertThat(testTemperature.getMax()).isEqualTo(DEFAULT_MAX);
        assertThat(testTemperature.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTemperature.getRusticite()).isEqualTo(DEFAULT_RUSTICITE);
    }

    @Test
    @Transactional
    void fullUpdateTemperatureWithPatch() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();

        // Update the temperature using partial update
        Temperature partialUpdatedTemperature = new Temperature();
        partialUpdatedTemperature.setId(temperature.getId());

        partialUpdatedTemperature.min(UPDATED_MIN).max(UPDATED_MAX).description(UPDATED_DESCRIPTION).rusticite(UPDATED_RUSTICITE);

        restTemperatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTemperature.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTemperature))
            )
            .andExpect(status().isOk());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
        Temperature testTemperature = temperatureList.get(temperatureList.size() - 1);
        assertThat(testTemperature.getMin()).isEqualTo(UPDATED_MIN);
        assertThat(testTemperature.getMax()).isEqualTo(UPDATED_MAX);
        assertThat(testTemperature.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTemperature.getRusticite()).isEqualTo(UPDATED_RUSTICITE);
    }

    @Test
    @Transactional
    void patchNonExistingTemperature() throws Exception {
        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();
        temperature.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTemperatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, temperature.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(temperature))
            )
            .andExpect(status().isBadRequest());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTemperature() throws Exception {
        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();
        temperature.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemperatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(temperature))
            )
            .andExpect(status().isBadRequest());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTemperature() throws Exception {
        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();
        temperature.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemperatureMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(temperature))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTemperature() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        int databaseSizeBeforeDelete = temperatureRepository.findAll().size();

        // Delete the temperature
        restTemperatureMockMvc
            .perform(delete(ENTITY_API_URL_ID, temperature.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
