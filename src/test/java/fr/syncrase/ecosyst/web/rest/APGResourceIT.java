package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.APG;
import fr.syncrase.ecosyst.domain.Classification;
import fr.syncrase.ecosyst.repository.APGRepository;
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
 * Integration tests for the {@link APGResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class APGResourceIT {

    private static final String ENTITY_API_URL = "/api/apgs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private APGRepository aPGRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAPGMockMvc;

    private APG aPG;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static APG createEntity(EntityManager em) {
        APG aPG = new APG();
        return aPG;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static APG createUpdatedEntity(EntityManager em) {
        APG aPG = new APG();
        return aPG;
    }

    @BeforeEach
    public void initTest() {
        aPG = createEntity(em);
    }

    @Test
    @Transactional
    void createAPG() throws Exception {
        int databaseSizeBeforeCreate = aPGRepository.findAll().size();
        // Create the APG
        restAPGMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(aPG)))
            .andExpect(status().isCreated());

        // Validate the APG in the database
        List<APG> aPGList = aPGRepository.findAll();
        assertThat(aPGList).hasSize(databaseSizeBeforeCreate + 1);
        APG testAPG = aPGList.get(aPGList.size() - 1);
    }

    @Test
    @Transactional
    void createAPGWithExistingId() throws Exception {
        // Create the APG with an existing ID
        aPG.setId(1L);

        int databaseSizeBeforeCreate = aPGRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAPGMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(aPG)))
            .andExpect(status().isBadRequest());

        // Validate the APG in the database
        List<APG> aPGList = aPGRepository.findAll();
        assertThat(aPGList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAPGS() throws Exception {
        // Initialize the database
        aPGRepository.saveAndFlush(aPG);

        // Get all the aPGList
        restAPGMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aPG.getId().intValue())));
    }

    @Test
    @Transactional
    void getAPG() throws Exception {
        // Initialize the database
        aPGRepository.saveAndFlush(aPG);

        // Get the aPG
        restAPGMockMvc
            .perform(get(ENTITY_API_URL_ID, aPG.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aPG.getId().intValue()));
    }

    @Test
    @Transactional
    void getAPGSByIdFiltering() throws Exception {
        // Initialize the database
        aPGRepository.saveAndFlush(aPG);

        Long id = aPG.getId();

        defaultAPGShouldBeFound("id.equals=" + id);
        defaultAPGShouldNotBeFound("id.notEquals=" + id);

        defaultAPGShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAPGShouldNotBeFound("id.greaterThan=" + id);

        defaultAPGShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAPGShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAPGSByClassificationIsEqualToSomething() throws Exception {
        // Initialize the database
        aPGRepository.saveAndFlush(aPG);
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
        aPG.setClassification(classification);
        aPGRepository.saveAndFlush(aPG);
        Long classificationId = classification.getId();

        // Get all the aPGList where classification equals to classificationId
        defaultAPGShouldBeFound("classificationId.equals=" + classificationId);

        // Get all the aPGList where classification equals to (classificationId + 1)
        defaultAPGShouldNotBeFound("classificationId.equals=" + (classificationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAPGShouldBeFound(String filter) throws Exception {
        restAPGMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aPG.getId().intValue())));

        // Check, that the count call also returns 1
        restAPGMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAPGShouldNotBeFound(String filter) throws Exception {
        restAPGMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAPGMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAPG() throws Exception {
        // Get the aPG
        restAPGMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAPG() throws Exception {
        // Initialize the database
        aPGRepository.saveAndFlush(aPG);

        int databaseSizeBeforeUpdate = aPGRepository.findAll().size();

        // Update the aPG
        APG updatedAPG = aPGRepository.findById(aPG.getId()).get();
        // Disconnect from session so that the updates on updatedAPG are not directly saved in db
        em.detach(updatedAPG);

        restAPGMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAPG.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAPG))
            )
            .andExpect(status().isOk());

        // Validate the APG in the database
        List<APG> aPGList = aPGRepository.findAll();
        assertThat(aPGList).hasSize(databaseSizeBeforeUpdate);
        APG testAPG = aPGList.get(aPGList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingAPG() throws Exception {
        int databaseSizeBeforeUpdate = aPGRepository.findAll().size();
        aPG.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAPGMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aPG.getId()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(aPG))
            )
            .andExpect(status().isBadRequest());

        // Validate the APG in the database
        List<APG> aPGList = aPGRepository.findAll();
        assertThat(aPGList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAPG() throws Exception {
        int databaseSizeBeforeUpdate = aPGRepository.findAll().size();
        aPG.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAPGMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aPG))
            )
            .andExpect(status().isBadRequest());

        // Validate the APG in the database
        List<APG> aPGList = aPGRepository.findAll();
        assertThat(aPGList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAPG() throws Exception {
        int databaseSizeBeforeUpdate = aPGRepository.findAll().size();
        aPG.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAPGMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(aPG)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the APG in the database
        List<APG> aPGList = aPGRepository.findAll();
        assertThat(aPGList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAPGWithPatch() throws Exception {
        // Initialize the database
        aPGRepository.saveAndFlush(aPG);

        int databaseSizeBeforeUpdate = aPGRepository.findAll().size();

        // Update the aPG using partial update
        APG partialUpdatedAPG = new APG();
        partialUpdatedAPG.setId(aPG.getId());

        restAPGMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAPG.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAPG))
            )
            .andExpect(status().isOk());

        // Validate the APG in the database
        List<APG> aPGList = aPGRepository.findAll();
        assertThat(aPGList).hasSize(databaseSizeBeforeUpdate);
        APG testAPG = aPGList.get(aPGList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateAPGWithPatch() throws Exception {
        // Initialize the database
        aPGRepository.saveAndFlush(aPG);

        int databaseSizeBeforeUpdate = aPGRepository.findAll().size();

        // Update the aPG using partial update
        APG partialUpdatedAPG = new APG();
        partialUpdatedAPG.setId(aPG.getId());

        restAPGMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAPG.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAPG))
            )
            .andExpect(status().isOk());

        // Validate the APG in the database
        List<APG> aPGList = aPGRepository.findAll();
        assertThat(aPGList).hasSize(databaseSizeBeforeUpdate);
        APG testAPG = aPGList.get(aPGList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingAPG() throws Exception {
        int databaseSizeBeforeUpdate = aPGRepository.findAll().size();
        aPG.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAPGMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aPG.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aPG))
            )
            .andExpect(status().isBadRequest());

        // Validate the APG in the database
        List<APG> aPGList = aPGRepository.findAll();
        assertThat(aPGList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAPG() throws Exception {
        int databaseSizeBeforeUpdate = aPGRepository.findAll().size();
        aPG.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAPGMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aPG))
            )
            .andExpect(status().isBadRequest());

        // Validate the APG in the database
        List<APG> aPGList = aPGRepository.findAll();
        assertThat(aPGList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAPG() throws Exception {
        int databaseSizeBeforeUpdate = aPGRepository.findAll().size();
        aPG.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAPGMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(aPG)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the APG in the database
        List<APG> aPGList = aPGRepository.findAll();
        assertThat(aPGList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAPG() throws Exception {
        // Initialize the database
        aPGRepository.saveAndFlush(aPG);

        int databaseSizeBeforeDelete = aPGRepository.findAll().size();

        // Delete the aPG
        restAPGMockMvc.perform(delete(ENTITY_API_URL_ID, aPG.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<APG> aPGList = aPGRepository.findAll();
        assertThat(aPGList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
