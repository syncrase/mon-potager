package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Germination;
import fr.syncrase.ecosyst.repository.GerminationRepository;
import fr.syncrase.ecosyst.service.criteria.GerminationCriteria;
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
 * Integration tests for the {@link GerminationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GerminationResourceIT {

    private static final String DEFAULT_TEMPS_DE_GERMINATION = "AAAAAAAAAA";
    private static final String UPDATED_TEMPS_DE_GERMINATION = "BBBBBBBBBB";

    private static final String DEFAULT_CONDITION_DE_GERMINATION = "AAAAAAAAAA";
    private static final String UPDATED_CONDITION_DE_GERMINATION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/germinations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GerminationRepository germinationRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGerminationMockMvc;

    private Germination germination;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Germination createEntity(EntityManager em) {
        Germination germination = new Germination()
            .tempsDeGermination(DEFAULT_TEMPS_DE_GERMINATION)
            .conditionDeGermination(DEFAULT_CONDITION_DE_GERMINATION);
        return germination;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Germination createUpdatedEntity(EntityManager em) {
        Germination germination = new Germination()
            .tempsDeGermination(UPDATED_TEMPS_DE_GERMINATION)
            .conditionDeGermination(UPDATED_CONDITION_DE_GERMINATION);
        return germination;
    }

    @BeforeEach
    public void initTest() {
        germination = createEntity(em);
    }

    @Test
    @Transactional
    void createGermination() throws Exception {
        int databaseSizeBeforeCreate = germinationRepository.findAll().size();
        // Create the Germination
        restGerminationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(germination)))
            .andExpect(status().isCreated());

        // Validate the Germination in the database
        List<Germination> germinationList = germinationRepository.findAll();
        assertThat(germinationList).hasSize(databaseSizeBeforeCreate + 1);
        Germination testGermination = germinationList.get(germinationList.size() - 1);
        assertThat(testGermination.getTempsDeGermination()).isEqualTo(DEFAULT_TEMPS_DE_GERMINATION);
        assertThat(testGermination.getConditionDeGermination()).isEqualTo(DEFAULT_CONDITION_DE_GERMINATION);
    }

    @Test
    @Transactional
    void createGerminationWithExistingId() throws Exception {
        // Create the Germination with an existing ID
        germination.setId(1L);

        int databaseSizeBeforeCreate = germinationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGerminationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(germination)))
            .andExpect(status().isBadRequest());

        // Validate the Germination in the database
        List<Germination> germinationList = germinationRepository.findAll();
        assertThat(germinationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllGerminations() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        // Get all the germinationList
        restGerminationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(germination.getId().intValue())))
            .andExpect(jsonPath("$.[*].tempsDeGermination").value(hasItem(DEFAULT_TEMPS_DE_GERMINATION)))
            .andExpect(jsonPath("$.[*].conditionDeGermination").value(hasItem(DEFAULT_CONDITION_DE_GERMINATION)));
    }

    @Test
    @Transactional
    void getGermination() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        // Get the germination
        restGerminationMockMvc
            .perform(get(ENTITY_API_URL_ID, germination.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(germination.getId().intValue()))
            .andExpect(jsonPath("$.tempsDeGermination").value(DEFAULT_TEMPS_DE_GERMINATION))
            .andExpect(jsonPath("$.conditionDeGermination").value(DEFAULT_CONDITION_DE_GERMINATION));
    }

    @Test
    @Transactional
    void getGerminationsByIdFiltering() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        Long id = germination.getId();

        defaultGerminationShouldBeFound("id.equals=" + id);
        defaultGerminationShouldNotBeFound("id.notEquals=" + id);

        defaultGerminationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultGerminationShouldNotBeFound("id.greaterThan=" + id);

        defaultGerminationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultGerminationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllGerminationsByTempsDeGerminationIsEqualToSomething() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        // Get all the germinationList where tempsDeGermination equals to DEFAULT_TEMPS_DE_GERMINATION
        defaultGerminationShouldBeFound("tempsDeGermination.equals=" + DEFAULT_TEMPS_DE_GERMINATION);

        // Get all the germinationList where tempsDeGermination equals to UPDATED_TEMPS_DE_GERMINATION
        defaultGerminationShouldNotBeFound("tempsDeGermination.equals=" + UPDATED_TEMPS_DE_GERMINATION);
    }

    @Test
    @Transactional
    void getAllGerminationsByTempsDeGerminationIsNotEqualToSomething() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        // Get all the germinationList where tempsDeGermination not equals to DEFAULT_TEMPS_DE_GERMINATION
        defaultGerminationShouldNotBeFound("tempsDeGermination.notEquals=" + DEFAULT_TEMPS_DE_GERMINATION);

        // Get all the germinationList where tempsDeGermination not equals to UPDATED_TEMPS_DE_GERMINATION
        defaultGerminationShouldBeFound("tempsDeGermination.notEquals=" + UPDATED_TEMPS_DE_GERMINATION);
    }

    @Test
    @Transactional
    void getAllGerminationsByTempsDeGerminationIsInShouldWork() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        // Get all the germinationList where tempsDeGermination in DEFAULT_TEMPS_DE_GERMINATION or UPDATED_TEMPS_DE_GERMINATION
        defaultGerminationShouldBeFound("tempsDeGermination.in=" + DEFAULT_TEMPS_DE_GERMINATION + "," + UPDATED_TEMPS_DE_GERMINATION);

        // Get all the germinationList where tempsDeGermination equals to UPDATED_TEMPS_DE_GERMINATION
        defaultGerminationShouldNotBeFound("tempsDeGermination.in=" + UPDATED_TEMPS_DE_GERMINATION);
    }

    @Test
    @Transactional
    void getAllGerminationsByTempsDeGerminationIsNullOrNotNull() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        // Get all the germinationList where tempsDeGermination is not null
        defaultGerminationShouldBeFound("tempsDeGermination.specified=true");

        // Get all the germinationList where tempsDeGermination is null
        defaultGerminationShouldNotBeFound("tempsDeGermination.specified=false");
    }

    @Test
    @Transactional
    void getAllGerminationsByTempsDeGerminationContainsSomething() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        // Get all the germinationList where tempsDeGermination contains DEFAULT_TEMPS_DE_GERMINATION
        defaultGerminationShouldBeFound("tempsDeGermination.contains=" + DEFAULT_TEMPS_DE_GERMINATION);

        // Get all the germinationList where tempsDeGermination contains UPDATED_TEMPS_DE_GERMINATION
        defaultGerminationShouldNotBeFound("tempsDeGermination.contains=" + UPDATED_TEMPS_DE_GERMINATION);
    }

    @Test
    @Transactional
    void getAllGerminationsByTempsDeGerminationNotContainsSomething() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        // Get all the germinationList where tempsDeGermination does not contain DEFAULT_TEMPS_DE_GERMINATION
        defaultGerminationShouldNotBeFound("tempsDeGermination.doesNotContain=" + DEFAULT_TEMPS_DE_GERMINATION);

        // Get all the germinationList where tempsDeGermination does not contain UPDATED_TEMPS_DE_GERMINATION
        defaultGerminationShouldBeFound("tempsDeGermination.doesNotContain=" + UPDATED_TEMPS_DE_GERMINATION);
    }

    @Test
    @Transactional
    void getAllGerminationsByConditionDeGerminationIsEqualToSomething() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        // Get all the germinationList where conditionDeGermination equals to DEFAULT_CONDITION_DE_GERMINATION
        defaultGerminationShouldBeFound("conditionDeGermination.equals=" + DEFAULT_CONDITION_DE_GERMINATION);

        // Get all the germinationList where conditionDeGermination equals to UPDATED_CONDITION_DE_GERMINATION
        defaultGerminationShouldNotBeFound("conditionDeGermination.equals=" + UPDATED_CONDITION_DE_GERMINATION);
    }

    @Test
    @Transactional
    void getAllGerminationsByConditionDeGerminationIsNotEqualToSomething() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        // Get all the germinationList where conditionDeGermination not equals to DEFAULT_CONDITION_DE_GERMINATION
        defaultGerminationShouldNotBeFound("conditionDeGermination.notEquals=" + DEFAULT_CONDITION_DE_GERMINATION);

        // Get all the germinationList where conditionDeGermination not equals to UPDATED_CONDITION_DE_GERMINATION
        defaultGerminationShouldBeFound("conditionDeGermination.notEquals=" + UPDATED_CONDITION_DE_GERMINATION);
    }

    @Test
    @Transactional
    void getAllGerminationsByConditionDeGerminationIsInShouldWork() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        // Get all the germinationList where conditionDeGermination in DEFAULT_CONDITION_DE_GERMINATION or UPDATED_CONDITION_DE_GERMINATION
        defaultGerminationShouldBeFound(
            "conditionDeGermination.in=" + DEFAULT_CONDITION_DE_GERMINATION + "," + UPDATED_CONDITION_DE_GERMINATION
        );

        // Get all the germinationList where conditionDeGermination equals to UPDATED_CONDITION_DE_GERMINATION
        defaultGerminationShouldNotBeFound("conditionDeGermination.in=" + UPDATED_CONDITION_DE_GERMINATION);
    }

    @Test
    @Transactional
    void getAllGerminationsByConditionDeGerminationIsNullOrNotNull() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        // Get all the germinationList where conditionDeGermination is not null
        defaultGerminationShouldBeFound("conditionDeGermination.specified=true");

        // Get all the germinationList where conditionDeGermination is null
        defaultGerminationShouldNotBeFound("conditionDeGermination.specified=false");
    }

    @Test
    @Transactional
    void getAllGerminationsByConditionDeGerminationContainsSomething() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        // Get all the germinationList where conditionDeGermination contains DEFAULT_CONDITION_DE_GERMINATION
        defaultGerminationShouldBeFound("conditionDeGermination.contains=" + DEFAULT_CONDITION_DE_GERMINATION);

        // Get all the germinationList where conditionDeGermination contains UPDATED_CONDITION_DE_GERMINATION
        defaultGerminationShouldNotBeFound("conditionDeGermination.contains=" + UPDATED_CONDITION_DE_GERMINATION);
    }

    @Test
    @Transactional
    void getAllGerminationsByConditionDeGerminationNotContainsSomething() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        // Get all the germinationList where conditionDeGermination does not contain DEFAULT_CONDITION_DE_GERMINATION
        defaultGerminationShouldNotBeFound("conditionDeGermination.doesNotContain=" + DEFAULT_CONDITION_DE_GERMINATION);

        // Get all the germinationList where conditionDeGermination does not contain UPDATED_CONDITION_DE_GERMINATION
        defaultGerminationShouldBeFound("conditionDeGermination.doesNotContain=" + UPDATED_CONDITION_DE_GERMINATION);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultGerminationShouldBeFound(String filter) throws Exception {
        restGerminationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(germination.getId().intValue())))
            .andExpect(jsonPath("$.[*].tempsDeGermination").value(hasItem(DEFAULT_TEMPS_DE_GERMINATION)))
            .andExpect(jsonPath("$.[*].conditionDeGermination").value(hasItem(DEFAULT_CONDITION_DE_GERMINATION)));

        // Check, that the count call also returns 1
        restGerminationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultGerminationShouldNotBeFound(String filter) throws Exception {
        restGerminationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restGerminationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingGermination() throws Exception {
        // Get the germination
        restGerminationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGermination() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        int databaseSizeBeforeUpdate = germinationRepository.findAll().size();

        // Update the germination
        Germination updatedGermination = germinationRepository.findById(germination.getId()).get();
        // Disconnect from session so that the updates on updatedGermination are not directly saved in db
        em.detach(updatedGermination);
        updatedGermination.tempsDeGermination(UPDATED_TEMPS_DE_GERMINATION).conditionDeGermination(UPDATED_CONDITION_DE_GERMINATION);

        restGerminationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGermination.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGermination))
            )
            .andExpect(status().isOk());

        // Validate the Germination in the database
        List<Germination> germinationList = germinationRepository.findAll();
        assertThat(germinationList).hasSize(databaseSizeBeforeUpdate);
        Germination testGermination = germinationList.get(germinationList.size() - 1);
        assertThat(testGermination.getTempsDeGermination()).isEqualTo(UPDATED_TEMPS_DE_GERMINATION);
        assertThat(testGermination.getConditionDeGermination()).isEqualTo(UPDATED_CONDITION_DE_GERMINATION);
    }

    @Test
    @Transactional
    void putNonExistingGermination() throws Exception {
        int databaseSizeBeforeUpdate = germinationRepository.findAll().size();
        germination.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGerminationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, germination.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(germination))
            )
            .andExpect(status().isBadRequest());

        // Validate the Germination in the database
        List<Germination> germinationList = germinationRepository.findAll();
        assertThat(germinationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGermination() throws Exception {
        int databaseSizeBeforeUpdate = germinationRepository.findAll().size();
        germination.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGerminationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(germination))
            )
            .andExpect(status().isBadRequest());

        // Validate the Germination in the database
        List<Germination> germinationList = germinationRepository.findAll();
        assertThat(germinationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGermination() throws Exception {
        int databaseSizeBeforeUpdate = germinationRepository.findAll().size();
        germination.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGerminationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(germination)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Germination in the database
        List<Germination> germinationList = germinationRepository.findAll();
        assertThat(germinationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGerminationWithPatch() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        int databaseSizeBeforeUpdate = germinationRepository.findAll().size();

        // Update the germination using partial update
        Germination partialUpdatedGermination = new Germination();
        partialUpdatedGermination.setId(germination.getId());

        partialUpdatedGermination.conditionDeGermination(UPDATED_CONDITION_DE_GERMINATION);

        restGerminationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGermination.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGermination))
            )
            .andExpect(status().isOk());

        // Validate the Germination in the database
        List<Germination> germinationList = germinationRepository.findAll();
        assertThat(germinationList).hasSize(databaseSizeBeforeUpdate);
        Germination testGermination = germinationList.get(germinationList.size() - 1);
        assertThat(testGermination.getTempsDeGermination()).isEqualTo(DEFAULT_TEMPS_DE_GERMINATION);
        assertThat(testGermination.getConditionDeGermination()).isEqualTo(UPDATED_CONDITION_DE_GERMINATION);
    }

    @Test
    @Transactional
    void fullUpdateGerminationWithPatch() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        int databaseSizeBeforeUpdate = germinationRepository.findAll().size();

        // Update the germination using partial update
        Germination partialUpdatedGermination = new Germination();
        partialUpdatedGermination.setId(germination.getId());

        partialUpdatedGermination.tempsDeGermination(UPDATED_TEMPS_DE_GERMINATION).conditionDeGermination(UPDATED_CONDITION_DE_GERMINATION);

        restGerminationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGermination.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGermination))
            )
            .andExpect(status().isOk());

        // Validate the Germination in the database
        List<Germination> germinationList = germinationRepository.findAll();
        assertThat(germinationList).hasSize(databaseSizeBeforeUpdate);
        Germination testGermination = germinationList.get(germinationList.size() - 1);
        assertThat(testGermination.getTempsDeGermination()).isEqualTo(UPDATED_TEMPS_DE_GERMINATION);
        assertThat(testGermination.getConditionDeGermination()).isEqualTo(UPDATED_CONDITION_DE_GERMINATION);
    }

    @Test
    @Transactional
    void patchNonExistingGermination() throws Exception {
        int databaseSizeBeforeUpdate = germinationRepository.findAll().size();
        germination.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGerminationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, germination.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(germination))
            )
            .andExpect(status().isBadRequest());

        // Validate the Germination in the database
        List<Germination> germinationList = germinationRepository.findAll();
        assertThat(germinationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGermination() throws Exception {
        int databaseSizeBeforeUpdate = germinationRepository.findAll().size();
        germination.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGerminationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(germination))
            )
            .andExpect(status().isBadRequest());

        // Validate the Germination in the database
        List<Germination> germinationList = germinationRepository.findAll();
        assertThat(germinationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGermination() throws Exception {
        int databaseSizeBeforeUpdate = germinationRepository.findAll().size();
        germination.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGerminationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(germination))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Germination in the database
        List<Germination> germinationList = germinationRepository.findAll();
        assertThat(germinationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGermination() throws Exception {
        // Initialize the database
        germinationRepository.saveAndFlush(germination);

        int databaseSizeBeforeDelete = germinationRepository.findAll().size();

        // Delete the germination
        restGerminationMockMvc
            .perform(delete(ENTITY_API_URL_ID, germination.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Germination> germinationList = germinationRepository.findAll();
        assertThat(germinationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
