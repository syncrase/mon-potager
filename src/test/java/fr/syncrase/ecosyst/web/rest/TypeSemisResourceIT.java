package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.TypeSemis;
import fr.syncrase.ecosyst.repository.TypeSemisRepository;
import fr.syncrase.ecosyst.service.criteria.TypeSemisCriteria;
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
 * Integration tests for the {@link TypeSemisResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TypeSemisResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/type-semis";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TypeSemisRepository typeSemisRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTypeSemisMockMvc;

    private TypeSemis typeSemis;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeSemis createEntity(EntityManager em) {
        TypeSemis typeSemis = new TypeSemis().type(DEFAULT_TYPE).description(DEFAULT_DESCRIPTION);
        return typeSemis;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeSemis createUpdatedEntity(EntityManager em) {
        TypeSemis typeSemis = new TypeSemis().type(UPDATED_TYPE).description(UPDATED_DESCRIPTION);
        return typeSemis;
    }

    @BeforeEach
    public void initTest() {
        typeSemis = createEntity(em);
    }

    @Test
    @Transactional
    void createTypeSemis() throws Exception {
        int databaseSizeBeforeCreate = typeSemisRepository.findAll().size();
        // Create the TypeSemis
        restTypeSemisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeSemis)))
            .andExpect(status().isCreated());

        // Validate the TypeSemis in the database
        List<TypeSemis> typeSemisList = typeSemisRepository.findAll();
        assertThat(typeSemisList).hasSize(databaseSizeBeforeCreate + 1);
        TypeSemis testTypeSemis = typeSemisList.get(typeSemisList.size() - 1);
        assertThat(testTypeSemis.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTypeSemis.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createTypeSemisWithExistingId() throws Exception {
        // Create the TypeSemis with an existing ID
        typeSemis.setId(1L);

        int databaseSizeBeforeCreate = typeSemisRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTypeSemisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeSemis)))
            .andExpect(status().isBadRequest());

        // Validate the TypeSemis in the database
        List<TypeSemis> typeSemisList = typeSemisRepository.findAll();
        assertThat(typeSemisList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = typeSemisRepository.findAll().size();
        // set the field null
        typeSemis.setType(null);

        // Create the TypeSemis, which fails.

        restTypeSemisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeSemis)))
            .andExpect(status().isBadRequest());

        List<TypeSemis> typeSemisList = typeSemisRepository.findAll();
        assertThat(typeSemisList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTypeSemis() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        // Get all the typeSemisList
        restTypeSemisMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typeSemis.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getTypeSemis() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        // Get the typeSemis
        restTypeSemisMockMvc
            .perform(get(ENTITY_API_URL_ID, typeSemis.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(typeSemis.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getTypeSemisByIdFiltering() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        Long id = typeSemis.getId();

        defaultTypeSemisShouldBeFound("id.equals=" + id);
        defaultTypeSemisShouldNotBeFound("id.notEquals=" + id);

        defaultTypeSemisShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTypeSemisShouldNotBeFound("id.greaterThan=" + id);

        defaultTypeSemisShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTypeSemisShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTypeSemisByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        // Get all the typeSemisList where type equals to DEFAULT_TYPE
        defaultTypeSemisShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the typeSemisList where type equals to UPDATED_TYPE
        defaultTypeSemisShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTypeSemisByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        // Get all the typeSemisList where type not equals to DEFAULT_TYPE
        defaultTypeSemisShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the typeSemisList where type not equals to UPDATED_TYPE
        defaultTypeSemisShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTypeSemisByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        // Get all the typeSemisList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultTypeSemisShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the typeSemisList where type equals to UPDATED_TYPE
        defaultTypeSemisShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTypeSemisByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        // Get all the typeSemisList where type is not null
        defaultTypeSemisShouldBeFound("type.specified=true");

        // Get all the typeSemisList where type is null
        defaultTypeSemisShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllTypeSemisByTypeContainsSomething() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        // Get all the typeSemisList where type contains DEFAULT_TYPE
        defaultTypeSemisShouldBeFound("type.contains=" + DEFAULT_TYPE);

        // Get all the typeSemisList where type contains UPDATED_TYPE
        defaultTypeSemisShouldNotBeFound("type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTypeSemisByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        // Get all the typeSemisList where type does not contain DEFAULT_TYPE
        defaultTypeSemisShouldNotBeFound("type.doesNotContain=" + DEFAULT_TYPE);

        // Get all the typeSemisList where type does not contain UPDATED_TYPE
        defaultTypeSemisShouldBeFound("type.doesNotContain=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTypeSemisByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        // Get all the typeSemisList where description equals to DEFAULT_DESCRIPTION
        defaultTypeSemisShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the typeSemisList where description equals to UPDATED_DESCRIPTION
        defaultTypeSemisShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTypeSemisByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        // Get all the typeSemisList where description not equals to DEFAULT_DESCRIPTION
        defaultTypeSemisShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the typeSemisList where description not equals to UPDATED_DESCRIPTION
        defaultTypeSemisShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTypeSemisByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        // Get all the typeSemisList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTypeSemisShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the typeSemisList where description equals to UPDATED_DESCRIPTION
        defaultTypeSemisShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTypeSemisByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        // Get all the typeSemisList where description is not null
        defaultTypeSemisShouldBeFound("description.specified=true");

        // Get all the typeSemisList where description is null
        defaultTypeSemisShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTypeSemisByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        // Get all the typeSemisList where description contains DEFAULT_DESCRIPTION
        defaultTypeSemisShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the typeSemisList where description contains UPDATED_DESCRIPTION
        defaultTypeSemisShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTypeSemisByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        // Get all the typeSemisList where description does not contain DEFAULT_DESCRIPTION
        defaultTypeSemisShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the typeSemisList where description does not contain UPDATED_DESCRIPTION
        defaultTypeSemisShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTypeSemisShouldBeFound(String filter) throws Exception {
        restTypeSemisMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typeSemis.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restTypeSemisMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTypeSemisShouldNotBeFound(String filter) throws Exception {
        restTypeSemisMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTypeSemisMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTypeSemis() throws Exception {
        // Get the typeSemis
        restTypeSemisMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTypeSemis() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        int databaseSizeBeforeUpdate = typeSemisRepository.findAll().size();

        // Update the typeSemis
        TypeSemis updatedTypeSemis = typeSemisRepository.findById(typeSemis.getId()).get();
        // Disconnect from session so that the updates on updatedTypeSemis are not directly saved in db
        em.detach(updatedTypeSemis);
        updatedTypeSemis.type(UPDATED_TYPE).description(UPDATED_DESCRIPTION);

        restTypeSemisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTypeSemis.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTypeSemis))
            )
            .andExpect(status().isOk());

        // Validate the TypeSemis in the database
        List<TypeSemis> typeSemisList = typeSemisRepository.findAll();
        assertThat(typeSemisList).hasSize(databaseSizeBeforeUpdate);
        TypeSemis testTypeSemis = typeSemisList.get(typeSemisList.size() - 1);
        assertThat(testTypeSemis.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTypeSemis.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingTypeSemis() throws Exception {
        int databaseSizeBeforeUpdate = typeSemisRepository.findAll().size();
        typeSemis.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypeSemisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, typeSemis.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typeSemis))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeSemis in the database
        List<TypeSemis> typeSemisList = typeSemisRepository.findAll();
        assertThat(typeSemisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTypeSemis() throws Exception {
        int databaseSizeBeforeUpdate = typeSemisRepository.findAll().size();
        typeSemis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeSemisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typeSemis))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeSemis in the database
        List<TypeSemis> typeSemisList = typeSemisRepository.findAll();
        assertThat(typeSemisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTypeSemis() throws Exception {
        int databaseSizeBeforeUpdate = typeSemisRepository.findAll().size();
        typeSemis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeSemisMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeSemis)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypeSemis in the database
        List<TypeSemis> typeSemisList = typeSemisRepository.findAll();
        assertThat(typeSemisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTypeSemisWithPatch() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        int databaseSizeBeforeUpdate = typeSemisRepository.findAll().size();

        // Update the typeSemis using partial update
        TypeSemis partialUpdatedTypeSemis = new TypeSemis();
        partialUpdatedTypeSemis.setId(typeSemis.getId());

        partialUpdatedTypeSemis.type(UPDATED_TYPE);

        restTypeSemisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypeSemis.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeSemis))
            )
            .andExpect(status().isOk());

        // Validate the TypeSemis in the database
        List<TypeSemis> typeSemisList = typeSemisRepository.findAll();
        assertThat(typeSemisList).hasSize(databaseSizeBeforeUpdate);
        TypeSemis testTypeSemis = typeSemisList.get(typeSemisList.size() - 1);
        assertThat(testTypeSemis.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTypeSemis.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateTypeSemisWithPatch() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        int databaseSizeBeforeUpdate = typeSemisRepository.findAll().size();

        // Update the typeSemis using partial update
        TypeSemis partialUpdatedTypeSemis = new TypeSemis();
        partialUpdatedTypeSemis.setId(typeSemis.getId());

        partialUpdatedTypeSemis.type(UPDATED_TYPE).description(UPDATED_DESCRIPTION);

        restTypeSemisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypeSemis.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeSemis))
            )
            .andExpect(status().isOk());

        // Validate the TypeSemis in the database
        List<TypeSemis> typeSemisList = typeSemisRepository.findAll();
        assertThat(typeSemisList).hasSize(databaseSizeBeforeUpdate);
        TypeSemis testTypeSemis = typeSemisList.get(typeSemisList.size() - 1);
        assertThat(testTypeSemis.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTypeSemis.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingTypeSemis() throws Exception {
        int databaseSizeBeforeUpdate = typeSemisRepository.findAll().size();
        typeSemis.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypeSemisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, typeSemis.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typeSemis))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeSemis in the database
        List<TypeSemis> typeSemisList = typeSemisRepository.findAll();
        assertThat(typeSemisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTypeSemis() throws Exception {
        int databaseSizeBeforeUpdate = typeSemisRepository.findAll().size();
        typeSemis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeSemisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typeSemis))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeSemis in the database
        List<TypeSemis> typeSemisList = typeSemisRepository.findAll();
        assertThat(typeSemisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTypeSemis() throws Exception {
        int databaseSizeBeforeUpdate = typeSemisRepository.findAll().size();
        typeSemis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeSemisMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(typeSemis))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypeSemis in the database
        List<TypeSemis> typeSemisList = typeSemisRepository.findAll();
        assertThat(typeSemisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTypeSemis() throws Exception {
        // Initialize the database
        typeSemisRepository.saveAndFlush(typeSemis);

        int databaseSizeBeforeDelete = typeSemisRepository.findAll().size();

        // Delete the typeSemis
        restTypeSemisMockMvc
            .perform(delete(ENTITY_API_URL_ID, typeSemis.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TypeSemis> typeSemisList = typeSemisRepository.findAll();
        assertThat(typeSemisList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
