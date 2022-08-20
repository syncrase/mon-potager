package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Classification;
import fr.syncrase.ecosyst.domain.Dahlgren;
import fr.syncrase.ecosyst.repository.DahlgrenRepository;
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
 * Integration tests for the {@link DahlgrenResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DahlgrenResourceIT {

    private static final String ENTITY_API_URL = "/api/dahlgrens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private DahlgrenRepository dahlgrenRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDahlgrenMockMvc;

    private Dahlgren dahlgren;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dahlgren createEntity(EntityManager em) {
        Dahlgren dahlgren = new Dahlgren();
        return dahlgren;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dahlgren createUpdatedEntity(EntityManager em) {
        Dahlgren dahlgren = new Dahlgren();
        return dahlgren;
    }

    @BeforeEach
    public void initTest() {
        dahlgren = createEntity(em);
    }

    @Test
    @Transactional
    void createDahlgren() throws Exception {
        int databaseSizeBeforeCreate = dahlgrenRepository.findAll().size();
        // Create the Dahlgren
        restDahlgrenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dahlgren)))
            .andExpect(status().isCreated());

        // Validate the Dahlgren in the database
        List<Dahlgren> dahlgrenList = dahlgrenRepository.findAll();
        assertThat(dahlgrenList).hasSize(databaseSizeBeforeCreate + 1);
        Dahlgren testDahlgren = dahlgrenList.get(dahlgrenList.size() - 1);
    }

    @Test
    @Transactional
    void createDahlgrenWithExistingId() throws Exception {
        // Create the Dahlgren with an existing ID
        dahlgren.setId(1L);

        int databaseSizeBeforeCreate = dahlgrenRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDahlgrenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dahlgren)))
            .andExpect(status().isBadRequest());

        // Validate the Dahlgren in the database
        List<Dahlgren> dahlgrenList = dahlgrenRepository.findAll();
        assertThat(dahlgrenList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDahlgrens() throws Exception {
        // Initialize the database
        dahlgrenRepository.saveAndFlush(dahlgren);

        // Get all the dahlgrenList
        restDahlgrenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dahlgren.getId().intValue())));
    }

    @Test
    @Transactional
    void getDahlgren() throws Exception {
        // Initialize the database
        dahlgrenRepository.saveAndFlush(dahlgren);

        // Get the dahlgren
        restDahlgrenMockMvc
            .perform(get(ENTITY_API_URL_ID, dahlgren.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dahlgren.getId().intValue()));
    }

    @Test
    @Transactional
    void getDahlgrensByIdFiltering() throws Exception {
        // Initialize the database
        dahlgrenRepository.saveAndFlush(dahlgren);

        Long id = dahlgren.getId();

        defaultDahlgrenShouldBeFound("id.equals=" + id);
        defaultDahlgrenShouldNotBeFound("id.notEquals=" + id);

        defaultDahlgrenShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDahlgrenShouldNotBeFound("id.greaterThan=" + id);

        defaultDahlgrenShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDahlgrenShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDahlgrensByClassificationIsEqualToSomething() throws Exception {
        // Initialize the database
        dahlgrenRepository.saveAndFlush(dahlgren);
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
        dahlgren.setClassification(classification);
        dahlgrenRepository.saveAndFlush(dahlgren);
        Long classificationId = classification.getId();

        // Get all the dahlgrenList where classification equals to classificationId
        defaultDahlgrenShouldBeFound("classificationId.equals=" + classificationId);

        // Get all the dahlgrenList where classification equals to (classificationId + 1)
        defaultDahlgrenShouldNotBeFound("classificationId.equals=" + (classificationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDahlgrenShouldBeFound(String filter) throws Exception {
        restDahlgrenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dahlgren.getId().intValue())));

        // Check, that the count call also returns 1
        restDahlgrenMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDahlgrenShouldNotBeFound(String filter) throws Exception {
        restDahlgrenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDahlgrenMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDahlgren() throws Exception {
        // Get the dahlgren
        restDahlgrenMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDahlgren() throws Exception {
        // Initialize the database
        dahlgrenRepository.saveAndFlush(dahlgren);

        int databaseSizeBeforeUpdate = dahlgrenRepository.findAll().size();

        // Update the dahlgren
        Dahlgren updatedDahlgren = dahlgrenRepository.findById(dahlgren.getId()).get();
        // Disconnect from session so that the updates on updatedDahlgren are not directly saved in db
        em.detach(updatedDahlgren);

        restDahlgrenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDahlgren.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDahlgren))
            )
            .andExpect(status().isOk());

        // Validate the Dahlgren in the database
        List<Dahlgren> dahlgrenList = dahlgrenRepository.findAll();
        assertThat(dahlgrenList).hasSize(databaseSizeBeforeUpdate);
        Dahlgren testDahlgren = dahlgrenList.get(dahlgrenList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingDahlgren() throws Exception {
        int databaseSizeBeforeUpdate = dahlgrenRepository.findAll().size();
        dahlgren.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDahlgrenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dahlgren.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dahlgren))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dahlgren in the database
        List<Dahlgren> dahlgrenList = dahlgrenRepository.findAll();
        assertThat(dahlgrenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDahlgren() throws Exception {
        int databaseSizeBeforeUpdate = dahlgrenRepository.findAll().size();
        dahlgren.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDahlgrenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dahlgren))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dahlgren in the database
        List<Dahlgren> dahlgrenList = dahlgrenRepository.findAll();
        assertThat(dahlgrenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDahlgren() throws Exception {
        int databaseSizeBeforeUpdate = dahlgrenRepository.findAll().size();
        dahlgren.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDahlgrenMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dahlgren)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dahlgren in the database
        List<Dahlgren> dahlgrenList = dahlgrenRepository.findAll();
        assertThat(dahlgrenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDahlgrenWithPatch() throws Exception {
        // Initialize the database
        dahlgrenRepository.saveAndFlush(dahlgren);

        int databaseSizeBeforeUpdate = dahlgrenRepository.findAll().size();

        // Update the dahlgren using partial update
        Dahlgren partialUpdatedDahlgren = new Dahlgren();
        partialUpdatedDahlgren.setId(dahlgren.getId());

        restDahlgrenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDahlgren.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDahlgren))
            )
            .andExpect(status().isOk());

        // Validate the Dahlgren in the database
        List<Dahlgren> dahlgrenList = dahlgrenRepository.findAll();
        assertThat(dahlgrenList).hasSize(databaseSizeBeforeUpdate);
        Dahlgren testDahlgren = dahlgrenList.get(dahlgrenList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateDahlgrenWithPatch() throws Exception {
        // Initialize the database
        dahlgrenRepository.saveAndFlush(dahlgren);

        int databaseSizeBeforeUpdate = dahlgrenRepository.findAll().size();

        // Update the dahlgren using partial update
        Dahlgren partialUpdatedDahlgren = new Dahlgren();
        partialUpdatedDahlgren.setId(dahlgren.getId());

        restDahlgrenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDahlgren.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDahlgren))
            )
            .andExpect(status().isOk());

        // Validate the Dahlgren in the database
        List<Dahlgren> dahlgrenList = dahlgrenRepository.findAll();
        assertThat(dahlgrenList).hasSize(databaseSizeBeforeUpdate);
        Dahlgren testDahlgren = dahlgrenList.get(dahlgrenList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingDahlgren() throws Exception {
        int databaseSizeBeforeUpdate = dahlgrenRepository.findAll().size();
        dahlgren.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDahlgrenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dahlgren.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dahlgren))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dahlgren in the database
        List<Dahlgren> dahlgrenList = dahlgrenRepository.findAll();
        assertThat(dahlgrenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDahlgren() throws Exception {
        int databaseSizeBeforeUpdate = dahlgrenRepository.findAll().size();
        dahlgren.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDahlgrenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dahlgren))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dahlgren in the database
        List<Dahlgren> dahlgrenList = dahlgrenRepository.findAll();
        assertThat(dahlgrenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDahlgren() throws Exception {
        int databaseSizeBeforeUpdate = dahlgrenRepository.findAll().size();
        dahlgren.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDahlgrenMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(dahlgren)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dahlgren in the database
        List<Dahlgren> dahlgrenList = dahlgrenRepository.findAll();
        assertThat(dahlgrenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDahlgren() throws Exception {
        // Initialize the database
        dahlgrenRepository.saveAndFlush(dahlgren);

        int databaseSizeBeforeDelete = dahlgrenRepository.findAll().size();

        // Delete the dahlgren
        restDahlgrenMockMvc
            .perform(delete(ENTITY_API_URL_ID, dahlgren.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Dahlgren> dahlgrenList = dahlgrenRepository.findAll();
        assertThat(dahlgrenList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
