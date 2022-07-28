package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Sol;
import fr.syncrase.ecosyst.repository.SolRepository;
import fr.syncrase.ecosyst.service.criteria.SolCriteria;
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
 * Integration tests for the {@link SolResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SolResourceIT {

    private static final Double DEFAULT_PH_MIN = 1D;
    private static final Double UPDATED_PH_MIN = 2D;
    private static final Double SMALLER_PH_MIN = 1D - 1D;

    private static final Double DEFAULT_PH_MAX = 1D;
    private static final Double UPDATED_PH_MAX = 2D;
    private static final Double SMALLER_PH_MAX = 1D - 1D;

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_RICHESSE = "AAAAAAAAAA";
    private static final String UPDATED_RICHESSE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sols";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SolRepository solRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSolMockMvc;

    private Sol sol;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sol createEntity(EntityManager em) {
        Sol sol = new Sol().phMin(DEFAULT_PH_MIN).phMax(DEFAULT_PH_MAX).type(DEFAULT_TYPE).richesse(DEFAULT_RICHESSE);
        return sol;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sol createUpdatedEntity(EntityManager em) {
        Sol sol = new Sol().phMin(UPDATED_PH_MIN).phMax(UPDATED_PH_MAX).type(UPDATED_TYPE).richesse(UPDATED_RICHESSE);
        return sol;
    }

    @BeforeEach
    public void initTest() {
        sol = createEntity(em);
    }

    @Test
    @Transactional
    void createSol() throws Exception {
        int databaseSizeBeforeCreate = solRepository.findAll().size();
        // Create the Sol
        restSolMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sol)))
            .andExpect(status().isCreated());

        // Validate the Sol in the database
        List<Sol> solList = solRepository.findAll();
        assertThat(solList).hasSize(databaseSizeBeforeCreate + 1);
        Sol testSol = solList.get(solList.size() - 1);
        assertThat(testSol.getPhMin()).isEqualTo(DEFAULT_PH_MIN);
        assertThat(testSol.getPhMax()).isEqualTo(DEFAULT_PH_MAX);
        assertThat(testSol.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testSol.getRichesse()).isEqualTo(DEFAULT_RICHESSE);
    }

    @Test
    @Transactional
    void createSolWithExistingId() throws Exception {
        // Create the Sol with an existing ID
        sol.setId(1L);

        int databaseSizeBeforeCreate = solRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSolMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sol)))
            .andExpect(status().isBadRequest());

        // Validate the Sol in the database
        List<Sol> solList = solRepository.findAll();
        assertThat(solList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSols() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList
        restSolMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sol.getId().intValue())))
            .andExpect(jsonPath("$.[*].phMin").value(hasItem(DEFAULT_PH_MIN.doubleValue())))
            .andExpect(jsonPath("$.[*].phMax").value(hasItem(DEFAULT_PH_MAX.doubleValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].richesse").value(hasItem(DEFAULT_RICHESSE)));
    }

    @Test
    @Transactional
    void getSol() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get the sol
        restSolMockMvc
            .perform(get(ENTITY_API_URL_ID, sol.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sol.getId().intValue()))
            .andExpect(jsonPath("$.phMin").value(DEFAULT_PH_MIN.doubleValue()))
            .andExpect(jsonPath("$.phMax").value(DEFAULT_PH_MAX.doubleValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.richesse").value(DEFAULT_RICHESSE));
    }

    @Test
    @Transactional
    void getSolsByIdFiltering() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        Long id = sol.getId();

        defaultSolShouldBeFound("id.equals=" + id);
        defaultSolShouldNotBeFound("id.notEquals=" + id);

        defaultSolShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSolShouldNotBeFound("id.greaterThan=" + id);

        defaultSolShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSolShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSolsByPhMinIsEqualToSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMin equals to DEFAULT_PH_MIN
        defaultSolShouldBeFound("phMin.equals=" + DEFAULT_PH_MIN);

        // Get all the solList where phMin equals to UPDATED_PH_MIN
        defaultSolShouldNotBeFound("phMin.equals=" + UPDATED_PH_MIN);
    }

    @Test
    @Transactional
    void getAllSolsByPhMinIsNotEqualToSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMin not equals to DEFAULT_PH_MIN
        defaultSolShouldNotBeFound("phMin.notEquals=" + DEFAULT_PH_MIN);

        // Get all the solList where phMin not equals to UPDATED_PH_MIN
        defaultSolShouldBeFound("phMin.notEquals=" + UPDATED_PH_MIN);
    }

    @Test
    @Transactional
    void getAllSolsByPhMinIsInShouldWork() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMin in DEFAULT_PH_MIN or UPDATED_PH_MIN
        defaultSolShouldBeFound("phMin.in=" + DEFAULT_PH_MIN + "," + UPDATED_PH_MIN);

        // Get all the solList where phMin equals to UPDATED_PH_MIN
        defaultSolShouldNotBeFound("phMin.in=" + UPDATED_PH_MIN);
    }

    @Test
    @Transactional
    void getAllSolsByPhMinIsNullOrNotNull() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMin is not null
        defaultSolShouldBeFound("phMin.specified=true");

        // Get all the solList where phMin is null
        defaultSolShouldNotBeFound("phMin.specified=false");
    }

    @Test
    @Transactional
    void getAllSolsByPhMinIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMin is greater than or equal to DEFAULT_PH_MIN
        defaultSolShouldBeFound("phMin.greaterThanOrEqual=" + DEFAULT_PH_MIN);

        // Get all the solList where phMin is greater than or equal to UPDATED_PH_MIN
        defaultSolShouldNotBeFound("phMin.greaterThanOrEqual=" + UPDATED_PH_MIN);
    }

    @Test
    @Transactional
    void getAllSolsByPhMinIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMin is less than or equal to DEFAULT_PH_MIN
        defaultSolShouldBeFound("phMin.lessThanOrEqual=" + DEFAULT_PH_MIN);

        // Get all the solList where phMin is less than or equal to SMALLER_PH_MIN
        defaultSolShouldNotBeFound("phMin.lessThanOrEqual=" + SMALLER_PH_MIN);
    }

    @Test
    @Transactional
    void getAllSolsByPhMinIsLessThanSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMin is less than DEFAULT_PH_MIN
        defaultSolShouldNotBeFound("phMin.lessThan=" + DEFAULT_PH_MIN);

        // Get all the solList where phMin is less than UPDATED_PH_MIN
        defaultSolShouldBeFound("phMin.lessThan=" + UPDATED_PH_MIN);
    }

    @Test
    @Transactional
    void getAllSolsByPhMinIsGreaterThanSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMin is greater than DEFAULT_PH_MIN
        defaultSolShouldNotBeFound("phMin.greaterThan=" + DEFAULT_PH_MIN);

        // Get all the solList where phMin is greater than SMALLER_PH_MIN
        defaultSolShouldBeFound("phMin.greaterThan=" + SMALLER_PH_MIN);
    }

    @Test
    @Transactional
    void getAllSolsByPhMaxIsEqualToSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMax equals to DEFAULT_PH_MAX
        defaultSolShouldBeFound("phMax.equals=" + DEFAULT_PH_MAX);

        // Get all the solList where phMax equals to UPDATED_PH_MAX
        defaultSolShouldNotBeFound("phMax.equals=" + UPDATED_PH_MAX);
    }

    @Test
    @Transactional
    void getAllSolsByPhMaxIsNotEqualToSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMax not equals to DEFAULT_PH_MAX
        defaultSolShouldNotBeFound("phMax.notEquals=" + DEFAULT_PH_MAX);

        // Get all the solList where phMax not equals to UPDATED_PH_MAX
        defaultSolShouldBeFound("phMax.notEquals=" + UPDATED_PH_MAX);
    }

    @Test
    @Transactional
    void getAllSolsByPhMaxIsInShouldWork() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMax in DEFAULT_PH_MAX or UPDATED_PH_MAX
        defaultSolShouldBeFound("phMax.in=" + DEFAULT_PH_MAX + "," + UPDATED_PH_MAX);

        // Get all the solList where phMax equals to UPDATED_PH_MAX
        defaultSolShouldNotBeFound("phMax.in=" + UPDATED_PH_MAX);
    }

    @Test
    @Transactional
    void getAllSolsByPhMaxIsNullOrNotNull() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMax is not null
        defaultSolShouldBeFound("phMax.specified=true");

        // Get all the solList where phMax is null
        defaultSolShouldNotBeFound("phMax.specified=false");
    }

    @Test
    @Transactional
    void getAllSolsByPhMaxIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMax is greater than or equal to DEFAULT_PH_MAX
        defaultSolShouldBeFound("phMax.greaterThanOrEqual=" + DEFAULT_PH_MAX);

        // Get all the solList where phMax is greater than or equal to UPDATED_PH_MAX
        defaultSolShouldNotBeFound("phMax.greaterThanOrEqual=" + UPDATED_PH_MAX);
    }

    @Test
    @Transactional
    void getAllSolsByPhMaxIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMax is less than or equal to DEFAULT_PH_MAX
        defaultSolShouldBeFound("phMax.lessThanOrEqual=" + DEFAULT_PH_MAX);

        // Get all the solList where phMax is less than or equal to SMALLER_PH_MAX
        defaultSolShouldNotBeFound("phMax.lessThanOrEqual=" + SMALLER_PH_MAX);
    }

    @Test
    @Transactional
    void getAllSolsByPhMaxIsLessThanSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMax is less than DEFAULT_PH_MAX
        defaultSolShouldNotBeFound("phMax.lessThan=" + DEFAULT_PH_MAX);

        // Get all the solList where phMax is less than UPDATED_PH_MAX
        defaultSolShouldBeFound("phMax.lessThan=" + UPDATED_PH_MAX);
    }

    @Test
    @Transactional
    void getAllSolsByPhMaxIsGreaterThanSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where phMax is greater than DEFAULT_PH_MAX
        defaultSolShouldNotBeFound("phMax.greaterThan=" + DEFAULT_PH_MAX);

        // Get all the solList where phMax is greater than SMALLER_PH_MAX
        defaultSolShouldBeFound("phMax.greaterThan=" + SMALLER_PH_MAX);
    }

    @Test
    @Transactional
    void getAllSolsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where type equals to DEFAULT_TYPE
        defaultSolShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the solList where type equals to UPDATED_TYPE
        defaultSolShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllSolsByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where type not equals to DEFAULT_TYPE
        defaultSolShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the solList where type not equals to UPDATED_TYPE
        defaultSolShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllSolsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultSolShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the solList where type equals to UPDATED_TYPE
        defaultSolShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllSolsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where type is not null
        defaultSolShouldBeFound("type.specified=true");

        // Get all the solList where type is null
        defaultSolShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllSolsByTypeContainsSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where type contains DEFAULT_TYPE
        defaultSolShouldBeFound("type.contains=" + DEFAULT_TYPE);

        // Get all the solList where type contains UPDATED_TYPE
        defaultSolShouldNotBeFound("type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllSolsByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where type does not contain DEFAULT_TYPE
        defaultSolShouldNotBeFound("type.doesNotContain=" + DEFAULT_TYPE);

        // Get all the solList where type does not contain UPDATED_TYPE
        defaultSolShouldBeFound("type.doesNotContain=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllSolsByRichesseIsEqualToSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where richesse equals to DEFAULT_RICHESSE
        defaultSolShouldBeFound("richesse.equals=" + DEFAULT_RICHESSE);

        // Get all the solList where richesse equals to UPDATED_RICHESSE
        defaultSolShouldNotBeFound("richesse.equals=" + UPDATED_RICHESSE);
    }

    @Test
    @Transactional
    void getAllSolsByRichesseIsNotEqualToSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where richesse not equals to DEFAULT_RICHESSE
        defaultSolShouldNotBeFound("richesse.notEquals=" + DEFAULT_RICHESSE);

        // Get all the solList where richesse not equals to UPDATED_RICHESSE
        defaultSolShouldBeFound("richesse.notEquals=" + UPDATED_RICHESSE);
    }

    @Test
    @Transactional
    void getAllSolsByRichesseIsInShouldWork() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where richesse in DEFAULT_RICHESSE or UPDATED_RICHESSE
        defaultSolShouldBeFound("richesse.in=" + DEFAULT_RICHESSE + "," + UPDATED_RICHESSE);

        // Get all the solList where richesse equals to UPDATED_RICHESSE
        defaultSolShouldNotBeFound("richesse.in=" + UPDATED_RICHESSE);
    }

    @Test
    @Transactional
    void getAllSolsByRichesseIsNullOrNotNull() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where richesse is not null
        defaultSolShouldBeFound("richesse.specified=true");

        // Get all the solList where richesse is null
        defaultSolShouldNotBeFound("richesse.specified=false");
    }

    @Test
    @Transactional
    void getAllSolsByRichesseContainsSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where richesse contains DEFAULT_RICHESSE
        defaultSolShouldBeFound("richesse.contains=" + DEFAULT_RICHESSE);

        // Get all the solList where richesse contains UPDATED_RICHESSE
        defaultSolShouldNotBeFound("richesse.contains=" + UPDATED_RICHESSE);
    }

    @Test
    @Transactional
    void getAllSolsByRichesseNotContainsSomething() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        // Get all the solList where richesse does not contain DEFAULT_RICHESSE
        defaultSolShouldNotBeFound("richesse.doesNotContain=" + DEFAULT_RICHESSE);

        // Get all the solList where richesse does not contain UPDATED_RICHESSE
        defaultSolShouldBeFound("richesse.doesNotContain=" + UPDATED_RICHESSE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSolShouldBeFound(String filter) throws Exception {
        restSolMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sol.getId().intValue())))
            .andExpect(jsonPath("$.[*].phMin").value(hasItem(DEFAULT_PH_MIN.doubleValue())))
            .andExpect(jsonPath("$.[*].phMax").value(hasItem(DEFAULT_PH_MAX.doubleValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].richesse").value(hasItem(DEFAULT_RICHESSE)));

        // Check, that the count call also returns 1
        restSolMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSolShouldNotBeFound(String filter) throws Exception {
        restSolMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSolMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSol() throws Exception {
        // Get the sol
        restSolMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSol() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        int databaseSizeBeforeUpdate = solRepository.findAll().size();

        // Update the sol
        Sol updatedSol = solRepository.findById(sol.getId()).get();
        // Disconnect from session so that the updates on updatedSol are not directly saved in db
        em.detach(updatedSol);
        updatedSol.phMin(UPDATED_PH_MIN).phMax(UPDATED_PH_MAX).type(UPDATED_TYPE).richesse(UPDATED_RICHESSE);

        restSolMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSol.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSol))
            )
            .andExpect(status().isOk());

        // Validate the Sol in the database
        List<Sol> solList = solRepository.findAll();
        assertThat(solList).hasSize(databaseSizeBeforeUpdate);
        Sol testSol = solList.get(solList.size() - 1);
        assertThat(testSol.getPhMin()).isEqualTo(UPDATED_PH_MIN);
        assertThat(testSol.getPhMax()).isEqualTo(UPDATED_PH_MAX);
        assertThat(testSol.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSol.getRichesse()).isEqualTo(UPDATED_RICHESSE);
    }

    @Test
    @Transactional
    void putNonExistingSol() throws Exception {
        int databaseSizeBeforeUpdate = solRepository.findAll().size();
        sol.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSolMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sol.getId()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sol))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sol in the database
        List<Sol> solList = solRepository.findAll();
        assertThat(solList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSol() throws Exception {
        int databaseSizeBeforeUpdate = solRepository.findAll().size();
        sol.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sol))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sol in the database
        List<Sol> solList = solRepository.findAll();
        assertThat(solList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSol() throws Exception {
        int databaseSizeBeforeUpdate = solRepository.findAll().size();
        sol.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sol)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sol in the database
        List<Sol> solList = solRepository.findAll();
        assertThat(solList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSolWithPatch() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        int databaseSizeBeforeUpdate = solRepository.findAll().size();

        // Update the sol using partial update
        Sol partialUpdatedSol = new Sol();
        partialUpdatedSol.setId(sol.getId());

        partialUpdatedSol.phMin(UPDATED_PH_MIN).phMax(UPDATED_PH_MAX).type(UPDATED_TYPE);

        restSolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSol.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSol))
            )
            .andExpect(status().isOk());

        // Validate the Sol in the database
        List<Sol> solList = solRepository.findAll();
        assertThat(solList).hasSize(databaseSizeBeforeUpdate);
        Sol testSol = solList.get(solList.size() - 1);
        assertThat(testSol.getPhMin()).isEqualTo(UPDATED_PH_MIN);
        assertThat(testSol.getPhMax()).isEqualTo(UPDATED_PH_MAX);
        assertThat(testSol.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSol.getRichesse()).isEqualTo(DEFAULT_RICHESSE);
    }

    @Test
    @Transactional
    void fullUpdateSolWithPatch() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        int databaseSizeBeforeUpdate = solRepository.findAll().size();

        // Update the sol using partial update
        Sol partialUpdatedSol = new Sol();
        partialUpdatedSol.setId(sol.getId());

        partialUpdatedSol.phMin(UPDATED_PH_MIN).phMax(UPDATED_PH_MAX).type(UPDATED_TYPE).richesse(UPDATED_RICHESSE);

        restSolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSol.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSol))
            )
            .andExpect(status().isOk());

        // Validate the Sol in the database
        List<Sol> solList = solRepository.findAll();
        assertThat(solList).hasSize(databaseSizeBeforeUpdate);
        Sol testSol = solList.get(solList.size() - 1);
        assertThat(testSol.getPhMin()).isEqualTo(UPDATED_PH_MIN);
        assertThat(testSol.getPhMax()).isEqualTo(UPDATED_PH_MAX);
        assertThat(testSol.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSol.getRichesse()).isEqualTo(UPDATED_RICHESSE);
    }

    @Test
    @Transactional
    void patchNonExistingSol() throws Exception {
        int databaseSizeBeforeUpdate = solRepository.findAll().size();
        sol.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sol.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sol))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sol in the database
        List<Sol> solList = solRepository.findAll();
        assertThat(solList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSol() throws Exception {
        int databaseSizeBeforeUpdate = solRepository.findAll().size();
        sol.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sol))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sol in the database
        List<Sol> solList = solRepository.findAll();
        assertThat(solList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSol() throws Exception {
        int databaseSizeBeforeUpdate = solRepository.findAll().size();
        sol.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(sol)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sol in the database
        List<Sol> solList = solRepository.findAll();
        assertThat(solList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSol() throws Exception {
        // Initialize the database
        solRepository.saveAndFlush(sol);

        int databaseSizeBeforeDelete = solRepository.findAll().size();

        // Delete the sol
        restSolMockMvc.perform(delete(ENTITY_API_URL_ID, sol.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Sol> solList = solRepository.findAll();
        assertThat(solList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
