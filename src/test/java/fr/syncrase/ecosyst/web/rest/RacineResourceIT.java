package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Racine;
import fr.syncrase.ecosyst.repository.RacineRepository;
import fr.syncrase.ecosyst.service.criteria.RacineCriteria;
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
 * Integration tests for the {@link RacineResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RacineResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/racines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RacineRepository racineRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRacineMockMvc;

    private Racine racine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Racine createEntity(EntityManager em) {
        Racine racine = new Racine().type(DEFAULT_TYPE);
        return racine;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Racine createUpdatedEntity(EntityManager em) {
        Racine racine = new Racine().type(UPDATED_TYPE);
        return racine;
    }

    @BeforeEach
    public void initTest() {
        racine = createEntity(em);
    }

    @Test
    @Transactional
    void createRacine() throws Exception {
        int databaseSizeBeforeCreate = racineRepository.findAll().size();
        // Create the Racine
        restRacineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(racine)))
            .andExpect(status().isCreated());

        // Validate the Racine in the database
        List<Racine> racineList = racineRepository.findAll();
        assertThat(racineList).hasSize(databaseSizeBeforeCreate + 1);
        Racine testRacine = racineList.get(racineList.size() - 1);
        assertThat(testRacine.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void createRacineWithExistingId() throws Exception {
        // Create the Racine with an existing ID
        racine.setId(1L);

        int databaseSizeBeforeCreate = racineRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRacineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(racine)))
            .andExpect(status().isBadRequest());

        // Validate the Racine in the database
        List<Racine> racineList = racineRepository.findAll();
        assertThat(racineList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRacines() throws Exception {
        // Initialize the database
        racineRepository.saveAndFlush(racine);

        // Get all the racineList
        restRacineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(racine.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));
    }

    @Test
    @Transactional
    void getRacine() throws Exception {
        // Initialize the database
        racineRepository.saveAndFlush(racine);

        // Get the racine
        restRacineMockMvc
            .perform(get(ENTITY_API_URL_ID, racine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(racine.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE));
    }

    @Test
    @Transactional
    void getRacinesByIdFiltering() throws Exception {
        // Initialize the database
        racineRepository.saveAndFlush(racine);

        Long id = racine.getId();

        defaultRacineShouldBeFound("id.equals=" + id);
        defaultRacineShouldNotBeFound("id.notEquals=" + id);

        defaultRacineShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRacineShouldNotBeFound("id.greaterThan=" + id);

        defaultRacineShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRacineShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRacinesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        racineRepository.saveAndFlush(racine);

        // Get all the racineList where type equals to DEFAULT_TYPE
        defaultRacineShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the racineList where type equals to UPDATED_TYPE
        defaultRacineShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllRacinesByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        racineRepository.saveAndFlush(racine);

        // Get all the racineList where type not equals to DEFAULT_TYPE
        defaultRacineShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the racineList where type not equals to UPDATED_TYPE
        defaultRacineShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllRacinesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        racineRepository.saveAndFlush(racine);

        // Get all the racineList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultRacineShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the racineList where type equals to UPDATED_TYPE
        defaultRacineShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllRacinesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        racineRepository.saveAndFlush(racine);

        // Get all the racineList where type is not null
        defaultRacineShouldBeFound("type.specified=true");

        // Get all the racineList where type is null
        defaultRacineShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllRacinesByTypeContainsSomething() throws Exception {
        // Initialize the database
        racineRepository.saveAndFlush(racine);

        // Get all the racineList where type contains DEFAULT_TYPE
        defaultRacineShouldBeFound("type.contains=" + DEFAULT_TYPE);

        // Get all the racineList where type contains UPDATED_TYPE
        defaultRacineShouldNotBeFound("type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllRacinesByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        racineRepository.saveAndFlush(racine);

        // Get all the racineList where type does not contain DEFAULT_TYPE
        defaultRacineShouldNotBeFound("type.doesNotContain=" + DEFAULT_TYPE);

        // Get all the racineList where type does not contain UPDATED_TYPE
        defaultRacineShouldBeFound("type.doesNotContain=" + UPDATED_TYPE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRacineShouldBeFound(String filter) throws Exception {
        restRacineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(racine.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));

        // Check, that the count call also returns 1
        restRacineMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRacineShouldNotBeFound(String filter) throws Exception {
        restRacineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRacineMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRacine() throws Exception {
        // Get the racine
        restRacineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRacine() throws Exception {
        // Initialize the database
        racineRepository.saveAndFlush(racine);

        int databaseSizeBeforeUpdate = racineRepository.findAll().size();

        // Update the racine
        Racine updatedRacine = racineRepository.findById(racine.getId()).get();
        // Disconnect from session so that the updates on updatedRacine are not directly saved in db
        em.detach(updatedRacine);
        updatedRacine.type(UPDATED_TYPE);

        restRacineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRacine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRacine))
            )
            .andExpect(status().isOk());

        // Validate the Racine in the database
        List<Racine> racineList = racineRepository.findAll();
        assertThat(racineList).hasSize(databaseSizeBeforeUpdate);
        Racine testRacine = racineList.get(racineList.size() - 1);
        assertThat(testRacine.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingRacine() throws Exception {
        int databaseSizeBeforeUpdate = racineRepository.findAll().size();
        racine.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRacineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, racine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(racine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Racine in the database
        List<Racine> racineList = racineRepository.findAll();
        assertThat(racineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRacine() throws Exception {
        int databaseSizeBeforeUpdate = racineRepository.findAll().size();
        racine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRacineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(racine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Racine in the database
        List<Racine> racineList = racineRepository.findAll();
        assertThat(racineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRacine() throws Exception {
        int databaseSizeBeforeUpdate = racineRepository.findAll().size();
        racine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRacineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(racine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Racine in the database
        List<Racine> racineList = racineRepository.findAll();
        assertThat(racineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRacineWithPatch() throws Exception {
        // Initialize the database
        racineRepository.saveAndFlush(racine);

        int databaseSizeBeforeUpdate = racineRepository.findAll().size();

        // Update the racine using partial update
        Racine partialUpdatedRacine = new Racine();
        partialUpdatedRacine.setId(racine.getId());

        partialUpdatedRacine.type(UPDATED_TYPE);

        restRacineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRacine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRacine))
            )
            .andExpect(status().isOk());

        // Validate the Racine in the database
        List<Racine> racineList = racineRepository.findAll();
        assertThat(racineList).hasSize(databaseSizeBeforeUpdate);
        Racine testRacine = racineList.get(racineList.size() - 1);
        assertThat(testRacine.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateRacineWithPatch() throws Exception {
        // Initialize the database
        racineRepository.saveAndFlush(racine);

        int databaseSizeBeforeUpdate = racineRepository.findAll().size();

        // Update the racine using partial update
        Racine partialUpdatedRacine = new Racine();
        partialUpdatedRacine.setId(racine.getId());

        partialUpdatedRacine.type(UPDATED_TYPE);

        restRacineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRacine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRacine))
            )
            .andExpect(status().isOk());

        // Validate the Racine in the database
        List<Racine> racineList = racineRepository.findAll();
        assertThat(racineList).hasSize(databaseSizeBeforeUpdate);
        Racine testRacine = racineList.get(racineList.size() - 1);
        assertThat(testRacine.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingRacine() throws Exception {
        int databaseSizeBeforeUpdate = racineRepository.findAll().size();
        racine.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRacineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, racine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(racine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Racine in the database
        List<Racine> racineList = racineRepository.findAll();
        assertThat(racineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRacine() throws Exception {
        int databaseSizeBeforeUpdate = racineRepository.findAll().size();
        racine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRacineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(racine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Racine in the database
        List<Racine> racineList = racineRepository.findAll();
        assertThat(racineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRacine() throws Exception {
        int databaseSizeBeforeUpdate = racineRepository.findAll().size();
        racine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRacineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(racine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Racine in the database
        List<Racine> racineList = racineRepository.findAll();
        assertThat(racineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRacine() throws Exception {
        // Initialize the database
        racineRepository.saveAndFlush(racine);

        int databaseSizeBeforeDelete = racineRepository.findAll().size();

        // Delete the racine
        restRacineMockMvc
            .perform(delete(ENTITY_API_URL_ID, racine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Racine> racineList = racineRepository.findAll();
        assertThat(racineList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
