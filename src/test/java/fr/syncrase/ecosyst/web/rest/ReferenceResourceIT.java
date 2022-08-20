package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.domain.Reference;
import fr.syncrase.ecosyst.domain.Url;
import fr.syncrase.ecosyst.domain.enumeration.ReferenceType;
import fr.syncrase.ecosyst.repository.ReferenceRepository;
import fr.syncrase.ecosyst.service.criteria.ReferenceCriteria;
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
 * Integration tests for the {@link ReferenceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReferenceResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ReferenceType DEFAULT_TYPE = ReferenceType.IMAGE;
    private static final ReferenceType UPDATED_TYPE = ReferenceType.SOURCE;

    private static final String ENTITY_API_URL = "/api/references";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ReferenceRepository referenceRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReferenceMockMvc;

    private Reference reference;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reference createEntity(EntityManager em) {
        Reference reference = new Reference().description(DEFAULT_DESCRIPTION).type(DEFAULT_TYPE);
        return reference;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reference createUpdatedEntity(EntityManager em) {
        Reference reference = new Reference().description(UPDATED_DESCRIPTION).type(UPDATED_TYPE);
        return reference;
    }

    @BeforeEach
    public void initTest() {
        reference = createEntity(em);
    }

    @Test
    @Transactional
    void createReference() throws Exception {
        int databaseSizeBeforeCreate = referenceRepository.findAll().size();
        // Create the Reference
        restReferenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reference)))
            .andExpect(status().isCreated());

        // Validate the Reference in the database
        List<Reference> referenceList = referenceRepository.findAll();
        assertThat(referenceList).hasSize(databaseSizeBeforeCreate + 1);
        Reference testReference = referenceList.get(referenceList.size() - 1);
        assertThat(testReference.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testReference.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void createReferenceWithExistingId() throws Exception {
        // Create the Reference with an existing ID
        reference.setId(1L);

        int databaseSizeBeforeCreate = referenceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReferenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reference)))
            .andExpect(status().isBadRequest());

        // Validate the Reference in the database
        List<Reference> referenceList = referenceRepository.findAll();
        assertThat(referenceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = referenceRepository.findAll().size();
        // set the field null
        reference.setType(null);

        // Create the Reference, which fails.

        restReferenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reference)))
            .andExpect(status().isBadRequest());

        List<Reference> referenceList = referenceRepository.findAll();
        assertThat(referenceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReferences() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        // Get all the referenceList
        restReferenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reference.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getReference() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        // Get the reference
        restReferenceMockMvc
            .perform(get(ENTITY_API_URL_ID, reference.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reference.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getReferencesByIdFiltering() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        Long id = reference.getId();

        defaultReferenceShouldBeFound("id.equals=" + id);
        defaultReferenceShouldNotBeFound("id.notEquals=" + id);

        defaultReferenceShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultReferenceShouldNotBeFound("id.greaterThan=" + id);

        defaultReferenceShouldBeFound("id.lessThanOrEqual=" + id);
        defaultReferenceShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReferencesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        // Get all the referenceList where description equals to DEFAULT_DESCRIPTION
        defaultReferenceShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the referenceList where description equals to UPDATED_DESCRIPTION
        defaultReferenceShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllReferencesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        // Get all the referenceList where description not equals to DEFAULT_DESCRIPTION
        defaultReferenceShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the referenceList where description not equals to UPDATED_DESCRIPTION
        defaultReferenceShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllReferencesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        // Get all the referenceList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultReferenceShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the referenceList where description equals to UPDATED_DESCRIPTION
        defaultReferenceShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllReferencesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        // Get all the referenceList where description is not null
        defaultReferenceShouldBeFound("description.specified=true");

        // Get all the referenceList where description is null
        defaultReferenceShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllReferencesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        // Get all the referenceList where description contains DEFAULT_DESCRIPTION
        defaultReferenceShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the referenceList where description contains UPDATED_DESCRIPTION
        defaultReferenceShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllReferencesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        // Get all the referenceList where description does not contain DEFAULT_DESCRIPTION
        defaultReferenceShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the referenceList where description does not contain UPDATED_DESCRIPTION
        defaultReferenceShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllReferencesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        // Get all the referenceList where type equals to DEFAULT_TYPE
        defaultReferenceShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the referenceList where type equals to UPDATED_TYPE
        defaultReferenceShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllReferencesByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        // Get all the referenceList where type not equals to DEFAULT_TYPE
        defaultReferenceShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the referenceList where type not equals to UPDATED_TYPE
        defaultReferenceShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllReferencesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        // Get all the referenceList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultReferenceShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the referenceList where type equals to UPDATED_TYPE
        defaultReferenceShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllReferencesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        // Get all the referenceList where type is not null
        defaultReferenceShouldBeFound("type.specified=true");

        // Get all the referenceList where type is null
        defaultReferenceShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllReferencesByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);
        Url url;
        if (TestUtil.findAll(em, Url.class).isEmpty()) {
            url = UrlResourceIT.createEntity(em);
            em.persist(url);
            em.flush();
        } else {
            url = TestUtil.findAll(em, Url.class).get(0);
        }
        em.persist(url);
        em.flush();
        reference.setUrl(url);
        referenceRepository.saveAndFlush(reference);
        Long urlId = url.getId();

        // Get all the referenceList where url equals to urlId
        defaultReferenceShouldBeFound("urlId.equals=" + urlId);

        // Get all the referenceList where url equals to (urlId + 1)
        defaultReferenceShouldNotBeFound("urlId.equals=" + (urlId + 1));
    }

    @Test
    @Transactional
    void getAllReferencesByPlantesIsEqualToSomething() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);
        Plante plantes;
        if (TestUtil.findAll(em, Plante.class).isEmpty()) {
            plantes = PlanteResourceIT.createEntity(em);
            em.persist(plantes);
            em.flush();
        } else {
            plantes = TestUtil.findAll(em, Plante.class).get(0);
        }
        em.persist(plantes);
        em.flush();
        reference.addPlantes(plantes);
        referenceRepository.saveAndFlush(reference);
        Long plantesId = plantes.getId();

        // Get all the referenceList where plantes equals to plantesId
        defaultReferenceShouldBeFound("plantesId.equals=" + plantesId);

        // Get all the referenceList where plantes equals to (plantesId + 1)
        defaultReferenceShouldNotBeFound("plantesId.equals=" + (plantesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReferenceShouldBeFound(String filter) throws Exception {
        restReferenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reference.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));

        // Check, that the count call also returns 1
        restReferenceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReferenceShouldNotBeFound(String filter) throws Exception {
        restReferenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReferenceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReference() throws Exception {
        // Get the reference
        restReferenceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewReference() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        int databaseSizeBeforeUpdate = referenceRepository.findAll().size();

        // Update the reference
        Reference updatedReference = referenceRepository.findById(reference.getId()).get();
        // Disconnect from session so that the updates on updatedReference are not directly saved in db
        em.detach(updatedReference);
        updatedReference.description(UPDATED_DESCRIPTION).type(UPDATED_TYPE);

        restReferenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedReference.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedReference))
            )
            .andExpect(status().isOk());

        // Validate the Reference in the database
        List<Reference> referenceList = referenceRepository.findAll();
        assertThat(referenceList).hasSize(databaseSizeBeforeUpdate);
        Reference testReference = referenceList.get(referenceList.size() - 1);
        assertThat(testReference.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testReference.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingReference() throws Exception {
        int databaseSizeBeforeUpdate = referenceRepository.findAll().size();
        reference.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReferenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reference.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reference))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reference in the database
        List<Reference> referenceList = referenceRepository.findAll();
        assertThat(referenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReference() throws Exception {
        int databaseSizeBeforeUpdate = referenceRepository.findAll().size();
        reference.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reference))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reference in the database
        List<Reference> referenceList = referenceRepository.findAll();
        assertThat(referenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReference() throws Exception {
        int databaseSizeBeforeUpdate = referenceRepository.findAll().size();
        reference.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferenceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reference)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reference in the database
        List<Reference> referenceList = referenceRepository.findAll();
        assertThat(referenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReferenceWithPatch() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        int databaseSizeBeforeUpdate = referenceRepository.findAll().size();

        // Update the reference using partial update
        Reference partialUpdatedReference = new Reference();
        partialUpdatedReference.setId(reference.getId());

        restReferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReference.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReference))
            )
            .andExpect(status().isOk());

        // Validate the Reference in the database
        List<Reference> referenceList = referenceRepository.findAll();
        assertThat(referenceList).hasSize(databaseSizeBeforeUpdate);
        Reference testReference = referenceList.get(referenceList.size() - 1);
        assertThat(testReference.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testReference.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateReferenceWithPatch() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        int databaseSizeBeforeUpdate = referenceRepository.findAll().size();

        // Update the reference using partial update
        Reference partialUpdatedReference = new Reference();
        partialUpdatedReference.setId(reference.getId());

        partialUpdatedReference.description(UPDATED_DESCRIPTION).type(UPDATED_TYPE);

        restReferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReference.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReference))
            )
            .andExpect(status().isOk());

        // Validate the Reference in the database
        List<Reference> referenceList = referenceRepository.findAll();
        assertThat(referenceList).hasSize(databaseSizeBeforeUpdate);
        Reference testReference = referenceList.get(referenceList.size() - 1);
        assertThat(testReference.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testReference.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingReference() throws Exception {
        int databaseSizeBeforeUpdate = referenceRepository.findAll().size();
        reference.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reference.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reference))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reference in the database
        List<Reference> referenceList = referenceRepository.findAll();
        assertThat(referenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReference() throws Exception {
        int databaseSizeBeforeUpdate = referenceRepository.findAll().size();
        reference.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reference))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reference in the database
        List<Reference> referenceList = referenceRepository.findAll();
        assertThat(referenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReference() throws Exception {
        int databaseSizeBeforeUpdate = referenceRepository.findAll().size();
        reference.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferenceMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(reference))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reference in the database
        List<Reference> referenceList = referenceRepository.findAll();
        assertThat(referenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReference() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        int databaseSizeBeforeDelete = referenceRepository.findAll().size();

        // Delete the reference
        restReferenceMockMvc
            .perform(delete(ENTITY_API_URL_ID, reference.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Reference> referenceList = referenceRepository.findAll();
        assertThat(referenceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
