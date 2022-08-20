package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.BenthamHooker;
import fr.syncrase.ecosyst.domain.Classification;
import fr.syncrase.ecosyst.repository.BenthamHookerRepository;
import fr.syncrase.ecosyst.service.criteria.BenthamHookerCriteria;
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
 * Integration tests for the {@link BenthamHookerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BenthamHookerResourceIT {

    private static final String ENTITY_API_URL = "/api/bentham-hookers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private BenthamHookerRepository benthamHookerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBenthamHookerMockMvc;

    private BenthamHooker benthamHooker;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BenthamHooker createEntity(EntityManager em) {
        BenthamHooker benthamHooker = new BenthamHooker();
        return benthamHooker;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BenthamHooker createUpdatedEntity(EntityManager em) {
        BenthamHooker benthamHooker = new BenthamHooker();
        return benthamHooker;
    }

    @BeforeEach
    public void initTest() {
        benthamHooker = createEntity(em);
    }

    @Test
    @Transactional
    void createBenthamHooker() throws Exception {
        int databaseSizeBeforeCreate = benthamHookerRepository.findAll().size();
        // Create the BenthamHooker
        restBenthamHookerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(benthamHooker)))
            .andExpect(status().isCreated());

        // Validate the BenthamHooker in the database
        List<BenthamHooker> benthamHookerList = benthamHookerRepository.findAll();
        assertThat(benthamHookerList).hasSize(databaseSizeBeforeCreate + 1);
        BenthamHooker testBenthamHooker = benthamHookerList.get(benthamHookerList.size() - 1);
    }

    @Test
    @Transactional
    void createBenthamHookerWithExistingId() throws Exception {
        // Create the BenthamHooker with an existing ID
        benthamHooker.setId(1L);

        int databaseSizeBeforeCreate = benthamHookerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBenthamHookerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(benthamHooker)))
            .andExpect(status().isBadRequest());

        // Validate the BenthamHooker in the database
        List<BenthamHooker> benthamHookerList = benthamHookerRepository.findAll();
        assertThat(benthamHookerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBenthamHookers() throws Exception {
        // Initialize the database
        benthamHookerRepository.saveAndFlush(benthamHooker);

        // Get all the benthamHookerList
        restBenthamHookerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(benthamHooker.getId().intValue())));
    }

    @Test
    @Transactional
    void getBenthamHooker() throws Exception {
        // Initialize the database
        benthamHookerRepository.saveAndFlush(benthamHooker);

        // Get the benthamHooker
        restBenthamHookerMockMvc
            .perform(get(ENTITY_API_URL_ID, benthamHooker.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(benthamHooker.getId().intValue()));
    }

    @Test
    @Transactional
    void getBenthamHookersByIdFiltering() throws Exception {
        // Initialize the database
        benthamHookerRepository.saveAndFlush(benthamHooker);

        Long id = benthamHooker.getId();

        defaultBenthamHookerShouldBeFound("id.equals=" + id);
        defaultBenthamHookerShouldNotBeFound("id.notEquals=" + id);

        defaultBenthamHookerShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBenthamHookerShouldNotBeFound("id.greaterThan=" + id);

        defaultBenthamHookerShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBenthamHookerShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBenthamHookersByClassificationIsEqualToSomething() throws Exception {
        // Initialize the database
        benthamHookerRepository.saveAndFlush(benthamHooker);
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
        benthamHooker.setClassification(classification);
        benthamHookerRepository.saveAndFlush(benthamHooker);
        Long classificationId = classification.getId();

        // Get all the benthamHookerList where classification equals to classificationId
        defaultBenthamHookerShouldBeFound("classificationId.equals=" + classificationId);

        // Get all the benthamHookerList where classification equals to (classificationId + 1)
        defaultBenthamHookerShouldNotBeFound("classificationId.equals=" + (classificationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBenthamHookerShouldBeFound(String filter) throws Exception {
        restBenthamHookerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(benthamHooker.getId().intValue())));

        // Check, that the count call also returns 1
        restBenthamHookerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBenthamHookerShouldNotBeFound(String filter) throws Exception {
        restBenthamHookerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBenthamHookerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBenthamHooker() throws Exception {
        // Get the benthamHooker
        restBenthamHookerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBenthamHooker() throws Exception {
        // Initialize the database
        benthamHookerRepository.saveAndFlush(benthamHooker);

        int databaseSizeBeforeUpdate = benthamHookerRepository.findAll().size();

        // Update the benthamHooker
        BenthamHooker updatedBenthamHooker = benthamHookerRepository.findById(benthamHooker.getId()).get();
        // Disconnect from session so that the updates on updatedBenthamHooker are not directly saved in db
        em.detach(updatedBenthamHooker);

        restBenthamHookerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBenthamHooker.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBenthamHooker))
            )
            .andExpect(status().isOk());

        // Validate the BenthamHooker in the database
        List<BenthamHooker> benthamHookerList = benthamHookerRepository.findAll();
        assertThat(benthamHookerList).hasSize(databaseSizeBeforeUpdate);
        BenthamHooker testBenthamHooker = benthamHookerList.get(benthamHookerList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingBenthamHooker() throws Exception {
        int databaseSizeBeforeUpdate = benthamHookerRepository.findAll().size();
        benthamHooker.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBenthamHookerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, benthamHooker.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(benthamHooker))
            )
            .andExpect(status().isBadRequest());

        // Validate the BenthamHooker in the database
        List<BenthamHooker> benthamHookerList = benthamHookerRepository.findAll();
        assertThat(benthamHookerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBenthamHooker() throws Exception {
        int databaseSizeBeforeUpdate = benthamHookerRepository.findAll().size();
        benthamHooker.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBenthamHookerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(benthamHooker))
            )
            .andExpect(status().isBadRequest());

        // Validate the BenthamHooker in the database
        List<BenthamHooker> benthamHookerList = benthamHookerRepository.findAll();
        assertThat(benthamHookerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBenthamHooker() throws Exception {
        int databaseSizeBeforeUpdate = benthamHookerRepository.findAll().size();
        benthamHooker.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBenthamHookerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(benthamHooker)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BenthamHooker in the database
        List<BenthamHooker> benthamHookerList = benthamHookerRepository.findAll();
        assertThat(benthamHookerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBenthamHookerWithPatch() throws Exception {
        // Initialize the database
        benthamHookerRepository.saveAndFlush(benthamHooker);

        int databaseSizeBeforeUpdate = benthamHookerRepository.findAll().size();

        // Update the benthamHooker using partial update
        BenthamHooker partialUpdatedBenthamHooker = new BenthamHooker();
        partialUpdatedBenthamHooker.setId(benthamHooker.getId());

        restBenthamHookerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBenthamHooker.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBenthamHooker))
            )
            .andExpect(status().isOk());

        // Validate the BenthamHooker in the database
        List<BenthamHooker> benthamHookerList = benthamHookerRepository.findAll();
        assertThat(benthamHookerList).hasSize(databaseSizeBeforeUpdate);
        BenthamHooker testBenthamHooker = benthamHookerList.get(benthamHookerList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateBenthamHookerWithPatch() throws Exception {
        // Initialize the database
        benthamHookerRepository.saveAndFlush(benthamHooker);

        int databaseSizeBeforeUpdate = benthamHookerRepository.findAll().size();

        // Update the benthamHooker using partial update
        BenthamHooker partialUpdatedBenthamHooker = new BenthamHooker();
        partialUpdatedBenthamHooker.setId(benthamHooker.getId());

        restBenthamHookerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBenthamHooker.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBenthamHooker))
            )
            .andExpect(status().isOk());

        // Validate the BenthamHooker in the database
        List<BenthamHooker> benthamHookerList = benthamHookerRepository.findAll();
        assertThat(benthamHookerList).hasSize(databaseSizeBeforeUpdate);
        BenthamHooker testBenthamHooker = benthamHookerList.get(benthamHookerList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingBenthamHooker() throws Exception {
        int databaseSizeBeforeUpdate = benthamHookerRepository.findAll().size();
        benthamHooker.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBenthamHookerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, benthamHooker.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(benthamHooker))
            )
            .andExpect(status().isBadRequest());

        // Validate the BenthamHooker in the database
        List<BenthamHooker> benthamHookerList = benthamHookerRepository.findAll();
        assertThat(benthamHookerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBenthamHooker() throws Exception {
        int databaseSizeBeforeUpdate = benthamHookerRepository.findAll().size();
        benthamHooker.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBenthamHookerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(benthamHooker))
            )
            .andExpect(status().isBadRequest());

        // Validate the BenthamHooker in the database
        List<BenthamHooker> benthamHookerList = benthamHookerRepository.findAll();
        assertThat(benthamHookerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBenthamHooker() throws Exception {
        int databaseSizeBeforeUpdate = benthamHookerRepository.findAll().size();
        benthamHooker.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBenthamHookerMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(benthamHooker))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BenthamHooker in the database
        List<BenthamHooker> benthamHookerList = benthamHookerRepository.findAll();
        assertThat(benthamHookerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBenthamHooker() throws Exception {
        // Initialize the database
        benthamHookerRepository.saveAndFlush(benthamHooker);

        int databaseSizeBeforeDelete = benthamHookerRepository.findAll().size();

        // Delete the benthamHooker
        restBenthamHookerMockMvc
            .perform(delete(ENTITY_API_URL_ID, benthamHooker.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BenthamHooker> benthamHookerList = benthamHookerRepository.findAll();
        assertThat(benthamHookerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
