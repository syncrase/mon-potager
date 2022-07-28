package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.CycleDeVie;
import fr.syncrase.ecosyst.domain.Reproduction;
import fr.syncrase.ecosyst.repository.ReproductionRepository;
import fr.syncrase.ecosyst.service.criteria.ReproductionCriteria;
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
 * Integration tests for the {@link ReproductionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReproductionResourceIT {

    private static final String DEFAULT_VITESSE = "AAAAAAAAAA";
    private static final String UPDATED_VITESSE = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/reproductions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReproductionRepository reproductionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReproductionMockMvc;

    private Reproduction reproduction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reproduction createEntity(EntityManager em) {
        Reproduction reproduction = new Reproduction().vitesse(DEFAULT_VITESSE).type(DEFAULT_TYPE);
        return reproduction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reproduction createUpdatedEntity(EntityManager em) {
        Reproduction reproduction = new Reproduction().vitesse(UPDATED_VITESSE).type(UPDATED_TYPE);
        return reproduction;
    }

    @BeforeEach
    public void initTest() {
        reproduction = createEntity(em);
    }

    @Test
    @Transactional
    void createReproduction() throws Exception {
        int databaseSizeBeforeCreate = reproductionRepository.findAll().size();
        // Create the Reproduction
        restReproductionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reproduction)))
            .andExpect(status().isCreated());

        // Validate the Reproduction in the database
        List<Reproduction> reproductionList = reproductionRepository.findAll();
        assertThat(reproductionList).hasSize(databaseSizeBeforeCreate + 1);
        Reproduction testReproduction = reproductionList.get(reproductionList.size() - 1);
        assertThat(testReproduction.getVitesse()).isEqualTo(DEFAULT_VITESSE);
        assertThat(testReproduction.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void createReproductionWithExistingId() throws Exception {
        // Create the Reproduction with an existing ID
        reproduction.setId(1L);

        int databaseSizeBeforeCreate = reproductionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReproductionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reproduction)))
            .andExpect(status().isBadRequest());

        // Validate the Reproduction in the database
        List<Reproduction> reproductionList = reproductionRepository.findAll();
        assertThat(reproductionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllReproductions() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        // Get all the reproductionList
        restReproductionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reproduction.getId().intValue())))
            .andExpect(jsonPath("$.[*].vitesse").value(hasItem(DEFAULT_VITESSE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));
    }

    @Test
    @Transactional
    void getReproduction() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        // Get the reproduction
        restReproductionMockMvc
            .perform(get(ENTITY_API_URL_ID, reproduction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reproduction.getId().intValue()))
            .andExpect(jsonPath("$.vitesse").value(DEFAULT_VITESSE))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE));
    }

    @Test
    @Transactional
    void getReproductionsByIdFiltering() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        Long id = reproduction.getId();

        defaultReproductionShouldBeFound("id.equals=" + id);
        defaultReproductionShouldNotBeFound("id.notEquals=" + id);

        defaultReproductionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultReproductionShouldNotBeFound("id.greaterThan=" + id);

        defaultReproductionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultReproductionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReproductionsByVitesseIsEqualToSomething() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        // Get all the reproductionList where vitesse equals to DEFAULT_VITESSE
        defaultReproductionShouldBeFound("vitesse.equals=" + DEFAULT_VITESSE);

        // Get all the reproductionList where vitesse equals to UPDATED_VITESSE
        defaultReproductionShouldNotBeFound("vitesse.equals=" + UPDATED_VITESSE);
    }

    @Test
    @Transactional
    void getAllReproductionsByVitesseIsNotEqualToSomething() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        // Get all the reproductionList where vitesse not equals to DEFAULT_VITESSE
        defaultReproductionShouldNotBeFound("vitesse.notEquals=" + DEFAULT_VITESSE);

        // Get all the reproductionList where vitesse not equals to UPDATED_VITESSE
        defaultReproductionShouldBeFound("vitesse.notEquals=" + UPDATED_VITESSE);
    }

    @Test
    @Transactional
    void getAllReproductionsByVitesseIsInShouldWork() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        // Get all the reproductionList where vitesse in DEFAULT_VITESSE or UPDATED_VITESSE
        defaultReproductionShouldBeFound("vitesse.in=" + DEFAULT_VITESSE + "," + UPDATED_VITESSE);

        // Get all the reproductionList where vitesse equals to UPDATED_VITESSE
        defaultReproductionShouldNotBeFound("vitesse.in=" + UPDATED_VITESSE);
    }

    @Test
    @Transactional
    void getAllReproductionsByVitesseIsNullOrNotNull() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        // Get all the reproductionList where vitesse is not null
        defaultReproductionShouldBeFound("vitesse.specified=true");

        // Get all the reproductionList where vitesse is null
        defaultReproductionShouldNotBeFound("vitesse.specified=false");
    }

    @Test
    @Transactional
    void getAllReproductionsByVitesseContainsSomething() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        // Get all the reproductionList where vitesse contains DEFAULT_VITESSE
        defaultReproductionShouldBeFound("vitesse.contains=" + DEFAULT_VITESSE);

        // Get all the reproductionList where vitesse contains UPDATED_VITESSE
        defaultReproductionShouldNotBeFound("vitesse.contains=" + UPDATED_VITESSE);
    }

    @Test
    @Transactional
    void getAllReproductionsByVitesseNotContainsSomething() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        // Get all the reproductionList where vitesse does not contain DEFAULT_VITESSE
        defaultReproductionShouldNotBeFound("vitesse.doesNotContain=" + DEFAULT_VITESSE);

        // Get all the reproductionList where vitesse does not contain UPDATED_VITESSE
        defaultReproductionShouldBeFound("vitesse.doesNotContain=" + UPDATED_VITESSE);
    }

    @Test
    @Transactional
    void getAllReproductionsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        // Get all the reproductionList where type equals to DEFAULT_TYPE
        defaultReproductionShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the reproductionList where type equals to UPDATED_TYPE
        defaultReproductionShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllReproductionsByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        // Get all the reproductionList where type not equals to DEFAULT_TYPE
        defaultReproductionShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the reproductionList where type not equals to UPDATED_TYPE
        defaultReproductionShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllReproductionsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        // Get all the reproductionList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultReproductionShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the reproductionList where type equals to UPDATED_TYPE
        defaultReproductionShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllReproductionsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        // Get all the reproductionList where type is not null
        defaultReproductionShouldBeFound("type.specified=true");

        // Get all the reproductionList where type is null
        defaultReproductionShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllReproductionsByTypeContainsSomething() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        // Get all the reproductionList where type contains DEFAULT_TYPE
        defaultReproductionShouldBeFound("type.contains=" + DEFAULT_TYPE);

        // Get all the reproductionList where type contains UPDATED_TYPE
        defaultReproductionShouldNotBeFound("type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllReproductionsByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        // Get all the reproductionList where type does not contain DEFAULT_TYPE
        defaultReproductionShouldNotBeFound("type.doesNotContain=" + DEFAULT_TYPE);

        // Get all the reproductionList where type does not contain UPDATED_TYPE
        defaultReproductionShouldBeFound("type.doesNotContain=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllReproductionsByCycleDeVieIsEqualToSomething() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);
        CycleDeVie cycleDeVie;
        if (TestUtil.findAll(em, CycleDeVie.class).isEmpty()) {
            cycleDeVie = CycleDeVieResourceIT.createEntity(em);
            em.persist(cycleDeVie);
            em.flush();
        } else {
            cycleDeVie = TestUtil.findAll(em, CycleDeVie.class).get(0);
        }
        em.persist(cycleDeVie);
        em.flush();
        reproduction.addCycleDeVie(cycleDeVie);
        reproductionRepository.saveAndFlush(reproduction);
        Long cycleDeVieId = cycleDeVie.getId();

        // Get all the reproductionList where cycleDeVie equals to cycleDeVieId
        defaultReproductionShouldBeFound("cycleDeVieId.equals=" + cycleDeVieId);

        // Get all the reproductionList where cycleDeVie equals to (cycleDeVieId + 1)
        defaultReproductionShouldNotBeFound("cycleDeVieId.equals=" + (cycleDeVieId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReproductionShouldBeFound(String filter) throws Exception {
        restReproductionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reproduction.getId().intValue())))
            .andExpect(jsonPath("$.[*].vitesse").value(hasItem(DEFAULT_VITESSE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));

        // Check, that the count call also returns 1
        restReproductionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReproductionShouldNotBeFound(String filter) throws Exception {
        restReproductionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReproductionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReproduction() throws Exception {
        // Get the reproduction
        restReproductionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewReproduction() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        int databaseSizeBeforeUpdate = reproductionRepository.findAll().size();

        // Update the reproduction
        Reproduction updatedReproduction = reproductionRepository.findById(reproduction.getId()).get();
        // Disconnect from session so that the updates on updatedReproduction are not directly saved in db
        em.detach(updatedReproduction);
        updatedReproduction.vitesse(UPDATED_VITESSE).type(UPDATED_TYPE);

        restReproductionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedReproduction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedReproduction))
            )
            .andExpect(status().isOk());

        // Validate the Reproduction in the database
        List<Reproduction> reproductionList = reproductionRepository.findAll();
        assertThat(reproductionList).hasSize(databaseSizeBeforeUpdate);
        Reproduction testReproduction = reproductionList.get(reproductionList.size() - 1);
        assertThat(testReproduction.getVitesse()).isEqualTo(UPDATED_VITESSE);
        assertThat(testReproduction.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingReproduction() throws Exception {
        int databaseSizeBeforeUpdate = reproductionRepository.findAll().size();
        reproduction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReproductionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reproduction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reproduction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reproduction in the database
        List<Reproduction> reproductionList = reproductionRepository.findAll();
        assertThat(reproductionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReproduction() throws Exception {
        int databaseSizeBeforeUpdate = reproductionRepository.findAll().size();
        reproduction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReproductionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reproduction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reproduction in the database
        List<Reproduction> reproductionList = reproductionRepository.findAll();
        assertThat(reproductionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReproduction() throws Exception {
        int databaseSizeBeforeUpdate = reproductionRepository.findAll().size();
        reproduction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReproductionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reproduction)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reproduction in the database
        List<Reproduction> reproductionList = reproductionRepository.findAll();
        assertThat(reproductionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReproductionWithPatch() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        int databaseSizeBeforeUpdate = reproductionRepository.findAll().size();

        // Update the reproduction using partial update
        Reproduction partialUpdatedReproduction = new Reproduction();
        partialUpdatedReproduction.setId(reproduction.getId());

        partialUpdatedReproduction.type(UPDATED_TYPE);

        restReproductionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReproduction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReproduction))
            )
            .andExpect(status().isOk());

        // Validate the Reproduction in the database
        List<Reproduction> reproductionList = reproductionRepository.findAll();
        assertThat(reproductionList).hasSize(databaseSizeBeforeUpdate);
        Reproduction testReproduction = reproductionList.get(reproductionList.size() - 1);
        assertThat(testReproduction.getVitesse()).isEqualTo(DEFAULT_VITESSE);
        assertThat(testReproduction.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateReproductionWithPatch() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        int databaseSizeBeforeUpdate = reproductionRepository.findAll().size();

        // Update the reproduction using partial update
        Reproduction partialUpdatedReproduction = new Reproduction();
        partialUpdatedReproduction.setId(reproduction.getId());

        partialUpdatedReproduction.vitesse(UPDATED_VITESSE).type(UPDATED_TYPE);

        restReproductionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReproduction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReproduction))
            )
            .andExpect(status().isOk());

        // Validate the Reproduction in the database
        List<Reproduction> reproductionList = reproductionRepository.findAll();
        assertThat(reproductionList).hasSize(databaseSizeBeforeUpdate);
        Reproduction testReproduction = reproductionList.get(reproductionList.size() - 1);
        assertThat(testReproduction.getVitesse()).isEqualTo(UPDATED_VITESSE);
        assertThat(testReproduction.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingReproduction() throws Exception {
        int databaseSizeBeforeUpdate = reproductionRepository.findAll().size();
        reproduction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReproductionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reproduction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reproduction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reproduction in the database
        List<Reproduction> reproductionList = reproductionRepository.findAll();
        assertThat(reproductionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReproduction() throws Exception {
        int databaseSizeBeforeUpdate = reproductionRepository.findAll().size();
        reproduction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReproductionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reproduction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reproduction in the database
        List<Reproduction> reproductionList = reproductionRepository.findAll();
        assertThat(reproductionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReproduction() throws Exception {
        int databaseSizeBeforeUpdate = reproductionRepository.findAll().size();
        reproduction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReproductionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(reproduction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reproduction in the database
        List<Reproduction> reproductionList = reproductionRepository.findAll();
        assertThat(reproductionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReproduction() throws Exception {
        // Initialize the database
        reproductionRepository.saveAndFlush(reproduction);

        int databaseSizeBeforeDelete = reproductionRepository.findAll().size();

        // Delete the reproduction
        restReproductionMockMvc
            .perform(delete(ENTITY_API_URL_ID, reproduction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Reproduction> reproductionList = reproductionRepository.findAll();
        assertThat(reproductionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
