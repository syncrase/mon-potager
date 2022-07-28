package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Mois;
import fr.syncrase.ecosyst.domain.PeriodeAnnee;
import fr.syncrase.ecosyst.repository.PeriodeAnneeRepository;
import fr.syncrase.ecosyst.service.PeriodeAnneeService;
import fr.syncrase.ecosyst.service.criteria.PeriodeAnneeCriteria;
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
 * Integration tests for the {@link PeriodeAnneeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PeriodeAnneeResourceIT {

    private static final String ENTITY_API_URL = "/api/periode-annees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PeriodeAnneeRepository periodeAnneeRepository;

    @Mock
    private PeriodeAnneeRepository periodeAnneeRepositoryMock;

    @Mock
    private PeriodeAnneeService periodeAnneeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPeriodeAnneeMockMvc;

    private PeriodeAnnee periodeAnnee;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PeriodeAnnee createEntity(EntityManager em) {
        PeriodeAnnee periodeAnnee = new PeriodeAnnee();
        // Add required entity
        Mois mois;
        if (TestUtil.findAll(em, Mois.class).isEmpty()) {
            mois = MoisResourceIT.createEntity(em);
            em.persist(mois);
            em.flush();
        } else {
            mois = TestUtil.findAll(em, Mois.class).get(0);
        }
        periodeAnnee.setDebut(mois);
        // Add required entity
        periodeAnnee.setFin(mois);
        return periodeAnnee;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PeriodeAnnee createUpdatedEntity(EntityManager em) {
        PeriodeAnnee periodeAnnee = new PeriodeAnnee();
        // Add required entity
        Mois mois;
        if (TestUtil.findAll(em, Mois.class).isEmpty()) {
            mois = MoisResourceIT.createUpdatedEntity(em);
            em.persist(mois);
            em.flush();
        } else {
            mois = TestUtil.findAll(em, Mois.class).get(0);
        }
        periodeAnnee.setDebut(mois);
        // Add required entity
        periodeAnnee.setFin(mois);
        return periodeAnnee;
    }

    @BeforeEach
    public void initTest() {
        periodeAnnee = createEntity(em);
    }

    @Test
    @Transactional
    void createPeriodeAnnee() throws Exception {
        int databaseSizeBeforeCreate = periodeAnneeRepository.findAll().size();
        // Create the PeriodeAnnee
        restPeriodeAnneeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(periodeAnnee)))
            .andExpect(status().isCreated());

        // Validate the PeriodeAnnee in the database
        List<PeriodeAnnee> periodeAnneeList = periodeAnneeRepository.findAll();
        assertThat(periodeAnneeList).hasSize(databaseSizeBeforeCreate + 1);
        PeriodeAnnee testPeriodeAnnee = periodeAnneeList.get(periodeAnneeList.size() - 1);
    }

    @Test
    @Transactional
    void createPeriodeAnneeWithExistingId() throws Exception {
        // Create the PeriodeAnnee with an existing ID
        periodeAnnee.setId(1L);

        int databaseSizeBeforeCreate = periodeAnneeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPeriodeAnneeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(periodeAnnee)))
            .andExpect(status().isBadRequest());

        // Validate the PeriodeAnnee in the database
        List<PeriodeAnnee> periodeAnneeList = periodeAnneeRepository.findAll();
        assertThat(periodeAnneeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPeriodeAnnees() throws Exception {
        // Initialize the database
        periodeAnneeRepository.saveAndFlush(periodeAnnee);

        // Get all the periodeAnneeList
        restPeriodeAnneeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(periodeAnnee.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPeriodeAnneesWithEagerRelationshipsIsEnabled() throws Exception {
        when(periodeAnneeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPeriodeAnneeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(periodeAnneeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPeriodeAnneesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(periodeAnneeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPeriodeAnneeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(periodeAnneeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getPeriodeAnnee() throws Exception {
        // Initialize the database
        periodeAnneeRepository.saveAndFlush(periodeAnnee);

        // Get the periodeAnnee
        restPeriodeAnneeMockMvc
            .perform(get(ENTITY_API_URL_ID, periodeAnnee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(periodeAnnee.getId().intValue()));
    }

    @Test
    @Transactional
    void getPeriodeAnneesByIdFiltering() throws Exception {
        // Initialize the database
        periodeAnneeRepository.saveAndFlush(periodeAnnee);

        Long id = periodeAnnee.getId();

        defaultPeriodeAnneeShouldBeFound("id.equals=" + id);
        defaultPeriodeAnneeShouldNotBeFound("id.notEquals=" + id);

        defaultPeriodeAnneeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPeriodeAnneeShouldNotBeFound("id.greaterThan=" + id);

        defaultPeriodeAnneeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPeriodeAnneeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPeriodeAnneesByDebutIsEqualToSomething() throws Exception {
        // Initialize the database
        periodeAnneeRepository.saveAndFlush(periodeAnnee);
        Mois debut;
        if (TestUtil.findAll(em, Mois.class).isEmpty()) {
            debut = MoisResourceIT.createEntity(em);
            em.persist(debut);
            em.flush();
        } else {
            debut = TestUtil.findAll(em, Mois.class).get(0);
        }
        em.persist(debut);
        em.flush();
        periodeAnnee.setDebut(debut);
        periodeAnneeRepository.saveAndFlush(periodeAnnee);
        Long debutId = debut.getId();

        // Get all the periodeAnneeList where debut equals to debutId
        defaultPeriodeAnneeShouldBeFound("debutId.equals=" + debutId);

        // Get all the periodeAnneeList where debut equals to (debutId + 1)
        defaultPeriodeAnneeShouldNotBeFound("debutId.equals=" + (debutId + 1));
    }

    @Test
    @Transactional
    void getAllPeriodeAnneesByFinIsEqualToSomething() throws Exception {
        // Initialize the database
        periodeAnneeRepository.saveAndFlush(periodeAnnee);
        Mois fin;
        if (TestUtil.findAll(em, Mois.class).isEmpty()) {
            fin = MoisResourceIT.createEntity(em);
            em.persist(fin);
            em.flush();
        } else {
            fin = TestUtil.findAll(em, Mois.class).get(0);
        }
        em.persist(fin);
        em.flush();
        periodeAnnee.setFin(fin);
        periodeAnneeRepository.saveAndFlush(periodeAnnee);
        Long finId = fin.getId();

        // Get all the periodeAnneeList where fin equals to finId
        defaultPeriodeAnneeShouldBeFound("finId.equals=" + finId);

        // Get all the periodeAnneeList where fin equals to (finId + 1)
        defaultPeriodeAnneeShouldNotBeFound("finId.equals=" + (finId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPeriodeAnneeShouldBeFound(String filter) throws Exception {
        restPeriodeAnneeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(periodeAnnee.getId().intValue())));

        // Check, that the count call also returns 1
        restPeriodeAnneeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPeriodeAnneeShouldNotBeFound(String filter) throws Exception {
        restPeriodeAnneeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPeriodeAnneeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPeriodeAnnee() throws Exception {
        // Get the periodeAnnee
        restPeriodeAnneeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPeriodeAnnee() throws Exception {
        // Initialize the database
        periodeAnneeRepository.saveAndFlush(periodeAnnee);

        int databaseSizeBeforeUpdate = periodeAnneeRepository.findAll().size();

        // Update the periodeAnnee
        PeriodeAnnee updatedPeriodeAnnee = periodeAnneeRepository.findById(periodeAnnee.getId()).get();
        // Disconnect from session so that the updates on updatedPeriodeAnnee are not directly saved in db
        em.detach(updatedPeriodeAnnee);

        restPeriodeAnneeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPeriodeAnnee.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPeriodeAnnee))
            )
            .andExpect(status().isOk());

        // Validate the PeriodeAnnee in the database
        List<PeriodeAnnee> periodeAnneeList = periodeAnneeRepository.findAll();
        assertThat(periodeAnneeList).hasSize(databaseSizeBeforeUpdate);
        PeriodeAnnee testPeriodeAnnee = periodeAnneeList.get(periodeAnneeList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingPeriodeAnnee() throws Exception {
        int databaseSizeBeforeUpdate = periodeAnneeRepository.findAll().size();
        periodeAnnee.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPeriodeAnneeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, periodeAnnee.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(periodeAnnee))
            )
            .andExpect(status().isBadRequest());

        // Validate the PeriodeAnnee in the database
        List<PeriodeAnnee> periodeAnneeList = periodeAnneeRepository.findAll();
        assertThat(periodeAnneeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPeriodeAnnee() throws Exception {
        int databaseSizeBeforeUpdate = periodeAnneeRepository.findAll().size();
        periodeAnnee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPeriodeAnneeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(periodeAnnee))
            )
            .andExpect(status().isBadRequest());

        // Validate the PeriodeAnnee in the database
        List<PeriodeAnnee> periodeAnneeList = periodeAnneeRepository.findAll();
        assertThat(periodeAnneeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPeriodeAnnee() throws Exception {
        int databaseSizeBeforeUpdate = periodeAnneeRepository.findAll().size();
        periodeAnnee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPeriodeAnneeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(periodeAnnee)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PeriodeAnnee in the database
        List<PeriodeAnnee> periodeAnneeList = periodeAnneeRepository.findAll();
        assertThat(periodeAnneeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePeriodeAnneeWithPatch() throws Exception {
        // Initialize the database
        periodeAnneeRepository.saveAndFlush(periodeAnnee);

        int databaseSizeBeforeUpdate = periodeAnneeRepository.findAll().size();

        // Update the periodeAnnee using partial update
        PeriodeAnnee partialUpdatedPeriodeAnnee = new PeriodeAnnee();
        partialUpdatedPeriodeAnnee.setId(periodeAnnee.getId());

        restPeriodeAnneeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPeriodeAnnee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPeriodeAnnee))
            )
            .andExpect(status().isOk());

        // Validate the PeriodeAnnee in the database
        List<PeriodeAnnee> periodeAnneeList = periodeAnneeRepository.findAll();
        assertThat(periodeAnneeList).hasSize(databaseSizeBeforeUpdate);
        PeriodeAnnee testPeriodeAnnee = periodeAnneeList.get(periodeAnneeList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdatePeriodeAnneeWithPatch() throws Exception {
        // Initialize the database
        periodeAnneeRepository.saveAndFlush(periodeAnnee);

        int databaseSizeBeforeUpdate = periodeAnneeRepository.findAll().size();

        // Update the periodeAnnee using partial update
        PeriodeAnnee partialUpdatedPeriodeAnnee = new PeriodeAnnee();
        partialUpdatedPeriodeAnnee.setId(periodeAnnee.getId());

        restPeriodeAnneeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPeriodeAnnee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPeriodeAnnee))
            )
            .andExpect(status().isOk());

        // Validate the PeriodeAnnee in the database
        List<PeriodeAnnee> periodeAnneeList = periodeAnneeRepository.findAll();
        assertThat(periodeAnneeList).hasSize(databaseSizeBeforeUpdate);
        PeriodeAnnee testPeriodeAnnee = periodeAnneeList.get(periodeAnneeList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingPeriodeAnnee() throws Exception {
        int databaseSizeBeforeUpdate = periodeAnneeRepository.findAll().size();
        periodeAnnee.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPeriodeAnneeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, periodeAnnee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(periodeAnnee))
            )
            .andExpect(status().isBadRequest());

        // Validate the PeriodeAnnee in the database
        List<PeriodeAnnee> periodeAnneeList = periodeAnneeRepository.findAll();
        assertThat(periodeAnneeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPeriodeAnnee() throws Exception {
        int databaseSizeBeforeUpdate = periodeAnneeRepository.findAll().size();
        periodeAnnee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPeriodeAnneeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(periodeAnnee))
            )
            .andExpect(status().isBadRequest());

        // Validate the PeriodeAnnee in the database
        List<PeriodeAnnee> periodeAnneeList = periodeAnneeRepository.findAll();
        assertThat(periodeAnneeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPeriodeAnnee() throws Exception {
        int databaseSizeBeforeUpdate = periodeAnneeRepository.findAll().size();
        periodeAnnee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPeriodeAnneeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(periodeAnnee))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PeriodeAnnee in the database
        List<PeriodeAnnee> periodeAnneeList = periodeAnneeRepository.findAll();
        assertThat(periodeAnneeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePeriodeAnnee() throws Exception {
        // Initialize the database
        periodeAnneeRepository.saveAndFlush(periodeAnnee);

        int databaseSizeBeforeDelete = periodeAnneeRepository.findAll().size();

        // Delete the periodeAnnee
        restPeriodeAnneeMockMvc
            .perform(delete(ENTITY_API_URL_ID, periodeAnnee.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PeriodeAnnee> periodeAnneeList = periodeAnneeRepository.findAll();
        assertThat(periodeAnneeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
