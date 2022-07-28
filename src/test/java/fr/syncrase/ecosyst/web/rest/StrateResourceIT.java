package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Strate;
import fr.syncrase.ecosyst.repository.StrateRepository;
import fr.syncrase.ecosyst.service.criteria.StrateCriteria;
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
 * Integration tests for the {@link StrateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StrateResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/strates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StrateRepository strateRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStrateMockMvc;

    private Strate strate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Strate createEntity(EntityManager em) {
        Strate strate = new Strate().type(DEFAULT_TYPE);
        return strate;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Strate createUpdatedEntity(EntityManager em) {
        Strate strate = new Strate().type(UPDATED_TYPE);
        return strate;
    }

    @BeforeEach
    public void initTest() {
        strate = createEntity(em);
    }

    @Test
    @Transactional
    void createStrate() throws Exception {
        int databaseSizeBeforeCreate = strateRepository.findAll().size();
        // Create the Strate
        restStrateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(strate)))
            .andExpect(status().isCreated());

        // Validate the Strate in the database
        List<Strate> strateList = strateRepository.findAll();
        assertThat(strateList).hasSize(databaseSizeBeforeCreate + 1);
        Strate testStrate = strateList.get(strateList.size() - 1);
        assertThat(testStrate.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void createStrateWithExistingId() throws Exception {
        // Create the Strate with an existing ID
        strate.setId(1L);

        int databaseSizeBeforeCreate = strateRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStrateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(strate)))
            .andExpect(status().isBadRequest());

        // Validate the Strate in the database
        List<Strate> strateList = strateRepository.findAll();
        assertThat(strateList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStrates() throws Exception {
        // Initialize the database
        strateRepository.saveAndFlush(strate);

        // Get all the strateList
        restStrateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(strate.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));
    }

    @Test
    @Transactional
    void getStrate() throws Exception {
        // Initialize the database
        strateRepository.saveAndFlush(strate);

        // Get the strate
        restStrateMockMvc
            .perform(get(ENTITY_API_URL_ID, strate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(strate.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE));
    }

    @Test
    @Transactional
    void getStratesByIdFiltering() throws Exception {
        // Initialize the database
        strateRepository.saveAndFlush(strate);

        Long id = strate.getId();

        defaultStrateShouldBeFound("id.equals=" + id);
        defaultStrateShouldNotBeFound("id.notEquals=" + id);

        defaultStrateShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStrateShouldNotBeFound("id.greaterThan=" + id);

        defaultStrateShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStrateShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStratesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        strateRepository.saveAndFlush(strate);

        // Get all the strateList where type equals to DEFAULT_TYPE
        defaultStrateShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the strateList where type equals to UPDATED_TYPE
        defaultStrateShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllStratesByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        strateRepository.saveAndFlush(strate);

        // Get all the strateList where type not equals to DEFAULT_TYPE
        defaultStrateShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the strateList where type not equals to UPDATED_TYPE
        defaultStrateShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllStratesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        strateRepository.saveAndFlush(strate);

        // Get all the strateList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultStrateShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the strateList where type equals to UPDATED_TYPE
        defaultStrateShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllStratesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        strateRepository.saveAndFlush(strate);

        // Get all the strateList where type is not null
        defaultStrateShouldBeFound("type.specified=true");

        // Get all the strateList where type is null
        defaultStrateShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllStratesByTypeContainsSomething() throws Exception {
        // Initialize the database
        strateRepository.saveAndFlush(strate);

        // Get all the strateList where type contains DEFAULT_TYPE
        defaultStrateShouldBeFound("type.contains=" + DEFAULT_TYPE);

        // Get all the strateList where type contains UPDATED_TYPE
        defaultStrateShouldNotBeFound("type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllStratesByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        strateRepository.saveAndFlush(strate);

        // Get all the strateList where type does not contain DEFAULT_TYPE
        defaultStrateShouldNotBeFound("type.doesNotContain=" + DEFAULT_TYPE);

        // Get all the strateList where type does not contain UPDATED_TYPE
        defaultStrateShouldBeFound("type.doesNotContain=" + UPDATED_TYPE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStrateShouldBeFound(String filter) throws Exception {
        restStrateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(strate.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));

        // Check, that the count call also returns 1
        restStrateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStrateShouldNotBeFound(String filter) throws Exception {
        restStrateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStrateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStrate() throws Exception {
        // Get the strate
        restStrateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewStrate() throws Exception {
        // Initialize the database
        strateRepository.saveAndFlush(strate);

        int databaseSizeBeforeUpdate = strateRepository.findAll().size();

        // Update the strate
        Strate updatedStrate = strateRepository.findById(strate.getId()).get();
        // Disconnect from session so that the updates on updatedStrate are not directly saved in db
        em.detach(updatedStrate);
        updatedStrate.type(UPDATED_TYPE);

        restStrateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStrate.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStrate))
            )
            .andExpect(status().isOk());

        // Validate the Strate in the database
        List<Strate> strateList = strateRepository.findAll();
        assertThat(strateList).hasSize(databaseSizeBeforeUpdate);
        Strate testStrate = strateList.get(strateList.size() - 1);
        assertThat(testStrate.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingStrate() throws Exception {
        int databaseSizeBeforeUpdate = strateRepository.findAll().size();
        strate.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStrateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, strate.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(strate))
            )
            .andExpect(status().isBadRequest());

        // Validate the Strate in the database
        List<Strate> strateList = strateRepository.findAll();
        assertThat(strateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStrate() throws Exception {
        int databaseSizeBeforeUpdate = strateRepository.findAll().size();
        strate.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStrateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(strate))
            )
            .andExpect(status().isBadRequest());

        // Validate the Strate in the database
        List<Strate> strateList = strateRepository.findAll();
        assertThat(strateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStrate() throws Exception {
        int databaseSizeBeforeUpdate = strateRepository.findAll().size();
        strate.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStrateMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(strate)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Strate in the database
        List<Strate> strateList = strateRepository.findAll();
        assertThat(strateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStrateWithPatch() throws Exception {
        // Initialize the database
        strateRepository.saveAndFlush(strate);

        int databaseSizeBeforeUpdate = strateRepository.findAll().size();

        // Update the strate using partial update
        Strate partialUpdatedStrate = new Strate();
        partialUpdatedStrate.setId(strate.getId());

        partialUpdatedStrate.type(UPDATED_TYPE);

        restStrateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStrate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStrate))
            )
            .andExpect(status().isOk());

        // Validate the Strate in the database
        List<Strate> strateList = strateRepository.findAll();
        assertThat(strateList).hasSize(databaseSizeBeforeUpdate);
        Strate testStrate = strateList.get(strateList.size() - 1);
        assertThat(testStrate.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateStrateWithPatch() throws Exception {
        // Initialize the database
        strateRepository.saveAndFlush(strate);

        int databaseSizeBeforeUpdate = strateRepository.findAll().size();

        // Update the strate using partial update
        Strate partialUpdatedStrate = new Strate();
        partialUpdatedStrate.setId(strate.getId());

        partialUpdatedStrate.type(UPDATED_TYPE);

        restStrateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStrate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStrate))
            )
            .andExpect(status().isOk());

        // Validate the Strate in the database
        List<Strate> strateList = strateRepository.findAll();
        assertThat(strateList).hasSize(databaseSizeBeforeUpdate);
        Strate testStrate = strateList.get(strateList.size() - 1);
        assertThat(testStrate.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingStrate() throws Exception {
        int databaseSizeBeforeUpdate = strateRepository.findAll().size();
        strate.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStrateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, strate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(strate))
            )
            .andExpect(status().isBadRequest());

        // Validate the Strate in the database
        List<Strate> strateList = strateRepository.findAll();
        assertThat(strateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStrate() throws Exception {
        int databaseSizeBeforeUpdate = strateRepository.findAll().size();
        strate.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStrateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(strate))
            )
            .andExpect(status().isBadRequest());

        // Validate the Strate in the database
        List<Strate> strateList = strateRepository.findAll();
        assertThat(strateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStrate() throws Exception {
        int databaseSizeBeforeUpdate = strateRepository.findAll().size();
        strate.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStrateMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(strate)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Strate in the database
        List<Strate> strateList = strateRepository.findAll();
        assertThat(strateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStrate() throws Exception {
        // Initialize the database
        strateRepository.saveAndFlush(strate);

        int databaseSizeBeforeDelete = strateRepository.findAll().size();

        // Delete the strate
        restStrateMockMvc
            .perform(delete(ENTITY_API_URL_ID, strate.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Strate> strateList = strateRepository.findAll();
        assertThat(strateList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
