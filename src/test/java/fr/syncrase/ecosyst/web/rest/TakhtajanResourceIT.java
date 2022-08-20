package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Classification;
import fr.syncrase.ecosyst.domain.Takhtajan;
import fr.syncrase.ecosyst.repository.TakhtajanRepository;
import fr.syncrase.ecosyst.service.criteria.TakhtajanCriteria;
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
 * Integration tests for the {@link TakhtajanResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TakhtajanResourceIT {

    private static final String ENTITY_API_URL = "/api/takhtajans";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private TakhtajanRepository takhtajanRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTakhtajanMockMvc;

    private Takhtajan takhtajan;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Takhtajan createEntity(EntityManager em) {
        Takhtajan takhtajan = new Takhtajan();
        return takhtajan;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Takhtajan createUpdatedEntity(EntityManager em) {
        Takhtajan takhtajan = new Takhtajan();
        return takhtajan;
    }

    @BeforeEach
    public void initTest() {
        takhtajan = createEntity(em);
    }

    @Test
    @Transactional
    void createTakhtajan() throws Exception {
        int databaseSizeBeforeCreate = takhtajanRepository.findAll().size();
        // Create the Takhtajan
        restTakhtajanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(takhtajan)))
            .andExpect(status().isCreated());

        // Validate the Takhtajan in the database
        List<Takhtajan> takhtajanList = takhtajanRepository.findAll();
        assertThat(takhtajanList).hasSize(databaseSizeBeforeCreate + 1);
        Takhtajan testTakhtajan = takhtajanList.get(takhtajanList.size() - 1);
    }

    @Test
    @Transactional
    void createTakhtajanWithExistingId() throws Exception {
        // Create the Takhtajan with an existing ID
        takhtajan.setId(1L);

        int databaseSizeBeforeCreate = takhtajanRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTakhtajanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(takhtajan)))
            .andExpect(status().isBadRequest());

        // Validate the Takhtajan in the database
        List<Takhtajan> takhtajanList = takhtajanRepository.findAll();
        assertThat(takhtajanList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTakhtajans() throws Exception {
        // Initialize the database
        takhtajanRepository.saveAndFlush(takhtajan);

        // Get all the takhtajanList
        restTakhtajanMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(takhtajan.getId().intValue())));
    }

    @Test
    @Transactional
    void getTakhtajan() throws Exception {
        // Initialize the database
        takhtajanRepository.saveAndFlush(takhtajan);

        // Get the takhtajan
        restTakhtajanMockMvc
            .perform(get(ENTITY_API_URL_ID, takhtajan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(takhtajan.getId().intValue()));
    }

    @Test
    @Transactional
    void getTakhtajansByIdFiltering() throws Exception {
        // Initialize the database
        takhtajanRepository.saveAndFlush(takhtajan);

        Long id = takhtajan.getId();

        defaultTakhtajanShouldBeFound("id.equals=" + id);
        defaultTakhtajanShouldNotBeFound("id.notEquals=" + id);

        defaultTakhtajanShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTakhtajanShouldNotBeFound("id.greaterThan=" + id);

        defaultTakhtajanShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTakhtajanShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTakhtajansByClassificationIsEqualToSomething() throws Exception {
        // Initialize the database
        takhtajanRepository.saveAndFlush(takhtajan);
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
        takhtajan.setClassification(classification);
        takhtajanRepository.saveAndFlush(takhtajan);
        Long classificationId = classification.getId();

        // Get all the takhtajanList where classification equals to classificationId
        defaultTakhtajanShouldBeFound("classificationId.equals=" + classificationId);

        // Get all the takhtajanList where classification equals to (classificationId + 1)
        defaultTakhtajanShouldNotBeFound("classificationId.equals=" + (classificationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTakhtajanShouldBeFound(String filter) throws Exception {
        restTakhtajanMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(takhtajan.getId().intValue())));

        // Check, that the count call also returns 1
        restTakhtajanMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTakhtajanShouldNotBeFound(String filter) throws Exception {
        restTakhtajanMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTakhtajanMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTakhtajan() throws Exception {
        // Get the takhtajan
        restTakhtajanMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTakhtajan() throws Exception {
        // Initialize the database
        takhtajanRepository.saveAndFlush(takhtajan);

        int databaseSizeBeforeUpdate = takhtajanRepository.findAll().size();

        // Update the takhtajan
        Takhtajan updatedTakhtajan = takhtajanRepository.findById(takhtajan.getId()).get();
        // Disconnect from session so that the updates on updatedTakhtajan are not directly saved in db
        em.detach(updatedTakhtajan);

        restTakhtajanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTakhtajan.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTakhtajan))
            )
            .andExpect(status().isOk());

        // Validate the Takhtajan in the database
        List<Takhtajan> takhtajanList = takhtajanRepository.findAll();
        assertThat(takhtajanList).hasSize(databaseSizeBeforeUpdate);
        Takhtajan testTakhtajan = takhtajanList.get(takhtajanList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingTakhtajan() throws Exception {
        int databaseSizeBeforeUpdate = takhtajanRepository.findAll().size();
        takhtajan.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTakhtajanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, takhtajan.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(takhtajan))
            )
            .andExpect(status().isBadRequest());

        // Validate the Takhtajan in the database
        List<Takhtajan> takhtajanList = takhtajanRepository.findAll();
        assertThat(takhtajanList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTakhtajan() throws Exception {
        int databaseSizeBeforeUpdate = takhtajanRepository.findAll().size();
        takhtajan.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTakhtajanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(takhtajan))
            )
            .andExpect(status().isBadRequest());

        // Validate the Takhtajan in the database
        List<Takhtajan> takhtajanList = takhtajanRepository.findAll();
        assertThat(takhtajanList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTakhtajan() throws Exception {
        int databaseSizeBeforeUpdate = takhtajanRepository.findAll().size();
        takhtajan.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTakhtajanMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(takhtajan)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Takhtajan in the database
        List<Takhtajan> takhtajanList = takhtajanRepository.findAll();
        assertThat(takhtajanList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTakhtajanWithPatch() throws Exception {
        // Initialize the database
        takhtajanRepository.saveAndFlush(takhtajan);

        int databaseSizeBeforeUpdate = takhtajanRepository.findAll().size();

        // Update the takhtajan using partial update
        Takhtajan partialUpdatedTakhtajan = new Takhtajan();
        partialUpdatedTakhtajan.setId(takhtajan.getId());

        restTakhtajanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTakhtajan.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTakhtajan))
            )
            .andExpect(status().isOk());

        // Validate the Takhtajan in the database
        List<Takhtajan> takhtajanList = takhtajanRepository.findAll();
        assertThat(takhtajanList).hasSize(databaseSizeBeforeUpdate);
        Takhtajan testTakhtajan = takhtajanList.get(takhtajanList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateTakhtajanWithPatch() throws Exception {
        // Initialize the database
        takhtajanRepository.saveAndFlush(takhtajan);

        int databaseSizeBeforeUpdate = takhtajanRepository.findAll().size();

        // Update the takhtajan using partial update
        Takhtajan partialUpdatedTakhtajan = new Takhtajan();
        partialUpdatedTakhtajan.setId(takhtajan.getId());

        restTakhtajanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTakhtajan.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTakhtajan))
            )
            .andExpect(status().isOk());

        // Validate the Takhtajan in the database
        List<Takhtajan> takhtajanList = takhtajanRepository.findAll();
        assertThat(takhtajanList).hasSize(databaseSizeBeforeUpdate);
        Takhtajan testTakhtajan = takhtajanList.get(takhtajanList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingTakhtajan() throws Exception {
        int databaseSizeBeforeUpdate = takhtajanRepository.findAll().size();
        takhtajan.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTakhtajanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, takhtajan.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(takhtajan))
            )
            .andExpect(status().isBadRequest());

        // Validate the Takhtajan in the database
        List<Takhtajan> takhtajanList = takhtajanRepository.findAll();
        assertThat(takhtajanList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTakhtajan() throws Exception {
        int databaseSizeBeforeUpdate = takhtajanRepository.findAll().size();
        takhtajan.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTakhtajanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(takhtajan))
            )
            .andExpect(status().isBadRequest());

        // Validate the Takhtajan in the database
        List<Takhtajan> takhtajanList = takhtajanRepository.findAll();
        assertThat(takhtajanList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTakhtajan() throws Exception {
        int databaseSizeBeforeUpdate = takhtajanRepository.findAll().size();
        takhtajan.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTakhtajanMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(takhtajan))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Takhtajan in the database
        List<Takhtajan> takhtajanList = takhtajanRepository.findAll();
        assertThat(takhtajanList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTakhtajan() throws Exception {
        // Initialize the database
        takhtajanRepository.saveAndFlush(takhtajan);

        int databaseSizeBeforeDelete = takhtajanRepository.findAll().size();

        // Delete the takhtajan
        restTakhtajanMockMvc
            .perform(delete(ENTITY_API_URL_ID, takhtajan.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Takhtajan> takhtajanList = takhtajanRepository.findAll();
        assertThat(takhtajanList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
