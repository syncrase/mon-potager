package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Classification;
import fr.syncrase.ecosyst.domain.Thorne;
import fr.syncrase.ecosyst.repository.ThorneRepository;
import fr.syncrase.ecosyst.service.criteria.ThorneCriteria;
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
 * Integration tests for the {@link ThorneResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ThorneResourceIT {

    private static final String ENTITY_API_URL = "/api/thornes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ThorneRepository thorneRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restThorneMockMvc;

    private Thorne thorne;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Thorne createEntity(EntityManager em) {
        Thorne thorne = new Thorne();
        return thorne;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Thorne createUpdatedEntity(EntityManager em) {
        Thorne thorne = new Thorne();
        return thorne;
    }

    @BeforeEach
    public void initTest() {
        thorne = createEntity(em);
    }

    @Test
    @Transactional
    void createThorne() throws Exception {
        int databaseSizeBeforeCreate = thorneRepository.findAll().size();
        // Create the Thorne
        restThorneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(thorne)))
            .andExpect(status().isCreated());

        // Validate the Thorne in the database
        List<Thorne> thorneList = thorneRepository.findAll();
        assertThat(thorneList).hasSize(databaseSizeBeforeCreate + 1);
        Thorne testThorne = thorneList.get(thorneList.size() - 1);
    }

    @Test
    @Transactional
    void createThorneWithExistingId() throws Exception {
        // Create the Thorne with an existing ID
        thorne.setId(1L);

        int databaseSizeBeforeCreate = thorneRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restThorneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(thorne)))
            .andExpect(status().isBadRequest());

        // Validate the Thorne in the database
        List<Thorne> thorneList = thorneRepository.findAll();
        assertThat(thorneList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllThornes() throws Exception {
        // Initialize the database
        thorneRepository.saveAndFlush(thorne);

        // Get all the thorneList
        restThorneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(thorne.getId().intValue())));
    }

    @Test
    @Transactional
    void getThorne() throws Exception {
        // Initialize the database
        thorneRepository.saveAndFlush(thorne);

        // Get the thorne
        restThorneMockMvc
            .perform(get(ENTITY_API_URL_ID, thorne.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(thorne.getId().intValue()));
    }

    @Test
    @Transactional
    void getThornesByIdFiltering() throws Exception {
        // Initialize the database
        thorneRepository.saveAndFlush(thorne);

        Long id = thorne.getId();

        defaultThorneShouldBeFound("id.equals=" + id);
        defaultThorneShouldNotBeFound("id.notEquals=" + id);

        defaultThorneShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultThorneShouldNotBeFound("id.greaterThan=" + id);

        defaultThorneShouldBeFound("id.lessThanOrEqual=" + id);
        defaultThorneShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllThornesByClassificationIsEqualToSomething() throws Exception {
        // Initialize the database
        thorneRepository.saveAndFlush(thorne);
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
        thorne.setClassification(classification);
        thorneRepository.saveAndFlush(thorne);
        Long classificationId = classification.getId();

        // Get all the thorneList where classification equals to classificationId
        defaultThorneShouldBeFound("classificationId.equals=" + classificationId);

        // Get all the thorneList where classification equals to (classificationId + 1)
        defaultThorneShouldNotBeFound("classificationId.equals=" + (classificationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultThorneShouldBeFound(String filter) throws Exception {
        restThorneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(thorne.getId().intValue())));

        // Check, that the count call also returns 1
        restThorneMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultThorneShouldNotBeFound(String filter) throws Exception {
        restThorneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restThorneMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingThorne() throws Exception {
        // Get the thorne
        restThorneMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewThorne() throws Exception {
        // Initialize the database
        thorneRepository.saveAndFlush(thorne);

        int databaseSizeBeforeUpdate = thorneRepository.findAll().size();

        // Update the thorne
        Thorne updatedThorne = thorneRepository.findById(thorne.getId()).get();
        // Disconnect from session so that the updates on updatedThorne are not directly saved in db
        em.detach(updatedThorne);

        restThorneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedThorne.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedThorne))
            )
            .andExpect(status().isOk());

        // Validate the Thorne in the database
        List<Thorne> thorneList = thorneRepository.findAll();
        assertThat(thorneList).hasSize(databaseSizeBeforeUpdate);
        Thorne testThorne = thorneList.get(thorneList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingThorne() throws Exception {
        int databaseSizeBeforeUpdate = thorneRepository.findAll().size();
        thorne.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restThorneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, thorne.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(thorne))
            )
            .andExpect(status().isBadRequest());

        // Validate the Thorne in the database
        List<Thorne> thorneList = thorneRepository.findAll();
        assertThat(thorneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchThorne() throws Exception {
        int databaseSizeBeforeUpdate = thorneRepository.findAll().size();
        thorne.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restThorneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(thorne))
            )
            .andExpect(status().isBadRequest());

        // Validate the Thorne in the database
        List<Thorne> thorneList = thorneRepository.findAll();
        assertThat(thorneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamThorne() throws Exception {
        int databaseSizeBeforeUpdate = thorneRepository.findAll().size();
        thorne.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restThorneMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(thorne)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Thorne in the database
        List<Thorne> thorneList = thorneRepository.findAll();
        assertThat(thorneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateThorneWithPatch() throws Exception {
        // Initialize the database
        thorneRepository.saveAndFlush(thorne);

        int databaseSizeBeforeUpdate = thorneRepository.findAll().size();

        // Update the thorne using partial update
        Thorne partialUpdatedThorne = new Thorne();
        partialUpdatedThorne.setId(thorne.getId());

        restThorneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedThorne.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedThorne))
            )
            .andExpect(status().isOk());

        // Validate the Thorne in the database
        List<Thorne> thorneList = thorneRepository.findAll();
        assertThat(thorneList).hasSize(databaseSizeBeforeUpdate);
        Thorne testThorne = thorneList.get(thorneList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateThorneWithPatch() throws Exception {
        // Initialize the database
        thorneRepository.saveAndFlush(thorne);

        int databaseSizeBeforeUpdate = thorneRepository.findAll().size();

        // Update the thorne using partial update
        Thorne partialUpdatedThorne = new Thorne();
        partialUpdatedThorne.setId(thorne.getId());

        restThorneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedThorne.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedThorne))
            )
            .andExpect(status().isOk());

        // Validate the Thorne in the database
        List<Thorne> thorneList = thorneRepository.findAll();
        assertThat(thorneList).hasSize(databaseSizeBeforeUpdate);
        Thorne testThorne = thorneList.get(thorneList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingThorne() throws Exception {
        int databaseSizeBeforeUpdate = thorneRepository.findAll().size();
        thorne.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restThorneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, thorne.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(thorne))
            )
            .andExpect(status().isBadRequest());

        // Validate the Thorne in the database
        List<Thorne> thorneList = thorneRepository.findAll();
        assertThat(thorneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchThorne() throws Exception {
        int databaseSizeBeforeUpdate = thorneRepository.findAll().size();
        thorne.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restThorneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(thorne))
            )
            .andExpect(status().isBadRequest());

        // Validate the Thorne in the database
        List<Thorne> thorneList = thorneRepository.findAll();
        assertThat(thorneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamThorne() throws Exception {
        int databaseSizeBeforeUpdate = thorneRepository.findAll().size();
        thorne.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restThorneMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(thorne)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Thorne in the database
        List<Thorne> thorneList = thorneRepository.findAll();
        assertThat(thorneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteThorne() throws Exception {
        // Initialize the database
        thorneRepository.saveAndFlush(thorne);

        int databaseSizeBeforeDelete = thorneRepository.findAll().size();

        // Delete the thorne
        restThorneMockMvc
            .perform(delete(ENTITY_API_URL_ID, thorne.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Thorne> thorneList = thorneRepository.findAll();
        assertThat(thorneList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
