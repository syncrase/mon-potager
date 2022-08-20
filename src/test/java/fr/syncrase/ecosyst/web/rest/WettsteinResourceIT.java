package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Classification;
import fr.syncrase.ecosyst.domain.Wettstein;
import fr.syncrase.ecosyst.repository.WettsteinRepository;
import fr.syncrase.ecosyst.service.criteria.WettsteinCriteria;
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
 * Integration tests for the {@link WettsteinResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WettsteinResourceIT {

    private static final String ENTITY_API_URL = "/api/wettsteins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private WettsteinRepository wettsteinRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWettsteinMockMvc;

    private Wettstein wettstein;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wettstein createEntity(EntityManager em) {
        Wettstein wettstein = new Wettstein();
        return wettstein;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wettstein createUpdatedEntity(EntityManager em) {
        Wettstein wettstein = new Wettstein();
        return wettstein;
    }

    @BeforeEach
    public void initTest() {
        wettstein = createEntity(em);
    }

    @Test
    @Transactional
    void createWettstein() throws Exception {
        int databaseSizeBeforeCreate = wettsteinRepository.findAll().size();
        // Create the Wettstein
        restWettsteinMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(wettstein)))
            .andExpect(status().isCreated());

        // Validate the Wettstein in the database
        List<Wettstein> wettsteinList = wettsteinRepository.findAll();
        assertThat(wettsteinList).hasSize(databaseSizeBeforeCreate + 1);
        Wettstein testWettstein = wettsteinList.get(wettsteinList.size() - 1);
    }

    @Test
    @Transactional
    void createWettsteinWithExistingId() throws Exception {
        // Create the Wettstein with an existing ID
        wettstein.setId(1L);

        int databaseSizeBeforeCreate = wettsteinRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWettsteinMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(wettstein)))
            .andExpect(status().isBadRequest());

        // Validate the Wettstein in the database
        List<Wettstein> wettsteinList = wettsteinRepository.findAll();
        assertThat(wettsteinList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllWettsteins() throws Exception {
        // Initialize the database
        wettsteinRepository.saveAndFlush(wettstein);

        // Get all the wettsteinList
        restWettsteinMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wettstein.getId().intValue())));
    }

    @Test
    @Transactional
    void getWettstein() throws Exception {
        // Initialize the database
        wettsteinRepository.saveAndFlush(wettstein);

        // Get the wettstein
        restWettsteinMockMvc
            .perform(get(ENTITY_API_URL_ID, wettstein.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(wettstein.getId().intValue()));
    }

    @Test
    @Transactional
    void getWettsteinsByIdFiltering() throws Exception {
        // Initialize the database
        wettsteinRepository.saveAndFlush(wettstein);

        Long id = wettstein.getId();

        defaultWettsteinShouldBeFound("id.equals=" + id);
        defaultWettsteinShouldNotBeFound("id.notEquals=" + id);

        defaultWettsteinShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultWettsteinShouldNotBeFound("id.greaterThan=" + id);

        defaultWettsteinShouldBeFound("id.lessThanOrEqual=" + id);
        defaultWettsteinShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWettsteinsByClassificationIsEqualToSomething() throws Exception {
        // Initialize the database
        wettsteinRepository.saveAndFlush(wettstein);
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
        wettstein.setClassification(classification);
        wettsteinRepository.saveAndFlush(wettstein);
        Long classificationId = classification.getId();

        // Get all the wettsteinList where classification equals to classificationId
        defaultWettsteinShouldBeFound("classificationId.equals=" + classificationId);

        // Get all the wettsteinList where classification equals to (classificationId + 1)
        defaultWettsteinShouldNotBeFound("classificationId.equals=" + (classificationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWettsteinShouldBeFound(String filter) throws Exception {
        restWettsteinMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wettstein.getId().intValue())));

        // Check, that the count call also returns 1
        restWettsteinMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWettsteinShouldNotBeFound(String filter) throws Exception {
        restWettsteinMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWettsteinMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWettstein() throws Exception {
        // Get the wettstein
        restWettsteinMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewWettstein() throws Exception {
        // Initialize the database
        wettsteinRepository.saveAndFlush(wettstein);

        int databaseSizeBeforeUpdate = wettsteinRepository.findAll().size();

        // Update the wettstein
        Wettstein updatedWettstein = wettsteinRepository.findById(wettstein.getId()).get();
        // Disconnect from session so that the updates on updatedWettstein are not directly saved in db
        em.detach(updatedWettstein);

        restWettsteinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWettstein.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedWettstein))
            )
            .andExpect(status().isOk());

        // Validate the Wettstein in the database
        List<Wettstein> wettsteinList = wettsteinRepository.findAll();
        assertThat(wettsteinList).hasSize(databaseSizeBeforeUpdate);
        Wettstein testWettstein = wettsteinList.get(wettsteinList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingWettstein() throws Exception {
        int databaseSizeBeforeUpdate = wettsteinRepository.findAll().size();
        wettstein.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWettsteinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, wettstein.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(wettstein))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wettstein in the database
        List<Wettstein> wettsteinList = wettsteinRepository.findAll();
        assertThat(wettsteinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWettstein() throws Exception {
        int databaseSizeBeforeUpdate = wettsteinRepository.findAll().size();
        wettstein.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWettsteinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(wettstein))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wettstein in the database
        List<Wettstein> wettsteinList = wettsteinRepository.findAll();
        assertThat(wettsteinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWettstein() throws Exception {
        int databaseSizeBeforeUpdate = wettsteinRepository.findAll().size();
        wettstein.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWettsteinMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(wettstein)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Wettstein in the database
        List<Wettstein> wettsteinList = wettsteinRepository.findAll();
        assertThat(wettsteinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWettsteinWithPatch() throws Exception {
        // Initialize the database
        wettsteinRepository.saveAndFlush(wettstein);

        int databaseSizeBeforeUpdate = wettsteinRepository.findAll().size();

        // Update the wettstein using partial update
        Wettstein partialUpdatedWettstein = new Wettstein();
        partialUpdatedWettstein.setId(wettstein.getId());

        restWettsteinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWettstein.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWettstein))
            )
            .andExpect(status().isOk());

        // Validate the Wettstein in the database
        List<Wettstein> wettsteinList = wettsteinRepository.findAll();
        assertThat(wettsteinList).hasSize(databaseSizeBeforeUpdate);
        Wettstein testWettstein = wettsteinList.get(wettsteinList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateWettsteinWithPatch() throws Exception {
        // Initialize the database
        wettsteinRepository.saveAndFlush(wettstein);

        int databaseSizeBeforeUpdate = wettsteinRepository.findAll().size();

        // Update the wettstein using partial update
        Wettstein partialUpdatedWettstein = new Wettstein();
        partialUpdatedWettstein.setId(wettstein.getId());

        restWettsteinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWettstein.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWettstein))
            )
            .andExpect(status().isOk());

        // Validate the Wettstein in the database
        List<Wettstein> wettsteinList = wettsteinRepository.findAll();
        assertThat(wettsteinList).hasSize(databaseSizeBeforeUpdate);
        Wettstein testWettstein = wettsteinList.get(wettsteinList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingWettstein() throws Exception {
        int databaseSizeBeforeUpdate = wettsteinRepository.findAll().size();
        wettstein.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWettsteinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, wettstein.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(wettstein))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wettstein in the database
        List<Wettstein> wettsteinList = wettsteinRepository.findAll();
        assertThat(wettsteinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWettstein() throws Exception {
        int databaseSizeBeforeUpdate = wettsteinRepository.findAll().size();
        wettstein.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWettsteinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(wettstein))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wettstein in the database
        List<Wettstein> wettsteinList = wettsteinRepository.findAll();
        assertThat(wettsteinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWettstein() throws Exception {
        int databaseSizeBeforeUpdate = wettsteinRepository.findAll().size();
        wettstein.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWettsteinMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(wettstein))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Wettstein in the database
        List<Wettstein> wettsteinList = wettsteinRepository.findAll();
        assertThat(wettsteinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWettstein() throws Exception {
        // Initialize the database
        wettsteinRepository.saveAndFlush(wettstein);

        int databaseSizeBeforeDelete = wettsteinRepository.findAll().size();

        // Delete the wettstein
        restWettsteinMockMvc
            .perform(delete(ENTITY_API_URL_ID, wettstein.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Wettstein> wettsteinList = wettsteinRepository.findAll();
        assertThat(wettsteinList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
