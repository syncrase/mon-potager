package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Mois;
import fr.syncrase.ecosyst.repository.MoisRepository;
import fr.syncrase.ecosyst.service.criteria.MoisCriteria;
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
 * Integration tests for the {@link MoisResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MoisResourceIT {

    private static final Double DEFAULT_NUMERO = 1D;
    private static final Double UPDATED_NUMERO = 2D;
    private static final Double SMALLER_NUMERO = 1D - 1D;

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mois";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MoisRepository moisRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMoisMockMvc;

    private Mois mois;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mois createEntity(EntityManager em) {
        Mois mois = new Mois().numero(DEFAULT_NUMERO).nom(DEFAULT_NOM);
        return mois;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mois createUpdatedEntity(EntityManager em) {
        Mois mois = new Mois().numero(UPDATED_NUMERO).nom(UPDATED_NOM);
        return mois;
    }

    @BeforeEach
    public void initTest() {
        mois = createEntity(em);
    }

    @Test
    @Transactional
    void createMois() throws Exception {
        int databaseSizeBeforeCreate = moisRepository.findAll().size();
        // Create the Mois
        restMoisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mois)))
            .andExpect(status().isCreated());

        // Validate the Mois in the database
        List<Mois> moisList = moisRepository.findAll();
        assertThat(moisList).hasSize(databaseSizeBeforeCreate + 1);
        Mois testMois = moisList.get(moisList.size() - 1);
        assertThat(testMois.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testMois.getNom()).isEqualTo(DEFAULT_NOM);
    }

    @Test
    @Transactional
    void createMoisWithExistingId() throws Exception {
        // Create the Mois with an existing ID
        mois.setId(1L);

        int databaseSizeBeforeCreate = moisRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMoisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mois)))
            .andExpect(status().isBadRequest());

        // Validate the Mois in the database
        List<Mois> moisList = moisRepository.findAll();
        assertThat(moisList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNumeroIsRequired() throws Exception {
        int databaseSizeBeforeTest = moisRepository.findAll().size();
        // set the field null
        mois.setNumero(null);

        // Create the Mois, which fails.

        restMoisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mois)))
            .andExpect(status().isBadRequest());

        List<Mois> moisList = moisRepository.findAll();
        assertThat(moisList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = moisRepository.findAll().size();
        // set the field null
        mois.setNom(null);

        // Create the Mois, which fails.

        restMoisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mois)))
            .andExpect(status().isBadRequest());

        List<Mois> moisList = moisRepository.findAll();
        assertThat(moisList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMois() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get all the moisList
        restMoisMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mois.getId().intValue())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO.doubleValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)));
    }

    @Test
    @Transactional
    void getMois() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get the mois
        restMoisMockMvc
            .perform(get(ENTITY_API_URL_ID, mois.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mois.getId().intValue()))
            .andExpect(jsonPath("$.numero").value(DEFAULT_NUMERO.doubleValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM));
    }

    @Test
    @Transactional
    void getMoisByIdFiltering() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        Long id = mois.getId();

        defaultMoisShouldBeFound("id.equals=" + id);
        defaultMoisShouldNotBeFound("id.notEquals=" + id);

        defaultMoisShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultMoisShouldNotBeFound("id.greaterThan=" + id);

        defaultMoisShouldBeFound("id.lessThanOrEqual=" + id);
        defaultMoisShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMoisByNumeroIsEqualToSomething() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get all the moisList where numero equals to DEFAULT_NUMERO
        defaultMoisShouldBeFound("numero.equals=" + DEFAULT_NUMERO);

        // Get all the moisList where numero equals to UPDATED_NUMERO
        defaultMoisShouldNotBeFound("numero.equals=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllMoisByNumeroIsNotEqualToSomething() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get all the moisList where numero not equals to DEFAULT_NUMERO
        defaultMoisShouldNotBeFound("numero.notEquals=" + DEFAULT_NUMERO);

        // Get all the moisList where numero not equals to UPDATED_NUMERO
        defaultMoisShouldBeFound("numero.notEquals=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllMoisByNumeroIsInShouldWork() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get all the moisList where numero in DEFAULT_NUMERO or UPDATED_NUMERO
        defaultMoisShouldBeFound("numero.in=" + DEFAULT_NUMERO + "," + UPDATED_NUMERO);

        // Get all the moisList where numero equals to UPDATED_NUMERO
        defaultMoisShouldNotBeFound("numero.in=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllMoisByNumeroIsNullOrNotNull() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get all the moisList where numero is not null
        defaultMoisShouldBeFound("numero.specified=true");

        // Get all the moisList where numero is null
        defaultMoisShouldNotBeFound("numero.specified=false");
    }

    @Test
    @Transactional
    void getAllMoisByNumeroIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get all the moisList where numero is greater than or equal to DEFAULT_NUMERO
        defaultMoisShouldBeFound("numero.greaterThanOrEqual=" + DEFAULT_NUMERO);

        // Get all the moisList where numero is greater than or equal to UPDATED_NUMERO
        defaultMoisShouldNotBeFound("numero.greaterThanOrEqual=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllMoisByNumeroIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get all the moisList where numero is less than or equal to DEFAULT_NUMERO
        defaultMoisShouldBeFound("numero.lessThanOrEqual=" + DEFAULT_NUMERO);

        // Get all the moisList where numero is less than or equal to SMALLER_NUMERO
        defaultMoisShouldNotBeFound("numero.lessThanOrEqual=" + SMALLER_NUMERO);
    }

    @Test
    @Transactional
    void getAllMoisByNumeroIsLessThanSomething() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get all the moisList where numero is less than DEFAULT_NUMERO
        defaultMoisShouldNotBeFound("numero.lessThan=" + DEFAULT_NUMERO);

        // Get all the moisList where numero is less than UPDATED_NUMERO
        defaultMoisShouldBeFound("numero.lessThan=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllMoisByNumeroIsGreaterThanSomething() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get all the moisList where numero is greater than DEFAULT_NUMERO
        defaultMoisShouldNotBeFound("numero.greaterThan=" + DEFAULT_NUMERO);

        // Get all the moisList where numero is greater than SMALLER_NUMERO
        defaultMoisShouldBeFound("numero.greaterThan=" + SMALLER_NUMERO);
    }

    @Test
    @Transactional
    void getAllMoisByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get all the moisList where nom equals to DEFAULT_NOM
        defaultMoisShouldBeFound("nom.equals=" + DEFAULT_NOM);

        // Get all the moisList where nom equals to UPDATED_NOM
        defaultMoisShouldNotBeFound("nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllMoisByNomIsNotEqualToSomething() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get all the moisList where nom not equals to DEFAULT_NOM
        defaultMoisShouldNotBeFound("nom.notEquals=" + DEFAULT_NOM);

        // Get all the moisList where nom not equals to UPDATED_NOM
        defaultMoisShouldBeFound("nom.notEquals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllMoisByNomIsInShouldWork() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get all the moisList where nom in DEFAULT_NOM or UPDATED_NOM
        defaultMoisShouldBeFound("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM);

        // Get all the moisList where nom equals to UPDATED_NOM
        defaultMoisShouldNotBeFound("nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllMoisByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get all the moisList where nom is not null
        defaultMoisShouldBeFound("nom.specified=true");

        // Get all the moisList where nom is null
        defaultMoisShouldNotBeFound("nom.specified=false");
    }

    @Test
    @Transactional
    void getAllMoisByNomContainsSomething() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get all the moisList where nom contains DEFAULT_NOM
        defaultMoisShouldBeFound("nom.contains=" + DEFAULT_NOM);

        // Get all the moisList where nom contains UPDATED_NOM
        defaultMoisShouldNotBeFound("nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllMoisByNomNotContainsSomething() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        // Get all the moisList where nom does not contain DEFAULT_NOM
        defaultMoisShouldNotBeFound("nom.doesNotContain=" + DEFAULT_NOM);

        // Get all the moisList where nom does not contain UPDATED_NOM
        defaultMoisShouldBeFound("nom.doesNotContain=" + UPDATED_NOM);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMoisShouldBeFound(String filter) throws Exception {
        restMoisMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mois.getId().intValue())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO.doubleValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)));

        // Check, that the count call also returns 1
        restMoisMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMoisShouldNotBeFound(String filter) throws Exception {
        restMoisMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMoisMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMois() throws Exception {
        // Get the mois
        restMoisMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewMois() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        int databaseSizeBeforeUpdate = moisRepository.findAll().size();

        // Update the mois
        Mois updatedMois = moisRepository.findById(mois.getId()).get();
        // Disconnect from session so that the updates on updatedMois are not directly saved in db
        em.detach(updatedMois);
        updatedMois.numero(UPDATED_NUMERO).nom(UPDATED_NOM);

        restMoisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMois.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedMois))
            )
            .andExpect(status().isOk());

        // Validate the Mois in the database
        List<Mois> moisList = moisRepository.findAll();
        assertThat(moisList).hasSize(databaseSizeBeforeUpdate);
        Mois testMois = moisList.get(moisList.size() - 1);
        assertThat(testMois.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testMois.getNom()).isEqualTo(UPDATED_NOM);
    }

    @Test
    @Transactional
    void putNonExistingMois() throws Exception {
        int databaseSizeBeforeUpdate = moisRepository.findAll().size();
        mois.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMoisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mois.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mois))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mois in the database
        List<Mois> moisList = moisRepository.findAll();
        assertThat(moisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMois() throws Exception {
        int databaseSizeBeforeUpdate = moisRepository.findAll().size();
        mois.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMoisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mois))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mois in the database
        List<Mois> moisList = moisRepository.findAll();
        assertThat(moisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMois() throws Exception {
        int databaseSizeBeforeUpdate = moisRepository.findAll().size();
        mois.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMoisMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mois)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mois in the database
        List<Mois> moisList = moisRepository.findAll();
        assertThat(moisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMoisWithPatch() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        int databaseSizeBeforeUpdate = moisRepository.findAll().size();

        // Update the mois using partial update
        Mois partialUpdatedMois = new Mois();
        partialUpdatedMois.setId(mois.getId());

        partialUpdatedMois.numero(UPDATED_NUMERO).nom(UPDATED_NOM);

        restMoisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMois.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMois))
            )
            .andExpect(status().isOk());

        // Validate the Mois in the database
        List<Mois> moisList = moisRepository.findAll();
        assertThat(moisList).hasSize(databaseSizeBeforeUpdate);
        Mois testMois = moisList.get(moisList.size() - 1);
        assertThat(testMois.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testMois.getNom()).isEqualTo(UPDATED_NOM);
    }

    @Test
    @Transactional
    void fullUpdateMoisWithPatch() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        int databaseSizeBeforeUpdate = moisRepository.findAll().size();

        // Update the mois using partial update
        Mois partialUpdatedMois = new Mois();
        partialUpdatedMois.setId(mois.getId());

        partialUpdatedMois.numero(UPDATED_NUMERO).nom(UPDATED_NOM);

        restMoisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMois.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMois))
            )
            .andExpect(status().isOk());

        // Validate the Mois in the database
        List<Mois> moisList = moisRepository.findAll();
        assertThat(moisList).hasSize(databaseSizeBeforeUpdate);
        Mois testMois = moisList.get(moisList.size() - 1);
        assertThat(testMois.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testMois.getNom()).isEqualTo(UPDATED_NOM);
    }

    @Test
    @Transactional
    void patchNonExistingMois() throws Exception {
        int databaseSizeBeforeUpdate = moisRepository.findAll().size();
        mois.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMoisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, mois.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(mois))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mois in the database
        List<Mois> moisList = moisRepository.findAll();
        assertThat(moisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMois() throws Exception {
        int databaseSizeBeforeUpdate = moisRepository.findAll().size();
        mois.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMoisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(mois))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mois in the database
        List<Mois> moisList = moisRepository.findAll();
        assertThat(moisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMois() throws Exception {
        int databaseSizeBeforeUpdate = moisRepository.findAll().size();
        mois.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMoisMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(mois)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mois in the database
        List<Mois> moisList = moisRepository.findAll();
        assertThat(moisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMois() throws Exception {
        // Initialize the database
        moisRepository.saveAndFlush(mois);

        int databaseSizeBeforeDelete = moisRepository.findAll().size();

        // Delete the mois
        restMoisMockMvc
            .perform(delete(ENTITY_API_URL_ID, mois.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Mois> moisList = moisRepository.findAll();
        assertThat(moisList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
