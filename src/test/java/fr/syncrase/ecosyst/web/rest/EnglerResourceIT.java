package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Classification;
import fr.syncrase.ecosyst.domain.Engler;
import fr.syncrase.ecosyst.repository.EnglerRepository;
import fr.syncrase.ecosyst.service.criteria.EnglerCriteria;
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
 * Integration tests for the {@link EnglerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EnglerResourceIT {

    private static final String ENTITY_API_URL = "/api/englers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private EnglerRepository englerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnglerMockMvc;

    private Engler engler;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Engler createEntity(EntityManager em) {
        Engler engler = new Engler();
        return engler;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Engler createUpdatedEntity(EntityManager em) {
        Engler engler = new Engler();
        return engler;
    }

    @BeforeEach
    public void initTest() {
        engler = createEntity(em);
    }

    @Test
    @Transactional
    void createEngler() throws Exception {
        int databaseSizeBeforeCreate = englerRepository.findAll().size();
        // Create the Engler
        restEnglerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engler)))
            .andExpect(status().isCreated());

        // Validate the Engler in the database
        List<Engler> englerList = englerRepository.findAll();
        assertThat(englerList).hasSize(databaseSizeBeforeCreate + 1);
        Engler testEngler = englerList.get(englerList.size() - 1);
    }

    @Test
    @Transactional
    void createEnglerWithExistingId() throws Exception {
        // Create the Engler with an existing ID
        engler.setId(1L);

        int databaseSizeBeforeCreate = englerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnglerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engler)))
            .andExpect(status().isBadRequest());

        // Validate the Engler in the database
        List<Engler> englerList = englerRepository.findAll();
        assertThat(englerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEnglers() throws Exception {
        // Initialize the database
        englerRepository.saveAndFlush(engler);

        // Get all the englerList
        restEnglerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(engler.getId().intValue())));
    }

    @Test
    @Transactional
    void getEngler() throws Exception {
        // Initialize the database
        englerRepository.saveAndFlush(engler);

        // Get the engler
        restEnglerMockMvc
            .perform(get(ENTITY_API_URL_ID, engler.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(engler.getId().intValue()));
    }

    @Test
    @Transactional
    void getEnglersByIdFiltering() throws Exception {
        // Initialize the database
        englerRepository.saveAndFlush(engler);

        Long id = engler.getId();

        defaultEnglerShouldBeFound("id.equals=" + id);
        defaultEnglerShouldNotBeFound("id.notEquals=" + id);

        defaultEnglerShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEnglerShouldNotBeFound("id.greaterThan=" + id);

        defaultEnglerShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEnglerShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEnglersByClassificationIsEqualToSomething() throws Exception {
        // Initialize the database
        englerRepository.saveAndFlush(engler);
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
        engler.setClassification(classification);
        englerRepository.saveAndFlush(engler);
        Long classificationId = classification.getId();

        // Get all the englerList where classification equals to classificationId
        defaultEnglerShouldBeFound("classificationId.equals=" + classificationId);

        // Get all the englerList where classification equals to (classificationId + 1)
        defaultEnglerShouldNotBeFound("classificationId.equals=" + (classificationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEnglerShouldBeFound(String filter) throws Exception {
        restEnglerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(engler.getId().intValue())));

        // Check, that the count call also returns 1
        restEnglerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEnglerShouldNotBeFound(String filter) throws Exception {
        restEnglerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEnglerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEngler() throws Exception {
        // Get the engler
        restEnglerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEngler() throws Exception {
        // Initialize the database
        englerRepository.saveAndFlush(engler);

        int databaseSizeBeforeUpdate = englerRepository.findAll().size();

        // Update the engler
        Engler updatedEngler = englerRepository.findById(engler.getId()).get();
        // Disconnect from session so that the updates on updatedEngler are not directly saved in db
        em.detach(updatedEngler);

        restEnglerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEngler.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEngler))
            )
            .andExpect(status().isOk());

        // Validate the Engler in the database
        List<Engler> englerList = englerRepository.findAll();
        assertThat(englerList).hasSize(databaseSizeBeforeUpdate);
        Engler testEngler = englerList.get(englerList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingEngler() throws Exception {
        int databaseSizeBeforeUpdate = englerRepository.findAll().size();
        engler.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnglerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, engler.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(engler))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engler in the database
        List<Engler> englerList = englerRepository.findAll();
        assertThat(englerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEngler() throws Exception {
        int databaseSizeBeforeUpdate = englerRepository.findAll().size();
        engler.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnglerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(engler))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engler in the database
        List<Engler> englerList = englerRepository.findAll();
        assertThat(englerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEngler() throws Exception {
        int databaseSizeBeforeUpdate = englerRepository.findAll().size();
        engler.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnglerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engler)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Engler in the database
        List<Engler> englerList = englerRepository.findAll();
        assertThat(englerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEnglerWithPatch() throws Exception {
        // Initialize the database
        englerRepository.saveAndFlush(engler);

        int databaseSizeBeforeUpdate = englerRepository.findAll().size();

        // Update the engler using partial update
        Engler partialUpdatedEngler = new Engler();
        partialUpdatedEngler.setId(engler.getId());

        restEnglerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEngler.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEngler))
            )
            .andExpect(status().isOk());

        // Validate the Engler in the database
        List<Engler> englerList = englerRepository.findAll();
        assertThat(englerList).hasSize(databaseSizeBeforeUpdate);
        Engler testEngler = englerList.get(englerList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateEnglerWithPatch() throws Exception {
        // Initialize the database
        englerRepository.saveAndFlush(engler);

        int databaseSizeBeforeUpdate = englerRepository.findAll().size();

        // Update the engler using partial update
        Engler partialUpdatedEngler = new Engler();
        partialUpdatedEngler.setId(engler.getId());

        restEnglerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEngler.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEngler))
            )
            .andExpect(status().isOk());

        // Validate the Engler in the database
        List<Engler> englerList = englerRepository.findAll();
        assertThat(englerList).hasSize(databaseSizeBeforeUpdate);
        Engler testEngler = englerList.get(englerList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingEngler() throws Exception {
        int databaseSizeBeforeUpdate = englerRepository.findAll().size();
        engler.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnglerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, engler.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(engler))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engler in the database
        List<Engler> englerList = englerRepository.findAll();
        assertThat(englerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEngler() throws Exception {
        int databaseSizeBeforeUpdate = englerRepository.findAll().size();
        engler.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnglerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(engler))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engler in the database
        List<Engler> englerList = englerRepository.findAll();
        assertThat(englerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEngler() throws Exception {
        int databaseSizeBeforeUpdate = englerRepository.findAll().size();
        engler.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnglerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(engler)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Engler in the database
        List<Engler> englerList = englerRepository.findAll();
        assertThat(englerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEngler() throws Exception {
        // Initialize the database
        englerRepository.saveAndFlush(engler);

        int databaseSizeBeforeDelete = englerRepository.findAll().size();

        // Delete the engler
        restEnglerMockMvc
            .perform(delete(ENTITY_API_URL_ID, engler.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Engler> englerList = englerRepository.findAll();
        assertThat(englerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
