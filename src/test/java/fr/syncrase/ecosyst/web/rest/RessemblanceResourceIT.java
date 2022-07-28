package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.domain.Ressemblance;
import fr.syncrase.ecosyst.repository.RessemblanceRepository;
import fr.syncrase.ecosyst.service.criteria.RessemblanceCriteria;
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
 * Integration tests for the {@link RessemblanceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RessemblanceResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ressemblances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RessemblanceRepository ressemblanceRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRessemblanceMockMvc;

    private Ressemblance ressemblance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ressemblance createEntity(EntityManager em) {
        Ressemblance ressemblance = new Ressemblance().description(DEFAULT_DESCRIPTION);
        return ressemblance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ressemblance createUpdatedEntity(EntityManager em) {
        Ressemblance ressemblance = new Ressemblance().description(UPDATED_DESCRIPTION);
        return ressemblance;
    }

    @BeforeEach
    public void initTest() {
        ressemblance = createEntity(em);
    }

    @Test
    @Transactional
    void createRessemblance() throws Exception {
        int databaseSizeBeforeCreate = ressemblanceRepository.findAll().size();
        // Create the Ressemblance
        restRessemblanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ressemblance)))
            .andExpect(status().isCreated());

        // Validate the Ressemblance in the database
        List<Ressemblance> ressemblanceList = ressemblanceRepository.findAll();
        assertThat(ressemblanceList).hasSize(databaseSizeBeforeCreate + 1);
        Ressemblance testRessemblance = ressemblanceList.get(ressemblanceList.size() - 1);
        assertThat(testRessemblance.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createRessemblanceWithExistingId() throws Exception {
        // Create the Ressemblance with an existing ID
        ressemblance.setId(1L);

        int databaseSizeBeforeCreate = ressemblanceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRessemblanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ressemblance)))
            .andExpect(status().isBadRequest());

        // Validate the Ressemblance in the database
        List<Ressemblance> ressemblanceList = ressemblanceRepository.findAll();
        assertThat(ressemblanceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRessemblances() throws Exception {
        // Initialize the database
        ressemblanceRepository.saveAndFlush(ressemblance);

        // Get all the ressemblanceList
        restRessemblanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ressemblance.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getRessemblance() throws Exception {
        // Initialize the database
        ressemblanceRepository.saveAndFlush(ressemblance);

        // Get the ressemblance
        restRessemblanceMockMvc
            .perform(get(ENTITY_API_URL_ID, ressemblance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ressemblance.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getRessemblancesByIdFiltering() throws Exception {
        // Initialize the database
        ressemblanceRepository.saveAndFlush(ressemblance);

        Long id = ressemblance.getId();

        defaultRessemblanceShouldBeFound("id.equals=" + id);
        defaultRessemblanceShouldNotBeFound("id.notEquals=" + id);

        defaultRessemblanceShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRessemblanceShouldNotBeFound("id.greaterThan=" + id);

        defaultRessemblanceShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRessemblanceShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRessemblancesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        ressemblanceRepository.saveAndFlush(ressemblance);

        // Get all the ressemblanceList where description equals to DEFAULT_DESCRIPTION
        defaultRessemblanceShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the ressemblanceList where description equals to UPDATED_DESCRIPTION
        defaultRessemblanceShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRessemblancesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ressemblanceRepository.saveAndFlush(ressemblance);

        // Get all the ressemblanceList where description not equals to DEFAULT_DESCRIPTION
        defaultRessemblanceShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the ressemblanceList where description not equals to UPDATED_DESCRIPTION
        defaultRessemblanceShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRessemblancesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        ressemblanceRepository.saveAndFlush(ressemblance);

        // Get all the ressemblanceList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultRessemblanceShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the ressemblanceList where description equals to UPDATED_DESCRIPTION
        defaultRessemblanceShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRessemblancesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        ressemblanceRepository.saveAndFlush(ressemblance);

        // Get all the ressemblanceList where description is not null
        defaultRessemblanceShouldBeFound("description.specified=true");

        // Get all the ressemblanceList where description is null
        defaultRessemblanceShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllRessemblancesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        ressemblanceRepository.saveAndFlush(ressemblance);

        // Get all the ressemblanceList where description contains DEFAULT_DESCRIPTION
        defaultRessemblanceShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the ressemblanceList where description contains UPDATED_DESCRIPTION
        defaultRessemblanceShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRessemblancesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        ressemblanceRepository.saveAndFlush(ressemblance);

        // Get all the ressemblanceList where description does not contain DEFAULT_DESCRIPTION
        defaultRessemblanceShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the ressemblanceList where description does not contain UPDATED_DESCRIPTION
        defaultRessemblanceShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRessemblancesByPlanteRessemblantIsEqualToSomething() throws Exception {
        // Initialize the database
        ressemblanceRepository.saveAndFlush(ressemblance);
        Plante planteRessemblant;
        if (TestUtil.findAll(em, Plante.class).isEmpty()) {
            planteRessemblant = PlanteResourceIT.createEntity(em);
            em.persist(planteRessemblant);
            em.flush();
        } else {
            planteRessemblant = TestUtil.findAll(em, Plante.class).get(0);
        }
        em.persist(planteRessemblant);
        em.flush();
        ressemblance.setPlanteRessemblant(planteRessemblant);
        ressemblanceRepository.saveAndFlush(ressemblance);
        Long planteRessemblantId = planteRessemblant.getId();

        // Get all the ressemblanceList where planteRessemblant equals to planteRessemblantId
        defaultRessemblanceShouldBeFound("planteRessemblantId.equals=" + planteRessemblantId);

        // Get all the ressemblanceList where planteRessemblant equals to (planteRessemblantId + 1)
        defaultRessemblanceShouldNotBeFound("planteRessemblantId.equals=" + (planteRessemblantId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRessemblanceShouldBeFound(String filter) throws Exception {
        restRessemblanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ressemblance.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restRessemblanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRessemblanceShouldNotBeFound(String filter) throws Exception {
        restRessemblanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRessemblanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRessemblance() throws Exception {
        // Get the ressemblance
        restRessemblanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRessemblance() throws Exception {
        // Initialize the database
        ressemblanceRepository.saveAndFlush(ressemblance);

        int databaseSizeBeforeUpdate = ressemblanceRepository.findAll().size();

        // Update the ressemblance
        Ressemblance updatedRessemblance = ressemblanceRepository.findById(ressemblance.getId()).get();
        // Disconnect from session so that the updates on updatedRessemblance are not directly saved in db
        em.detach(updatedRessemblance);
        updatedRessemblance.description(UPDATED_DESCRIPTION);

        restRessemblanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRessemblance.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRessemblance))
            )
            .andExpect(status().isOk());

        // Validate the Ressemblance in the database
        List<Ressemblance> ressemblanceList = ressemblanceRepository.findAll();
        assertThat(ressemblanceList).hasSize(databaseSizeBeforeUpdate);
        Ressemblance testRessemblance = ressemblanceList.get(ressemblanceList.size() - 1);
        assertThat(testRessemblance.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingRessemblance() throws Exception {
        int databaseSizeBeforeUpdate = ressemblanceRepository.findAll().size();
        ressemblance.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRessemblanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ressemblance.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ressemblance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ressemblance in the database
        List<Ressemblance> ressemblanceList = ressemblanceRepository.findAll();
        assertThat(ressemblanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRessemblance() throws Exception {
        int databaseSizeBeforeUpdate = ressemblanceRepository.findAll().size();
        ressemblance.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRessemblanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ressemblance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ressemblance in the database
        List<Ressemblance> ressemblanceList = ressemblanceRepository.findAll();
        assertThat(ressemblanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRessemblance() throws Exception {
        int databaseSizeBeforeUpdate = ressemblanceRepository.findAll().size();
        ressemblance.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRessemblanceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ressemblance)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ressemblance in the database
        List<Ressemblance> ressemblanceList = ressemblanceRepository.findAll();
        assertThat(ressemblanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRessemblanceWithPatch() throws Exception {
        // Initialize the database
        ressemblanceRepository.saveAndFlush(ressemblance);

        int databaseSizeBeforeUpdate = ressemblanceRepository.findAll().size();

        // Update the ressemblance using partial update
        Ressemblance partialUpdatedRessemblance = new Ressemblance();
        partialUpdatedRessemblance.setId(ressemblance.getId());

        partialUpdatedRessemblance.description(UPDATED_DESCRIPTION);

        restRessemblanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRessemblance.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRessemblance))
            )
            .andExpect(status().isOk());

        // Validate the Ressemblance in the database
        List<Ressemblance> ressemblanceList = ressemblanceRepository.findAll();
        assertThat(ressemblanceList).hasSize(databaseSizeBeforeUpdate);
        Ressemblance testRessemblance = ressemblanceList.get(ressemblanceList.size() - 1);
        assertThat(testRessemblance.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateRessemblanceWithPatch() throws Exception {
        // Initialize the database
        ressemblanceRepository.saveAndFlush(ressemblance);

        int databaseSizeBeforeUpdate = ressemblanceRepository.findAll().size();

        // Update the ressemblance using partial update
        Ressemblance partialUpdatedRessemblance = new Ressemblance();
        partialUpdatedRessemblance.setId(ressemblance.getId());

        partialUpdatedRessemblance.description(UPDATED_DESCRIPTION);

        restRessemblanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRessemblance.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRessemblance))
            )
            .andExpect(status().isOk());

        // Validate the Ressemblance in the database
        List<Ressemblance> ressemblanceList = ressemblanceRepository.findAll();
        assertThat(ressemblanceList).hasSize(databaseSizeBeforeUpdate);
        Ressemblance testRessemblance = ressemblanceList.get(ressemblanceList.size() - 1);
        assertThat(testRessemblance.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingRessemblance() throws Exception {
        int databaseSizeBeforeUpdate = ressemblanceRepository.findAll().size();
        ressemblance.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRessemblanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ressemblance.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ressemblance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ressemblance in the database
        List<Ressemblance> ressemblanceList = ressemblanceRepository.findAll();
        assertThat(ressemblanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRessemblance() throws Exception {
        int databaseSizeBeforeUpdate = ressemblanceRepository.findAll().size();
        ressemblance.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRessemblanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ressemblance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ressemblance in the database
        List<Ressemblance> ressemblanceList = ressemblanceRepository.findAll();
        assertThat(ressemblanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRessemblance() throws Exception {
        int databaseSizeBeforeUpdate = ressemblanceRepository.findAll().size();
        ressemblance.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRessemblanceMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(ressemblance))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ressemblance in the database
        List<Ressemblance> ressemblanceList = ressemblanceRepository.findAll();
        assertThat(ressemblanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRessemblance() throws Exception {
        // Initialize the database
        ressemblanceRepository.saveAndFlush(ressemblance);

        int databaseSizeBeforeDelete = ressemblanceRepository.findAll().size();

        // Delete the ressemblance
        restRessemblanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, ressemblance.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ressemblance> ressemblanceList = ressemblanceRepository.findAll();
        assertThat(ressemblanceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
