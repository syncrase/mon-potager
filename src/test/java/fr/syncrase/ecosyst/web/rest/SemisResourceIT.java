package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.Germination;
import fr.syncrase.ecosyst.domain.PeriodeAnnee;
import fr.syncrase.ecosyst.domain.Semis;
import fr.syncrase.ecosyst.domain.TypeSemis;
import fr.syncrase.ecosyst.repository.SemisRepository;
import fr.syncrase.ecosyst.service.SemisService;
import fr.syncrase.ecosyst.service.criteria.SemisCriteria;
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
 * Integration tests for the {@link SemisResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SemisResourceIT {

    private static final String ENTITY_API_URL = "/api/semis";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SemisRepository semisRepository;

    @Mock
    private SemisRepository semisRepositoryMock;

    @Mock
    private SemisService semisServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSemisMockMvc;

    private Semis semis;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Semis createEntity(EntityManager em) {
        Semis semis = new Semis();
        return semis;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Semis createUpdatedEntity(EntityManager em) {
        Semis semis = new Semis();
        return semis;
    }

    @BeforeEach
    public void initTest() {
        semis = createEntity(em);
    }

    @Test
    @Transactional
    void createSemis() throws Exception {
        int databaseSizeBeforeCreate = semisRepository.findAll().size();
        // Create the Semis
        restSemisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(semis)))
            .andExpect(status().isCreated());

        // Validate the Semis in the database
        List<Semis> semisList = semisRepository.findAll();
        assertThat(semisList).hasSize(databaseSizeBeforeCreate + 1);
        Semis testSemis = semisList.get(semisList.size() - 1);
    }

    @Test
    @Transactional
    void createSemisWithExistingId() throws Exception {
        // Create the Semis with an existing ID
        semis.setId(1L);

        int databaseSizeBeforeCreate = semisRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSemisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(semis)))
            .andExpect(status().isBadRequest());

        // Validate the Semis in the database
        List<Semis> semisList = semisRepository.findAll();
        assertThat(semisList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSemis() throws Exception {
        // Initialize the database
        semisRepository.saveAndFlush(semis);

        // Get all the semisList
        restSemisMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(semis.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSemisWithEagerRelationshipsIsEnabled() throws Exception {
        when(semisServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSemisMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(semisServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSemisWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(semisServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSemisMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(semisServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getSemis() throws Exception {
        // Initialize the database
        semisRepository.saveAndFlush(semis);

        // Get the semis
        restSemisMockMvc
            .perform(get(ENTITY_API_URL_ID, semis.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(semis.getId().intValue()));
    }

    @Test
    @Transactional
    void getSemisByIdFiltering() throws Exception {
        // Initialize the database
        semisRepository.saveAndFlush(semis);

        Long id = semis.getId();

        defaultSemisShouldBeFound("id.equals=" + id);
        defaultSemisShouldNotBeFound("id.notEquals=" + id);

        defaultSemisShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSemisShouldNotBeFound("id.greaterThan=" + id);

        defaultSemisShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSemisShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSemisBySemisPleineTerreIsEqualToSomething() throws Exception {
        // Initialize the database
        semisRepository.saveAndFlush(semis);
        PeriodeAnnee semisPleineTerre;
        if (TestUtil.findAll(em, PeriodeAnnee.class).isEmpty()) {
            semisPleineTerre = PeriodeAnneeResourceIT.createEntity(em);
            em.persist(semisPleineTerre);
            em.flush();
        } else {
            semisPleineTerre = TestUtil.findAll(em, PeriodeAnnee.class).get(0);
        }
        em.persist(semisPleineTerre);
        em.flush();
        semis.setSemisPleineTerre(semisPleineTerre);
        semisRepository.saveAndFlush(semis);
        Long semisPleineTerreId = semisPleineTerre.getId();

        // Get all the semisList where semisPleineTerre equals to semisPleineTerreId
        defaultSemisShouldBeFound("semisPleineTerreId.equals=" + semisPleineTerreId);

        // Get all the semisList where semisPleineTerre equals to (semisPleineTerreId + 1)
        defaultSemisShouldNotBeFound("semisPleineTerreId.equals=" + (semisPleineTerreId + 1));
    }

    @Test
    @Transactional
    void getAllSemisBySemisSousAbrisIsEqualToSomething() throws Exception {
        // Initialize the database
        semisRepository.saveAndFlush(semis);
        PeriodeAnnee semisSousAbris;
        if (TestUtil.findAll(em, PeriodeAnnee.class).isEmpty()) {
            semisSousAbris = PeriodeAnneeResourceIT.createEntity(em);
            em.persist(semisSousAbris);
            em.flush();
        } else {
            semisSousAbris = TestUtil.findAll(em, PeriodeAnnee.class).get(0);
        }
        em.persist(semisSousAbris);
        em.flush();
        semis.setSemisSousAbris(semisSousAbris);
        semisRepository.saveAndFlush(semis);
        Long semisSousAbrisId = semisSousAbris.getId();

        // Get all the semisList where semisSousAbris equals to semisSousAbrisId
        defaultSemisShouldBeFound("semisSousAbrisId.equals=" + semisSousAbrisId);

        // Get all the semisList where semisSousAbris equals to (semisSousAbrisId + 1)
        defaultSemisShouldNotBeFound("semisSousAbrisId.equals=" + (semisSousAbrisId + 1));
    }

    @Test
    @Transactional
    void getAllSemisByTypeSemisIsEqualToSomething() throws Exception {
        // Initialize the database
        semisRepository.saveAndFlush(semis);
        TypeSemis typeSemis;
        if (TestUtil.findAll(em, TypeSemis.class).isEmpty()) {
            typeSemis = TypeSemisResourceIT.createEntity(em);
            em.persist(typeSemis);
            em.flush();
        } else {
            typeSemis = TestUtil.findAll(em, TypeSemis.class).get(0);
        }
        em.persist(typeSemis);
        em.flush();
        semis.setTypeSemis(typeSemis);
        semisRepository.saveAndFlush(semis);
        Long typeSemisId = typeSemis.getId();

        // Get all the semisList where typeSemis equals to typeSemisId
        defaultSemisShouldBeFound("typeSemisId.equals=" + typeSemisId);

        // Get all the semisList where typeSemis equals to (typeSemisId + 1)
        defaultSemisShouldNotBeFound("typeSemisId.equals=" + (typeSemisId + 1));
    }

    @Test
    @Transactional
    void getAllSemisByGerminationIsEqualToSomething() throws Exception {
        // Initialize the database
        semisRepository.saveAndFlush(semis);
        Germination germination;
        if (TestUtil.findAll(em, Germination.class).isEmpty()) {
            germination = GerminationResourceIT.createEntity(em);
            em.persist(germination);
            em.flush();
        } else {
            germination = TestUtil.findAll(em, Germination.class).get(0);
        }
        em.persist(germination);
        em.flush();
        semis.setGermination(germination);
        semisRepository.saveAndFlush(semis);
        Long germinationId = germination.getId();

        // Get all the semisList where germination equals to germinationId
        defaultSemisShouldBeFound("germinationId.equals=" + germinationId);

        // Get all the semisList where germination equals to (germinationId + 1)
        defaultSemisShouldNotBeFound("germinationId.equals=" + (germinationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSemisShouldBeFound(String filter) throws Exception {
        restSemisMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(semis.getId().intValue())));

        // Check, that the count call also returns 1
        restSemisMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSemisShouldNotBeFound(String filter) throws Exception {
        restSemisMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSemisMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSemis() throws Exception {
        // Get the semis
        restSemisMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSemis() throws Exception {
        // Initialize the database
        semisRepository.saveAndFlush(semis);

        int databaseSizeBeforeUpdate = semisRepository.findAll().size();

        // Update the semis
        Semis updatedSemis = semisRepository.findById(semis.getId()).get();
        // Disconnect from session so that the updates on updatedSemis are not directly saved in db
        em.detach(updatedSemis);

        restSemisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSemis.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSemis))
            )
            .andExpect(status().isOk());

        // Validate the Semis in the database
        List<Semis> semisList = semisRepository.findAll();
        assertThat(semisList).hasSize(databaseSizeBeforeUpdate);
        Semis testSemis = semisList.get(semisList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingSemis() throws Exception {
        int databaseSizeBeforeUpdate = semisRepository.findAll().size();
        semis.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSemisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, semis.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(semis))
            )
            .andExpect(status().isBadRequest());

        // Validate the Semis in the database
        List<Semis> semisList = semisRepository.findAll();
        assertThat(semisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSemis() throws Exception {
        int databaseSizeBeforeUpdate = semisRepository.findAll().size();
        semis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSemisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(semis))
            )
            .andExpect(status().isBadRequest());

        // Validate the Semis in the database
        List<Semis> semisList = semisRepository.findAll();
        assertThat(semisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSemis() throws Exception {
        int databaseSizeBeforeUpdate = semisRepository.findAll().size();
        semis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSemisMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(semis)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Semis in the database
        List<Semis> semisList = semisRepository.findAll();
        assertThat(semisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSemisWithPatch() throws Exception {
        // Initialize the database
        semisRepository.saveAndFlush(semis);

        int databaseSizeBeforeUpdate = semisRepository.findAll().size();

        // Update the semis using partial update
        Semis partialUpdatedSemis = new Semis();
        partialUpdatedSemis.setId(semis.getId());

        restSemisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSemis.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSemis))
            )
            .andExpect(status().isOk());

        // Validate the Semis in the database
        List<Semis> semisList = semisRepository.findAll();
        assertThat(semisList).hasSize(databaseSizeBeforeUpdate);
        Semis testSemis = semisList.get(semisList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateSemisWithPatch() throws Exception {
        // Initialize the database
        semisRepository.saveAndFlush(semis);

        int databaseSizeBeforeUpdate = semisRepository.findAll().size();

        // Update the semis using partial update
        Semis partialUpdatedSemis = new Semis();
        partialUpdatedSemis.setId(semis.getId());

        restSemisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSemis.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSemis))
            )
            .andExpect(status().isOk());

        // Validate the Semis in the database
        List<Semis> semisList = semisRepository.findAll();
        assertThat(semisList).hasSize(databaseSizeBeforeUpdate);
        Semis testSemis = semisList.get(semisList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingSemis() throws Exception {
        int databaseSizeBeforeUpdate = semisRepository.findAll().size();
        semis.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSemisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, semis.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(semis))
            )
            .andExpect(status().isBadRequest());

        // Validate the Semis in the database
        List<Semis> semisList = semisRepository.findAll();
        assertThat(semisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSemis() throws Exception {
        int databaseSizeBeforeUpdate = semisRepository.findAll().size();
        semis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSemisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(semis))
            )
            .andExpect(status().isBadRequest());

        // Validate the Semis in the database
        List<Semis> semisList = semisRepository.findAll();
        assertThat(semisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSemis() throws Exception {
        int databaseSizeBeforeUpdate = semisRepository.findAll().size();
        semis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSemisMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(semis)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Semis in the database
        List<Semis> semisList = semisRepository.findAll();
        assertThat(semisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSemis() throws Exception {
        // Initialize the database
        semisRepository.saveAndFlush(semis);

        int databaseSizeBeforeDelete = semisRepository.findAll().size();

        // Delete the semis
        restSemisMockMvc
            .perform(delete(ENTITY_API_URL_ID, semis.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Semis> semisList = semisRepository.findAll();
        assertThat(semisList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
