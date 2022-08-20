package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Classification;
import fr.syncrase.ecosyst.domain.NomVernaculaire;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.domain.Reference;
import fr.syncrase.ecosyst.repository.PlanteRepository;
import fr.syncrase.ecosyst.service.PlanteService;
import fr.syncrase.ecosyst.service.criteria.PlanteCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PlanteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PlanteResourceIT {

    private static final String ENTITY_API_URL = "/api/plantes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private PlanteRepository planteRepository;

    @Mock
    private PlanteRepository planteRepositoryMock;

    @Mock
    private PlanteService planteServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlanteMockMvc;

    private Plante plante;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plante createEntity(EntityManager em) {
        Plante plante = new Plante();
        return plante;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plante createUpdatedEntity(EntityManager em) {
        Plante plante = new Plante();
        return plante;
    }

    @BeforeEach
    public void initTest() {
        plante = createEntity(em);
    }

    @Test
    @Transactional
    void createPlante() throws Exception {
        int databaseSizeBeforeCreate = planteRepository.findAll().size();
        // Create the Plante
        restPlanteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(plante)))
            .andExpect(status().isCreated());

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll();
        assertThat(planteList).hasSize(databaseSizeBeforeCreate + 1);
        Plante testPlante = planteList.get(planteList.size() - 1);
    }

    @Test
    @Transactional
    void createPlanteWithExistingId() throws Exception {
        // Create the Plante with an existing ID
        plante.setId(1L);

        int databaseSizeBeforeCreate = planteRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlanteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(plante)))
            .andExpect(status().isBadRequest());

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll();
        assertThat(planteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPlantes() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList
        restPlanteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(plante.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlantesWithEagerRelationshipsIsEnabled() throws Exception {
        when(planteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPlanteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(planteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlantesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(planteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPlanteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(planteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getPlante() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get the plante
        restPlanteMockMvc
            .perform(get(ENTITY_API_URL_ID, plante.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(plante.getId().intValue()));
    }

    @Test
    @Transactional
    void getPlantesByIdFiltering() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        Long id = plante.getId();

        defaultPlanteShouldBeFound("id.equals=" + id);
        defaultPlanteShouldNotBeFound("id.notEquals=" + id);

        defaultPlanteShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPlanteShouldNotBeFound("id.greaterThan=" + id);

        defaultPlanteShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPlanteShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPlantesByClassificationIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);
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
        plante.setClassification(classification);
        planteRepository.saveAndFlush(plante);
        Long classificationId = classification.getId();

        // Get all the planteList where classification equals to classificationId
        defaultPlanteShouldBeFound("classificationId.equals=" + classificationId);

        // Get all the planteList where classification equals to (classificationId + 1)
        defaultPlanteShouldNotBeFound("classificationId.equals=" + (classificationId + 1));
    }

    @Test
    @Transactional
    void getAllPlantesByNomsVernaculairesIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);
        NomVernaculaire nomsVernaculaires;
        if (TestUtil.findAll(em, NomVernaculaire.class).isEmpty()) {
            nomsVernaculaires = NomVernaculaireResourceIT.createEntity(em);
            em.persist(nomsVernaculaires);
            em.flush();
        } else {
            nomsVernaculaires = TestUtil.findAll(em, NomVernaculaire.class).get(0);
        }
        em.persist(nomsVernaculaires);
        em.flush();
        plante.addNomsVernaculaires(nomsVernaculaires);
        planteRepository.saveAndFlush(plante);
        Long nomsVernaculairesId = nomsVernaculaires.getId();

        // Get all the planteList where nomsVernaculaires equals to nomsVernaculairesId
        defaultPlanteShouldBeFound("nomsVernaculairesId.equals=" + nomsVernaculairesId);

        // Get all the planteList where nomsVernaculaires equals to (nomsVernaculairesId + 1)
        defaultPlanteShouldNotBeFound("nomsVernaculairesId.equals=" + (nomsVernaculairesId + 1));
    }

    @Test
    @Transactional
    void getAllPlantesByReferencesIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);
        Reference references;
        if (TestUtil.findAll(em, Reference.class).isEmpty()) {
            references = ReferenceResourceIT.createEntity(em);
            em.persist(references);
            em.flush();
        } else {
            references = TestUtil.findAll(em, Reference.class).get(0);
        }
        em.persist(references);
        em.flush();
        plante.addReferences(references);
        planteRepository.saveAndFlush(plante);
        Long referencesId = references.getId();

        // Get all the planteList where references equals to referencesId
        defaultPlanteShouldBeFound("referencesId.equals=" + referencesId);

        // Get all the planteList where references equals to (referencesId + 1)
        defaultPlanteShouldNotBeFound("referencesId.equals=" + (referencesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPlanteShouldBeFound(String filter) throws Exception {
        restPlanteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(plante.getId().intValue())));

        // Check, that the count call also returns 1
        restPlanteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPlanteShouldNotBeFound(String filter) throws Exception {
        restPlanteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPlanteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPlante() throws Exception {
        // Get the plante
        restPlanteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPlante() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        int databaseSizeBeforeUpdate = planteRepository.findAll().size();

        // Update the plante
        Plante updatedPlante = planteRepository.findById(plante.getId()).get();
        // Disconnect from session so that the updates on updatedPlante are not directly saved in db
        em.detach(updatedPlante);

        restPlanteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPlante.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPlante))
            )
            .andExpect(status().isOk());

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
        Plante testPlante = planteList.get(planteList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingPlante() throws Exception {
        int databaseSizeBeforeUpdate = planteRepository.findAll().size();
        plante.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlanteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, plante.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(plante))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlante() throws Exception {
        int databaseSizeBeforeUpdate = planteRepository.findAll().size();
        plante.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(plante))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlante() throws Exception {
        int databaseSizeBeforeUpdate = planteRepository.findAll().size();
        plante.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(plante)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlanteWithPatch() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        int databaseSizeBeforeUpdate = planteRepository.findAll().size();

        // Update the plante using partial update
        Plante partialUpdatedPlante = new Plante();
        partialUpdatedPlante.setId(plante.getId());

        restPlanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlante.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlante))
            )
            .andExpect(status().isOk());

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
        Plante testPlante = planteList.get(planteList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdatePlanteWithPatch() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        int databaseSizeBeforeUpdate = planteRepository.findAll().size();

        // Update the plante using partial update
        Plante partialUpdatedPlante = new Plante();
        partialUpdatedPlante.setId(plante.getId());

        restPlanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlante.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlante))
            )
            .andExpect(status().isOk());

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
        Plante testPlante = planteList.get(planteList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingPlante() throws Exception {
        int databaseSizeBeforeUpdate = planteRepository.findAll().size();
        plante.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, plante.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(plante))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlante() throws Exception {
        int databaseSizeBeforeUpdate = planteRepository.findAll().size();
        plante.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(plante))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlante() throws Exception {
        int databaseSizeBeforeUpdate = planteRepository.findAll().size();
        plante.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(plante)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Plante in the database
        List<Plante> planteList = planteRepository.findAll();
        assertThat(planteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlante() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        int databaseSizeBeforeDelete = planteRepository.findAll().size();

        // Delete the plante
        restPlanteMockMvc
            .perform(delete(ENTITY_API_URL_ID, plante.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Plante> planteList = planteRepository.findAll();
        assertThat(planteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
