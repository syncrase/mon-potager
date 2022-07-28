package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Ensoleillement;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.repository.EnsoleillementRepository;
import fr.syncrase.ecosyst.service.criteria.EnsoleillementCriteria;
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
 * Integration tests for the {@link EnsoleillementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EnsoleillementResourceIT {

    private static final String DEFAULT_ORIENTATION = "AAAAAAAAAA";
    private static final String UPDATED_ORIENTATION = "BBBBBBBBBB";

    private static final Double DEFAULT_ENSOLEILEMENT = 1D;
    private static final Double UPDATED_ENSOLEILEMENT = 2D;
    private static final Double SMALLER_ENSOLEILEMENT = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/ensoleillements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EnsoleillementRepository ensoleillementRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnsoleillementMockMvc;

    private Ensoleillement ensoleillement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ensoleillement createEntity(EntityManager em) {
        Ensoleillement ensoleillement = new Ensoleillement().orientation(DEFAULT_ORIENTATION).ensoleilement(DEFAULT_ENSOLEILEMENT);
        return ensoleillement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ensoleillement createUpdatedEntity(EntityManager em) {
        Ensoleillement ensoleillement = new Ensoleillement().orientation(UPDATED_ORIENTATION).ensoleilement(UPDATED_ENSOLEILEMENT);
        return ensoleillement;
    }

    @BeforeEach
    public void initTest() {
        ensoleillement = createEntity(em);
    }

    @Test
    @Transactional
    void createEnsoleillement() throws Exception {
        int databaseSizeBeforeCreate = ensoleillementRepository.findAll().size();
        // Create the Ensoleillement
        restEnsoleillementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ensoleillement))
            )
            .andExpect(status().isCreated());

        // Validate the Ensoleillement in the database
        List<Ensoleillement> ensoleillementList = ensoleillementRepository.findAll();
        assertThat(ensoleillementList).hasSize(databaseSizeBeforeCreate + 1);
        Ensoleillement testEnsoleillement = ensoleillementList.get(ensoleillementList.size() - 1);
        assertThat(testEnsoleillement.getOrientation()).isEqualTo(DEFAULT_ORIENTATION);
        assertThat(testEnsoleillement.getEnsoleilement()).isEqualTo(DEFAULT_ENSOLEILEMENT);
    }

    @Test
    @Transactional
    void createEnsoleillementWithExistingId() throws Exception {
        // Create the Ensoleillement with an existing ID
        ensoleillement.setId(1L);

        int databaseSizeBeforeCreate = ensoleillementRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnsoleillementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ensoleillement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ensoleillement in the database
        List<Ensoleillement> ensoleillementList = ensoleillementRepository.findAll();
        assertThat(ensoleillementList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEnsoleillements() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get all the ensoleillementList
        restEnsoleillementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ensoleillement.getId().intValue())))
            .andExpect(jsonPath("$.[*].orientation").value(hasItem(DEFAULT_ORIENTATION)))
            .andExpect(jsonPath("$.[*].ensoleilement").value(hasItem(DEFAULT_ENSOLEILEMENT.doubleValue())));
    }

    @Test
    @Transactional
    void getEnsoleillement() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get the ensoleillement
        restEnsoleillementMockMvc
            .perform(get(ENTITY_API_URL_ID, ensoleillement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ensoleillement.getId().intValue()))
            .andExpect(jsonPath("$.orientation").value(DEFAULT_ORIENTATION))
            .andExpect(jsonPath("$.ensoleilement").value(DEFAULT_ENSOLEILEMENT.doubleValue()));
    }

    @Test
    @Transactional
    void getEnsoleillementsByIdFiltering() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        Long id = ensoleillement.getId();

        defaultEnsoleillementShouldBeFound("id.equals=" + id);
        defaultEnsoleillementShouldNotBeFound("id.notEquals=" + id);

        defaultEnsoleillementShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEnsoleillementShouldNotBeFound("id.greaterThan=" + id);

        defaultEnsoleillementShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEnsoleillementShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEnsoleillementsByOrientationIsEqualToSomething() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get all the ensoleillementList where orientation equals to DEFAULT_ORIENTATION
        defaultEnsoleillementShouldBeFound("orientation.equals=" + DEFAULT_ORIENTATION);

        // Get all the ensoleillementList where orientation equals to UPDATED_ORIENTATION
        defaultEnsoleillementShouldNotBeFound("orientation.equals=" + UPDATED_ORIENTATION);
    }

    @Test
    @Transactional
    void getAllEnsoleillementsByOrientationIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get all the ensoleillementList where orientation not equals to DEFAULT_ORIENTATION
        defaultEnsoleillementShouldNotBeFound("orientation.notEquals=" + DEFAULT_ORIENTATION);

        // Get all the ensoleillementList where orientation not equals to UPDATED_ORIENTATION
        defaultEnsoleillementShouldBeFound("orientation.notEquals=" + UPDATED_ORIENTATION);
    }

    @Test
    @Transactional
    void getAllEnsoleillementsByOrientationIsInShouldWork() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get all the ensoleillementList where orientation in DEFAULT_ORIENTATION or UPDATED_ORIENTATION
        defaultEnsoleillementShouldBeFound("orientation.in=" + DEFAULT_ORIENTATION + "," + UPDATED_ORIENTATION);

        // Get all the ensoleillementList where orientation equals to UPDATED_ORIENTATION
        defaultEnsoleillementShouldNotBeFound("orientation.in=" + UPDATED_ORIENTATION);
    }

    @Test
    @Transactional
    void getAllEnsoleillementsByOrientationIsNullOrNotNull() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get all the ensoleillementList where orientation is not null
        defaultEnsoleillementShouldBeFound("orientation.specified=true");

        // Get all the ensoleillementList where orientation is null
        defaultEnsoleillementShouldNotBeFound("orientation.specified=false");
    }

    @Test
    @Transactional
    void getAllEnsoleillementsByOrientationContainsSomething() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get all the ensoleillementList where orientation contains DEFAULT_ORIENTATION
        defaultEnsoleillementShouldBeFound("orientation.contains=" + DEFAULT_ORIENTATION);

        // Get all the ensoleillementList where orientation contains UPDATED_ORIENTATION
        defaultEnsoleillementShouldNotBeFound("orientation.contains=" + UPDATED_ORIENTATION);
    }

    @Test
    @Transactional
    void getAllEnsoleillementsByOrientationNotContainsSomething() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get all the ensoleillementList where orientation does not contain DEFAULT_ORIENTATION
        defaultEnsoleillementShouldNotBeFound("orientation.doesNotContain=" + DEFAULT_ORIENTATION);

        // Get all the ensoleillementList where orientation does not contain UPDATED_ORIENTATION
        defaultEnsoleillementShouldBeFound("orientation.doesNotContain=" + UPDATED_ORIENTATION);
    }

    @Test
    @Transactional
    void getAllEnsoleillementsByEnsoleilementIsEqualToSomething() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get all the ensoleillementList where ensoleilement equals to DEFAULT_ENSOLEILEMENT
        defaultEnsoleillementShouldBeFound("ensoleilement.equals=" + DEFAULT_ENSOLEILEMENT);

        // Get all the ensoleillementList where ensoleilement equals to UPDATED_ENSOLEILEMENT
        defaultEnsoleillementShouldNotBeFound("ensoleilement.equals=" + UPDATED_ENSOLEILEMENT);
    }

    @Test
    @Transactional
    void getAllEnsoleillementsByEnsoleilementIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get all the ensoleillementList where ensoleilement not equals to DEFAULT_ENSOLEILEMENT
        defaultEnsoleillementShouldNotBeFound("ensoleilement.notEquals=" + DEFAULT_ENSOLEILEMENT);

        // Get all the ensoleillementList where ensoleilement not equals to UPDATED_ENSOLEILEMENT
        defaultEnsoleillementShouldBeFound("ensoleilement.notEquals=" + UPDATED_ENSOLEILEMENT);
    }

    @Test
    @Transactional
    void getAllEnsoleillementsByEnsoleilementIsInShouldWork() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get all the ensoleillementList where ensoleilement in DEFAULT_ENSOLEILEMENT or UPDATED_ENSOLEILEMENT
        defaultEnsoleillementShouldBeFound("ensoleilement.in=" + DEFAULT_ENSOLEILEMENT + "," + UPDATED_ENSOLEILEMENT);

        // Get all the ensoleillementList where ensoleilement equals to UPDATED_ENSOLEILEMENT
        defaultEnsoleillementShouldNotBeFound("ensoleilement.in=" + UPDATED_ENSOLEILEMENT);
    }

    @Test
    @Transactional
    void getAllEnsoleillementsByEnsoleilementIsNullOrNotNull() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get all the ensoleillementList where ensoleilement is not null
        defaultEnsoleillementShouldBeFound("ensoleilement.specified=true");

        // Get all the ensoleillementList where ensoleilement is null
        defaultEnsoleillementShouldNotBeFound("ensoleilement.specified=false");
    }

    @Test
    @Transactional
    void getAllEnsoleillementsByEnsoleilementIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get all the ensoleillementList where ensoleilement is greater than or equal to DEFAULT_ENSOLEILEMENT
        defaultEnsoleillementShouldBeFound("ensoleilement.greaterThanOrEqual=" + DEFAULT_ENSOLEILEMENT);

        // Get all the ensoleillementList where ensoleilement is greater than or equal to UPDATED_ENSOLEILEMENT
        defaultEnsoleillementShouldNotBeFound("ensoleilement.greaterThanOrEqual=" + UPDATED_ENSOLEILEMENT);
    }

    @Test
    @Transactional
    void getAllEnsoleillementsByEnsoleilementIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get all the ensoleillementList where ensoleilement is less than or equal to DEFAULT_ENSOLEILEMENT
        defaultEnsoleillementShouldBeFound("ensoleilement.lessThanOrEqual=" + DEFAULT_ENSOLEILEMENT);

        // Get all the ensoleillementList where ensoleilement is less than or equal to SMALLER_ENSOLEILEMENT
        defaultEnsoleillementShouldNotBeFound("ensoleilement.lessThanOrEqual=" + SMALLER_ENSOLEILEMENT);
    }

    @Test
    @Transactional
    void getAllEnsoleillementsByEnsoleilementIsLessThanSomething() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get all the ensoleillementList where ensoleilement is less than DEFAULT_ENSOLEILEMENT
        defaultEnsoleillementShouldNotBeFound("ensoleilement.lessThan=" + DEFAULT_ENSOLEILEMENT);

        // Get all the ensoleillementList where ensoleilement is less than UPDATED_ENSOLEILEMENT
        defaultEnsoleillementShouldBeFound("ensoleilement.lessThan=" + UPDATED_ENSOLEILEMENT);
    }

    @Test
    @Transactional
    void getAllEnsoleillementsByEnsoleilementIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        // Get all the ensoleillementList where ensoleilement is greater than DEFAULT_ENSOLEILEMENT
        defaultEnsoleillementShouldNotBeFound("ensoleilement.greaterThan=" + DEFAULT_ENSOLEILEMENT);

        // Get all the ensoleillementList where ensoleilement is greater than SMALLER_ENSOLEILEMENT
        defaultEnsoleillementShouldBeFound("ensoleilement.greaterThan=" + SMALLER_ENSOLEILEMENT);
    }

    @Test
    @Transactional
    void getAllEnsoleillementsByPlanteIsEqualToSomething() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);
        Plante plante;
        if (TestUtil.findAll(em, Plante.class).isEmpty()) {
            plante = PlanteResourceIT.createEntity(em);
            em.persist(plante);
            em.flush();
        } else {
            plante = TestUtil.findAll(em, Plante.class).get(0);
        }
        em.persist(plante);
        em.flush();
        ensoleillement.setPlante(plante);
        ensoleillementRepository.saveAndFlush(ensoleillement);
        Long planteId = plante.getId();

        // Get all the ensoleillementList where plante equals to planteId
        defaultEnsoleillementShouldBeFound("planteId.equals=" + planteId);

        // Get all the ensoleillementList where plante equals to (planteId + 1)
        defaultEnsoleillementShouldNotBeFound("planteId.equals=" + (planteId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEnsoleillementShouldBeFound(String filter) throws Exception {
        restEnsoleillementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ensoleillement.getId().intValue())))
            .andExpect(jsonPath("$.[*].orientation").value(hasItem(DEFAULT_ORIENTATION)))
            .andExpect(jsonPath("$.[*].ensoleilement").value(hasItem(DEFAULT_ENSOLEILEMENT.doubleValue())));

        // Check, that the count call also returns 1
        restEnsoleillementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEnsoleillementShouldNotBeFound(String filter) throws Exception {
        restEnsoleillementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEnsoleillementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEnsoleillement() throws Exception {
        // Get the ensoleillement
        restEnsoleillementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEnsoleillement() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        int databaseSizeBeforeUpdate = ensoleillementRepository.findAll().size();

        // Update the ensoleillement
        Ensoleillement updatedEnsoleillement = ensoleillementRepository.findById(ensoleillement.getId()).get();
        // Disconnect from session so that the updates on updatedEnsoleillement are not directly saved in db
        em.detach(updatedEnsoleillement);
        updatedEnsoleillement.orientation(UPDATED_ORIENTATION).ensoleilement(UPDATED_ENSOLEILEMENT);

        restEnsoleillementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEnsoleillement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEnsoleillement))
            )
            .andExpect(status().isOk());

        // Validate the Ensoleillement in the database
        List<Ensoleillement> ensoleillementList = ensoleillementRepository.findAll();
        assertThat(ensoleillementList).hasSize(databaseSizeBeforeUpdate);
        Ensoleillement testEnsoleillement = ensoleillementList.get(ensoleillementList.size() - 1);
        assertThat(testEnsoleillement.getOrientation()).isEqualTo(UPDATED_ORIENTATION);
        assertThat(testEnsoleillement.getEnsoleilement()).isEqualTo(UPDATED_ENSOLEILEMENT);
    }

    @Test
    @Transactional
    void putNonExistingEnsoleillement() throws Exception {
        int databaseSizeBeforeUpdate = ensoleillementRepository.findAll().size();
        ensoleillement.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnsoleillementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ensoleillement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ensoleillement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ensoleillement in the database
        List<Ensoleillement> ensoleillementList = ensoleillementRepository.findAll();
        assertThat(ensoleillementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEnsoleillement() throws Exception {
        int databaseSizeBeforeUpdate = ensoleillementRepository.findAll().size();
        ensoleillement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnsoleillementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ensoleillement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ensoleillement in the database
        List<Ensoleillement> ensoleillementList = ensoleillementRepository.findAll();
        assertThat(ensoleillementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEnsoleillement() throws Exception {
        int databaseSizeBeforeUpdate = ensoleillementRepository.findAll().size();
        ensoleillement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnsoleillementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ensoleillement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ensoleillement in the database
        List<Ensoleillement> ensoleillementList = ensoleillementRepository.findAll();
        assertThat(ensoleillementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEnsoleillementWithPatch() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        int databaseSizeBeforeUpdate = ensoleillementRepository.findAll().size();

        // Update the ensoleillement using partial update
        Ensoleillement partialUpdatedEnsoleillement = new Ensoleillement();
        partialUpdatedEnsoleillement.setId(ensoleillement.getId());

        partialUpdatedEnsoleillement.orientation(UPDATED_ORIENTATION).ensoleilement(UPDATED_ENSOLEILEMENT);

        restEnsoleillementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnsoleillement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEnsoleillement))
            )
            .andExpect(status().isOk());

        // Validate the Ensoleillement in the database
        List<Ensoleillement> ensoleillementList = ensoleillementRepository.findAll();
        assertThat(ensoleillementList).hasSize(databaseSizeBeforeUpdate);
        Ensoleillement testEnsoleillement = ensoleillementList.get(ensoleillementList.size() - 1);
        assertThat(testEnsoleillement.getOrientation()).isEqualTo(UPDATED_ORIENTATION);
        assertThat(testEnsoleillement.getEnsoleilement()).isEqualTo(UPDATED_ENSOLEILEMENT);
    }

    @Test
    @Transactional
    void fullUpdateEnsoleillementWithPatch() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        int databaseSizeBeforeUpdate = ensoleillementRepository.findAll().size();

        // Update the ensoleillement using partial update
        Ensoleillement partialUpdatedEnsoleillement = new Ensoleillement();
        partialUpdatedEnsoleillement.setId(ensoleillement.getId());

        partialUpdatedEnsoleillement.orientation(UPDATED_ORIENTATION).ensoleilement(UPDATED_ENSOLEILEMENT);

        restEnsoleillementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnsoleillement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEnsoleillement))
            )
            .andExpect(status().isOk());

        // Validate the Ensoleillement in the database
        List<Ensoleillement> ensoleillementList = ensoleillementRepository.findAll();
        assertThat(ensoleillementList).hasSize(databaseSizeBeforeUpdate);
        Ensoleillement testEnsoleillement = ensoleillementList.get(ensoleillementList.size() - 1);
        assertThat(testEnsoleillement.getOrientation()).isEqualTo(UPDATED_ORIENTATION);
        assertThat(testEnsoleillement.getEnsoleilement()).isEqualTo(UPDATED_ENSOLEILEMENT);
    }

    @Test
    @Transactional
    void patchNonExistingEnsoleillement() throws Exception {
        int databaseSizeBeforeUpdate = ensoleillementRepository.findAll().size();
        ensoleillement.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnsoleillementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ensoleillement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ensoleillement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ensoleillement in the database
        List<Ensoleillement> ensoleillementList = ensoleillementRepository.findAll();
        assertThat(ensoleillementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEnsoleillement() throws Exception {
        int databaseSizeBeforeUpdate = ensoleillementRepository.findAll().size();
        ensoleillement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnsoleillementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ensoleillement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ensoleillement in the database
        List<Ensoleillement> ensoleillementList = ensoleillementRepository.findAll();
        assertThat(ensoleillementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEnsoleillement() throws Exception {
        int databaseSizeBeforeUpdate = ensoleillementRepository.findAll().size();
        ensoleillement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnsoleillementMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(ensoleillement))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ensoleillement in the database
        List<Ensoleillement> ensoleillementList = ensoleillementRepository.findAll();
        assertThat(ensoleillementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEnsoleillement() throws Exception {
        // Initialize the database
        ensoleillementRepository.saveAndFlush(ensoleillement);

        int databaseSizeBeforeDelete = ensoleillementRepository.findAll().size();

        // Delete the ensoleillement
        restEnsoleillementMockMvc
            .perform(delete(ENTITY_API_URL_ID, ensoleillement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ensoleillement> ensoleillementList = ensoleillementRepository.findAll();
        assertThat(ensoleillementList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
