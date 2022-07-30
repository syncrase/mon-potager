package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.NomVernaculaire;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.repository.NomVernaculaireRepository;
import fr.syncrase.ecosyst.service.criteria.NomVernaculaireCriteria;
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
 * Integration tests for the {@link NomVernaculaireResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NomVernaculaireResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/nom-vernaculaires";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NomVernaculaireRepository nomVernaculaireRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNomVernaculaireMockMvc;

    private NomVernaculaire nomVernaculaire;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NomVernaculaire createEntity(EntityManager em) {
        NomVernaculaire nomVernaculaire = new NomVernaculaire().nom(DEFAULT_NOM);
        return nomVernaculaire;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NomVernaculaire createUpdatedEntity(EntityManager em) {
        NomVernaculaire nomVernaculaire = new NomVernaculaire().nom(UPDATED_NOM);
        return nomVernaculaire;
    }

    @BeforeEach
    public void initTest() {
        nomVernaculaire = createEntity(em);
    }

    @Test
    @Transactional
    void createNomVernaculaire() throws Exception {
        int databaseSizeBeforeCreate = nomVernaculaireRepository.findAll().size();
        // Create the NomVernaculaire
        restNomVernaculaireMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(nomVernaculaire))
            )
            .andExpect(status().isCreated());

        // Validate the NomVernaculaire in the database
        List<NomVernaculaire> nomVernaculaireList = nomVernaculaireRepository.findAll();
        assertThat(nomVernaculaireList).hasSize(databaseSizeBeforeCreate + 1);
        NomVernaculaire testNomVernaculaire = nomVernaculaireList.get(nomVernaculaireList.size() - 1);
        assertThat(testNomVernaculaire.getNom()).isEqualTo(DEFAULT_NOM);
    }

    @Test
    @Transactional
    void createNomVernaculaireWithExistingId() throws Exception {
        // Create the NomVernaculaire with an existing ID
        nomVernaculaire.setId(1L);

        int databaseSizeBeforeCreate = nomVernaculaireRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNomVernaculaireMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(nomVernaculaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the NomVernaculaire in the database
        List<NomVernaculaire> nomVernaculaireList = nomVernaculaireRepository.findAll();
        assertThat(nomVernaculaireList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllNomVernaculaires() throws Exception {
        // Initialize the database
        nomVernaculaireRepository.saveAndFlush(nomVernaculaire);

        // Get all the nomVernaculaireList
        restNomVernaculaireMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(nomVernaculaire.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)));
    }

    @Test
    @Transactional
    void getNomVernaculaire() throws Exception {
        // Initialize the database
        nomVernaculaireRepository.saveAndFlush(nomVernaculaire);

        // Get the nomVernaculaire
        restNomVernaculaireMockMvc
            .perform(get(ENTITY_API_URL_ID, nomVernaculaire.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(nomVernaculaire.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM));
    }

    @Test
    @Transactional
    void getNomVernaculairesByIdFiltering() throws Exception {
        // Initialize the database
        nomVernaculaireRepository.saveAndFlush(nomVernaculaire);

        Long id = nomVernaculaire.getId();

        defaultNomVernaculaireShouldBeFound("id.equals=" + id);
        defaultNomVernaculaireShouldNotBeFound("id.notEquals=" + id);

        defaultNomVernaculaireShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultNomVernaculaireShouldNotBeFound("id.greaterThan=" + id);

        defaultNomVernaculaireShouldBeFound("id.lessThanOrEqual=" + id);
        defaultNomVernaculaireShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllNomVernaculairesByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        nomVernaculaireRepository.saveAndFlush(nomVernaculaire);

        // Get all the nomVernaculaireList where nom equals to DEFAULT_NOM
        defaultNomVernaculaireShouldBeFound("nom.equals=" + DEFAULT_NOM);

        // Get all the nomVernaculaireList where nom equals to UPDATED_NOM
        defaultNomVernaculaireShouldNotBeFound("nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllNomVernaculairesByNomIsNotEqualToSomething() throws Exception {
        // Initialize the database
        nomVernaculaireRepository.saveAndFlush(nomVernaculaire);

        // Get all the nomVernaculaireList where nom not equals to DEFAULT_NOM
        defaultNomVernaculaireShouldNotBeFound("nom.notEquals=" + DEFAULT_NOM);

        // Get all the nomVernaculaireList where nom not equals to UPDATED_NOM
        defaultNomVernaculaireShouldBeFound("nom.notEquals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllNomVernaculairesByNomIsInShouldWork() throws Exception {
        // Initialize the database
        nomVernaculaireRepository.saveAndFlush(nomVernaculaire);

        // Get all the nomVernaculaireList where nom in DEFAULT_NOM or UPDATED_NOM
        defaultNomVernaculaireShouldBeFound("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM);

        // Get all the nomVernaculaireList where nom equals to UPDATED_NOM
        defaultNomVernaculaireShouldNotBeFound("nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllNomVernaculairesByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        nomVernaculaireRepository.saveAndFlush(nomVernaculaire);

        // Get all the nomVernaculaireList where nom is not null
        defaultNomVernaculaireShouldBeFound("nom.specified=true");

        // Get all the nomVernaculaireList where nom is null
        defaultNomVernaculaireShouldNotBeFound("nom.specified=false");
    }

    @Test
    @Transactional
    void getAllNomVernaculairesByNomContainsSomething() throws Exception {
        // Initialize the database
        nomVernaculaireRepository.saveAndFlush(nomVernaculaire);

        // Get all the nomVernaculaireList where nom contains DEFAULT_NOM
        defaultNomVernaculaireShouldBeFound("nom.contains=" + DEFAULT_NOM);

        // Get all the nomVernaculaireList where nom contains UPDATED_NOM
        defaultNomVernaculaireShouldNotBeFound("nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllNomVernaculairesByNomNotContainsSomething() throws Exception {
        // Initialize the database
        nomVernaculaireRepository.saveAndFlush(nomVernaculaire);

        // Get all the nomVernaculaireList where nom does not contain DEFAULT_NOM
        defaultNomVernaculaireShouldNotBeFound("nom.doesNotContain=" + DEFAULT_NOM);

        // Get all the nomVernaculaireList where nom does not contain UPDATED_NOM
        defaultNomVernaculaireShouldBeFound("nom.doesNotContain=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllNomVernaculairesByPlantesIsEqualToSomething() throws Exception {
        // Initialize the database
        nomVernaculaireRepository.saveAndFlush(nomVernaculaire);
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
        nomVernaculaire.addPlantes(plantes);
        nomVernaculaireRepository.saveAndFlush(nomVernaculaire);
        Long plantesId = plantes.getId();

        // Get all the nomVernaculaireList where plantes equals to plantesId
        defaultNomVernaculaireShouldBeFound("plantesId.equals=" + plantesId);

        // Get all the nomVernaculaireList where plantes equals to (plantesId + 1)
        defaultNomVernaculaireShouldNotBeFound("plantesId.equals=" + (plantesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNomVernaculaireShouldBeFound(String filter) throws Exception {
        restNomVernaculaireMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(nomVernaculaire.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)));

        // Check, that the count call also returns 1
        restNomVernaculaireMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNomVernaculaireShouldNotBeFound(String filter) throws Exception {
        restNomVernaculaireMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNomVernaculaireMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingNomVernaculaire() throws Exception {
        // Get the nomVernaculaire
        restNomVernaculaireMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewNomVernaculaire() throws Exception {
        // Initialize the database
        nomVernaculaireRepository.saveAndFlush(nomVernaculaire);

        int databaseSizeBeforeUpdate = nomVernaculaireRepository.findAll().size();

        // Update the nomVernaculaire
        NomVernaculaire updatedNomVernaculaire = nomVernaculaireRepository.findById(nomVernaculaire.getId()).get();
        // Disconnect from session so that the updates on updatedNomVernaculaire are not directly saved in db
        em.detach(updatedNomVernaculaire);
        updatedNomVernaculaire.nom(UPDATED_NOM);

        restNomVernaculaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedNomVernaculaire.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedNomVernaculaire))
            )
            .andExpect(status().isOk());

        // Validate the NomVernaculaire in the database
        List<NomVernaculaire> nomVernaculaireList = nomVernaculaireRepository.findAll();
        assertThat(nomVernaculaireList).hasSize(databaseSizeBeforeUpdate);
        NomVernaculaire testNomVernaculaire = nomVernaculaireList.get(nomVernaculaireList.size() - 1);
        assertThat(testNomVernaculaire.getNom()).isEqualTo(UPDATED_NOM);
    }

    @Test
    @Transactional
    void putNonExistingNomVernaculaire() throws Exception {
        int databaseSizeBeforeUpdate = nomVernaculaireRepository.findAll().size();
        nomVernaculaire.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNomVernaculaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, nomVernaculaire.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(nomVernaculaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the NomVernaculaire in the database
        List<NomVernaculaire> nomVernaculaireList = nomVernaculaireRepository.findAll();
        assertThat(nomVernaculaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNomVernaculaire() throws Exception {
        int databaseSizeBeforeUpdate = nomVernaculaireRepository.findAll().size();
        nomVernaculaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNomVernaculaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(nomVernaculaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the NomVernaculaire in the database
        List<NomVernaculaire> nomVernaculaireList = nomVernaculaireRepository.findAll();
        assertThat(nomVernaculaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNomVernaculaire() throws Exception {
        int databaseSizeBeforeUpdate = nomVernaculaireRepository.findAll().size();
        nomVernaculaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNomVernaculaireMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(nomVernaculaire))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NomVernaculaire in the database
        List<NomVernaculaire> nomVernaculaireList = nomVernaculaireRepository.findAll();
        assertThat(nomVernaculaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNomVernaculaireWithPatch() throws Exception {
        // Initialize the database
        nomVernaculaireRepository.saveAndFlush(nomVernaculaire);

        int databaseSizeBeforeUpdate = nomVernaculaireRepository.findAll().size();

        // Update the nomVernaculaire using partial update
        NomVernaculaire partialUpdatedNomVernaculaire = new NomVernaculaire();
        partialUpdatedNomVernaculaire.setId(nomVernaculaire.getId());

        partialUpdatedNomVernaculaire.nom(UPDATED_NOM);

        restNomVernaculaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNomVernaculaire.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNomVernaculaire))
            )
            .andExpect(status().isOk());

        // Validate the NomVernaculaire in the database
        List<NomVernaculaire> nomVernaculaireList = nomVernaculaireRepository.findAll();
        assertThat(nomVernaculaireList).hasSize(databaseSizeBeforeUpdate);
        NomVernaculaire testNomVernaculaire = nomVernaculaireList.get(nomVernaculaireList.size() - 1);
        assertThat(testNomVernaculaire.getNom()).isEqualTo(UPDATED_NOM);
    }

    @Test
    @Transactional
    void fullUpdateNomVernaculaireWithPatch() throws Exception {
        // Initialize the database
        nomVernaculaireRepository.saveAndFlush(nomVernaculaire);

        int databaseSizeBeforeUpdate = nomVernaculaireRepository.findAll().size();

        // Update the nomVernaculaire using partial update
        NomVernaculaire partialUpdatedNomVernaculaire = new NomVernaculaire();
        partialUpdatedNomVernaculaire.setId(nomVernaculaire.getId());

        partialUpdatedNomVernaculaire.nom(UPDATED_NOM);

        restNomVernaculaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNomVernaculaire.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNomVernaculaire))
            )
            .andExpect(status().isOk());

        // Validate the NomVernaculaire in the database
        List<NomVernaculaire> nomVernaculaireList = nomVernaculaireRepository.findAll();
        assertThat(nomVernaculaireList).hasSize(databaseSizeBeforeUpdate);
        NomVernaculaire testNomVernaculaire = nomVernaculaireList.get(nomVernaculaireList.size() - 1);
        assertThat(testNomVernaculaire.getNom()).isEqualTo(UPDATED_NOM);
    }

    @Test
    @Transactional
    void patchNonExistingNomVernaculaire() throws Exception {
        int databaseSizeBeforeUpdate = nomVernaculaireRepository.findAll().size();
        nomVernaculaire.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNomVernaculaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, nomVernaculaire.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(nomVernaculaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the NomVernaculaire in the database
        List<NomVernaculaire> nomVernaculaireList = nomVernaculaireRepository.findAll();
        assertThat(nomVernaculaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNomVernaculaire() throws Exception {
        int databaseSizeBeforeUpdate = nomVernaculaireRepository.findAll().size();
        nomVernaculaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNomVernaculaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(nomVernaculaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the NomVernaculaire in the database
        List<NomVernaculaire> nomVernaculaireList = nomVernaculaireRepository.findAll();
        assertThat(nomVernaculaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNomVernaculaire() throws Exception {
        int databaseSizeBeforeUpdate = nomVernaculaireRepository.findAll().size();
        nomVernaculaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNomVernaculaireMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(nomVernaculaire))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NomVernaculaire in the database
        List<NomVernaculaire> nomVernaculaireList = nomVernaculaireRepository.findAll();
        assertThat(nomVernaculaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNomVernaculaire() throws Exception {
        // Initialize the database
        nomVernaculaireRepository.saveAndFlush(nomVernaculaire);

        int databaseSizeBeforeDelete = nomVernaculaireRepository.findAll().size();

        // Delete the nomVernaculaire
        restNomVernaculaireMockMvc
            .perform(delete(ENTITY_API_URL_ID, nomVernaculaire.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<NomVernaculaire> nomVernaculaireList = nomVernaculaireRepository.findAll();
        assertThat(nomVernaculaireList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
