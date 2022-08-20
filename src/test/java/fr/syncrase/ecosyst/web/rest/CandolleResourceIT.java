package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Candolle;
import fr.syncrase.ecosyst.domain.Classification;
import fr.syncrase.ecosyst.repository.CandolleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link CandolleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CandolleResourceIT {

    private static final String ENTITY_API_URL = "/api/candolles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private CandolleRepository candolleRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCandolleMockMvc;

    private Candolle candolle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candolle createEntity(EntityManager em) {
        Candolle candolle = new Candolle();
        return candolle;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candolle createUpdatedEntity(EntityManager em) {
        Candolle candolle = new Candolle();
        return candolle;
    }

    @BeforeEach
    public void initTest() {
        candolle = createEntity(em);
    }

    @Test
    @Transactional
    void createCandolle() throws Exception {
        int databaseSizeBeforeCreate = candolleRepository.findAll().size();
        // Create the Candolle
        restCandolleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(candolle)))
            .andExpect(status().isCreated());

        // Validate the Candolle in the database
        List<Candolle> candolleList = candolleRepository.findAll();
        assertThat(candolleList).hasSize(databaseSizeBeforeCreate + 1);
        Candolle testCandolle = candolleList.get(candolleList.size() - 1);
    }

    @Test
    @Transactional
    void createCandolleWithExistingId() throws Exception {
        // Create the Candolle with an existing ID
        candolle.setId(1L);

        int databaseSizeBeforeCreate = candolleRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCandolleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(candolle)))
            .andExpect(status().isBadRequest());

        // Validate the Candolle in the database
        List<Candolle> candolleList = candolleRepository.findAll();
        assertThat(candolleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCandolles() throws Exception {
        // Initialize the database
        candolleRepository.saveAndFlush(candolle);

        // Get all the candolleList
        restCandolleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(candolle.getId().intValue())));
    }

    @Test
    @Transactional
    void getCandolle() throws Exception {
        // Initialize the database
        candolleRepository.saveAndFlush(candolle);

        // Get the candolle
        restCandolleMockMvc
            .perform(get(ENTITY_API_URL_ID, candolle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(candolle.getId().intValue()));
    }

    @Test
    @Transactional
    void getCandollesByIdFiltering() throws Exception {
        // Initialize the database
        candolleRepository.saveAndFlush(candolle);

        Long id = candolle.getId();

        defaultCandolleShouldBeFound("id.equals=" + id);
        defaultCandolleShouldNotBeFound("id.notEquals=" + id);

        defaultCandolleShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCandolleShouldNotBeFound("id.greaterThan=" + id);

        defaultCandolleShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCandolleShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCandollesByClassificationIsEqualToSomething() throws Exception {
        // Initialize the database
        candolleRepository.saveAndFlush(candolle);
        Classification classification;
        if (TestUtil.findAll(em, Classification.class).isEmpty()) {
            classification = ClassificationResourceIT.createEntity(em);
            em.persist(classification);
            em.flush();
        } else {
            classification = TestUtil.findAll(em, Classification.class).get(0);
        }
        em.persist(classification);
        em.flush();
        candolle.setClassification(classification);
        candolleRepository.saveAndFlush(candolle);
        Long classificationId = classification.getId();

        // Get all the candolleList where classification equals to classificationId
        defaultCandolleShouldBeFound("classificationId.equals=" + classificationId);

        // Get all the candolleList where classification equals to (classificationId + 1)
        defaultCandolleShouldNotBeFound("classificationId.equals=" + (classificationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCandolleShouldBeFound(String filter) throws Exception {
        restCandolleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(candolle.getId().intValue())));

        // Check, that the count call also returns 1
        restCandolleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCandolleShouldNotBeFound(String filter) throws Exception {
        restCandolleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCandolleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCandolle() throws Exception {
        // Get the candolle
        restCandolleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCandolle() throws Exception {
        // Initialize the database
        candolleRepository.saveAndFlush(candolle);

        int databaseSizeBeforeUpdate = candolleRepository.findAll().size();

        // Update the candolle
        Candolle updatedCandolle = candolleRepository.findById(candolle.getId()).get();
        // Disconnect from session so that the updates on updatedCandolle are not directly saved in db
        em.detach(updatedCandolle);

        restCandolleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCandolle.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCandolle))
            )
            .andExpect(status().isOk());

        // Validate the Candolle in the database
        List<Candolle> candolleList = candolleRepository.findAll();
        assertThat(candolleList).hasSize(databaseSizeBeforeUpdate);
        Candolle testCandolle = candolleList.get(candolleList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingCandolle() throws Exception {
        int databaseSizeBeforeUpdate = candolleRepository.findAll().size();
        candolle.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCandolleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, candolle.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(candolle))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candolle in the database
        List<Candolle> candolleList = candolleRepository.findAll();
        assertThat(candolleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCandolle() throws Exception {
        int databaseSizeBeforeUpdate = candolleRepository.findAll().size();
        candolle.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandolleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(candolle))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candolle in the database
        List<Candolle> candolleList = candolleRepository.findAll();
        assertThat(candolleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCandolle() throws Exception {
        int databaseSizeBeforeUpdate = candolleRepository.findAll().size();
        candolle.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandolleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(candolle)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Candolle in the database
        List<Candolle> candolleList = candolleRepository.findAll();
        assertThat(candolleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCandolleWithPatch() throws Exception {
        // Initialize the database
        candolleRepository.saveAndFlush(candolle);

        int databaseSizeBeforeUpdate = candolleRepository.findAll().size();

        // Update the candolle using partial update
        Candolle partialUpdatedCandolle = new Candolle();
        partialUpdatedCandolle.setId(candolle.getId());

        restCandolleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCandolle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCandolle))
            )
            .andExpect(status().isOk());

        // Validate the Candolle in the database
        List<Candolle> candolleList = candolleRepository.findAll();
        assertThat(candolleList).hasSize(databaseSizeBeforeUpdate);
        Candolle testCandolle = candolleList.get(candolleList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateCandolleWithPatch() throws Exception {
        // Initialize the database
        candolleRepository.saveAndFlush(candolle);

        int databaseSizeBeforeUpdate = candolleRepository.findAll().size();

        // Update the candolle using partial update
        Candolle partialUpdatedCandolle = new Candolle();
        partialUpdatedCandolle.setId(candolle.getId());

        restCandolleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCandolle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCandolle))
            )
            .andExpect(status().isOk());

        // Validate the Candolle in the database
        List<Candolle> candolleList = candolleRepository.findAll();
        assertThat(candolleList).hasSize(databaseSizeBeforeUpdate);
        Candolle testCandolle = candolleList.get(candolleList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingCandolle() throws Exception {
        int databaseSizeBeforeUpdate = candolleRepository.findAll().size();
        candolle.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCandolleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, candolle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(candolle))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candolle in the database
        List<Candolle> candolleList = candolleRepository.findAll();
        assertThat(candolleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCandolle() throws Exception {
        int databaseSizeBeforeUpdate = candolleRepository.findAll().size();
        candolle.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandolleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(candolle))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candolle in the database
        List<Candolle> candolleList = candolleRepository.findAll();
        assertThat(candolleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCandolle() throws Exception {
        int databaseSizeBeforeUpdate = candolleRepository.findAll().size();
        candolle.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandolleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(candolle)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Candolle in the database
        List<Candolle> candolleList = candolleRepository.findAll();
        assertThat(candolleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCandolle() throws Exception {
        // Initialize the database
        candolleRepository.saveAndFlush(candolle);

        int databaseSizeBeforeDelete = candolleRepository.findAll().size();

        // Delete the candolle
        restCandolleMockMvc
            .perform(delete(ENTITY_API_URL_ID, candolle.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Candolle> candolleList = candolleRepository.findAll();
        assertThat(candolleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
