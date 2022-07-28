package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.CycleDeVie;
import fr.syncrase.ecosyst.domain.PeriodeAnnee;
import fr.syncrase.ecosyst.domain.Reproduction;
import fr.syncrase.ecosyst.domain.Semis;
import fr.syncrase.ecosyst.repository.CycleDeVieRepository;
import fr.syncrase.ecosyst.service.criteria.CycleDeVieCriteria;
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
 * Integration tests for the {@link CycleDeVieResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CycleDeVieResourceIT {

    private static final String ENTITY_API_URL = "/api/cycle-de-vies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CycleDeVieRepository cycleDeVieRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCycleDeVieMockMvc;

    private CycleDeVie cycleDeVie;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CycleDeVie createEntity(EntityManager em) {
        CycleDeVie cycleDeVie = new CycleDeVie();
        return cycleDeVie;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CycleDeVie createUpdatedEntity(EntityManager em) {
        CycleDeVie cycleDeVie = new CycleDeVie();
        return cycleDeVie;
    }

    @BeforeEach
    public void initTest() {
        cycleDeVie = createEntity(em);
    }

    @Test
    @Transactional
    void createCycleDeVie() throws Exception {
        int databaseSizeBeforeCreate = cycleDeVieRepository.findAll().size();
        // Create the CycleDeVie
        restCycleDeVieMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cycleDeVie)))
            .andExpect(status().isCreated());

        // Validate the CycleDeVie in the database
        List<CycleDeVie> cycleDeVieList = cycleDeVieRepository.findAll();
        assertThat(cycleDeVieList).hasSize(databaseSizeBeforeCreate + 1);
        CycleDeVie testCycleDeVie = cycleDeVieList.get(cycleDeVieList.size() - 1);
    }

    @Test
    @Transactional
    void createCycleDeVieWithExistingId() throws Exception {
        // Create the CycleDeVie with an existing ID
        cycleDeVie.setId(1L);

        int databaseSizeBeforeCreate = cycleDeVieRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCycleDeVieMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cycleDeVie)))
            .andExpect(status().isBadRequest());

        // Validate the CycleDeVie in the database
        List<CycleDeVie> cycleDeVieList = cycleDeVieRepository.findAll();
        assertThat(cycleDeVieList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCycleDeVies() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);

        // Get all the cycleDeVieList
        restCycleDeVieMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cycleDeVie.getId().intValue())));
    }

    @Test
    @Transactional
    void getCycleDeVie() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);

        // Get the cycleDeVie
        restCycleDeVieMockMvc
            .perform(get(ENTITY_API_URL_ID, cycleDeVie.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cycleDeVie.getId().intValue()));
    }

    @Test
    @Transactional
    void getCycleDeViesByIdFiltering() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);

        Long id = cycleDeVie.getId();

        defaultCycleDeVieShouldBeFound("id.equals=" + id);
        defaultCycleDeVieShouldNotBeFound("id.notEquals=" + id);

        defaultCycleDeVieShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCycleDeVieShouldNotBeFound("id.greaterThan=" + id);

        defaultCycleDeVieShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCycleDeVieShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCycleDeViesBySemisIsEqualToSomething() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        Semis semis;
        if (TestUtil.findAll(em, Semis.class).isEmpty()) {
            semis = SemisResourceIT.createEntity(em);
            em.persist(semis);
            em.flush();
        } else {
            semis = TestUtil.findAll(em, Semis.class).get(0);
        }
        em.persist(semis);
        em.flush();
        cycleDeVie.setSemis(semis);
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        Long semisId = semis.getId();

        // Get all the cycleDeVieList where semis equals to semisId
        defaultCycleDeVieShouldBeFound("semisId.equals=" + semisId);

        // Get all the cycleDeVieList where semis equals to (semisId + 1)
        defaultCycleDeVieShouldNotBeFound("semisId.equals=" + (semisId + 1));
    }

    @Test
    @Transactional
    void getAllCycleDeViesByApparitionFeuillesIsEqualToSomething() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        PeriodeAnnee apparitionFeuilles;
        if (TestUtil.findAll(em, PeriodeAnnee.class).isEmpty()) {
            apparitionFeuilles = PeriodeAnneeResourceIT.createEntity(em);
            em.persist(apparitionFeuilles);
            em.flush();
        } else {
            apparitionFeuilles = TestUtil.findAll(em, PeriodeAnnee.class).get(0);
        }
        em.persist(apparitionFeuilles);
        em.flush();
        cycleDeVie.setApparitionFeuilles(apparitionFeuilles);
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        Long apparitionFeuillesId = apparitionFeuilles.getId();

        // Get all the cycleDeVieList where apparitionFeuilles equals to apparitionFeuillesId
        defaultCycleDeVieShouldBeFound("apparitionFeuillesId.equals=" + apparitionFeuillesId);

        // Get all the cycleDeVieList where apparitionFeuilles equals to (apparitionFeuillesId + 1)
        defaultCycleDeVieShouldNotBeFound("apparitionFeuillesId.equals=" + (apparitionFeuillesId + 1));
    }

    @Test
    @Transactional
    void getAllCycleDeViesByFloraisonIsEqualToSomething() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        PeriodeAnnee floraison;
        if (TestUtil.findAll(em, PeriodeAnnee.class).isEmpty()) {
            floraison = PeriodeAnneeResourceIT.createEntity(em);
            em.persist(floraison);
            em.flush();
        } else {
            floraison = TestUtil.findAll(em, PeriodeAnnee.class).get(0);
        }
        em.persist(floraison);
        em.flush();
        cycleDeVie.setFloraison(floraison);
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        Long floraisonId = floraison.getId();

        // Get all the cycleDeVieList where floraison equals to floraisonId
        defaultCycleDeVieShouldBeFound("floraisonId.equals=" + floraisonId);

        // Get all the cycleDeVieList where floraison equals to (floraisonId + 1)
        defaultCycleDeVieShouldNotBeFound("floraisonId.equals=" + (floraisonId + 1));
    }

    @Test
    @Transactional
    void getAllCycleDeViesByRecolteIsEqualToSomething() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        PeriodeAnnee recolte;
        if (TestUtil.findAll(em, PeriodeAnnee.class).isEmpty()) {
            recolte = PeriodeAnneeResourceIT.createEntity(em);
            em.persist(recolte);
            em.flush();
        } else {
            recolte = TestUtil.findAll(em, PeriodeAnnee.class).get(0);
        }
        em.persist(recolte);
        em.flush();
        cycleDeVie.setRecolte(recolte);
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        Long recolteId = recolte.getId();

        // Get all the cycleDeVieList where recolte equals to recolteId
        defaultCycleDeVieShouldBeFound("recolteId.equals=" + recolteId);

        // Get all the cycleDeVieList where recolte equals to (recolteId + 1)
        defaultCycleDeVieShouldNotBeFound("recolteId.equals=" + (recolteId + 1));
    }

    @Test
    @Transactional
    void getAllCycleDeViesByCroissanceIsEqualToSomething() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        PeriodeAnnee croissance;
        if (TestUtil.findAll(em, PeriodeAnnee.class).isEmpty()) {
            croissance = PeriodeAnneeResourceIT.createEntity(em);
            em.persist(croissance);
            em.flush();
        } else {
            croissance = TestUtil.findAll(em, PeriodeAnnee.class).get(0);
        }
        em.persist(croissance);
        em.flush();
        cycleDeVie.setCroissance(croissance);
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        Long croissanceId = croissance.getId();

        // Get all the cycleDeVieList where croissance equals to croissanceId
        defaultCycleDeVieShouldBeFound("croissanceId.equals=" + croissanceId);

        // Get all the cycleDeVieList where croissance equals to (croissanceId + 1)
        defaultCycleDeVieShouldNotBeFound("croissanceId.equals=" + (croissanceId + 1));
    }

    @Test
    @Transactional
    void getAllCycleDeViesByMaturiteIsEqualToSomething() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        PeriodeAnnee maturite;
        if (TestUtil.findAll(em, PeriodeAnnee.class).isEmpty()) {
            maturite = PeriodeAnneeResourceIT.createEntity(em);
            em.persist(maturite);
            em.flush();
        } else {
            maturite = TestUtil.findAll(em, PeriodeAnnee.class).get(0);
        }
        em.persist(maturite);
        em.flush();
        cycleDeVie.setMaturite(maturite);
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        Long maturiteId = maturite.getId();

        // Get all the cycleDeVieList where maturite equals to maturiteId
        defaultCycleDeVieShouldBeFound("maturiteId.equals=" + maturiteId);

        // Get all the cycleDeVieList where maturite equals to (maturiteId + 1)
        defaultCycleDeVieShouldNotBeFound("maturiteId.equals=" + (maturiteId + 1));
    }

    @Test
    @Transactional
    void getAllCycleDeViesByPlantationIsEqualToSomething() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        PeriodeAnnee plantation;
        if (TestUtil.findAll(em, PeriodeAnnee.class).isEmpty()) {
            plantation = PeriodeAnneeResourceIT.createEntity(em);
            em.persist(plantation);
            em.flush();
        } else {
            plantation = TestUtil.findAll(em, PeriodeAnnee.class).get(0);
        }
        em.persist(plantation);
        em.flush();
        cycleDeVie.setPlantation(plantation);
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        Long plantationId = plantation.getId();

        // Get all the cycleDeVieList where plantation equals to plantationId
        defaultCycleDeVieShouldBeFound("plantationId.equals=" + plantationId);

        // Get all the cycleDeVieList where plantation equals to (plantationId + 1)
        defaultCycleDeVieShouldNotBeFound("plantationId.equals=" + (plantationId + 1));
    }

    @Test
    @Transactional
    void getAllCycleDeViesByRempotageIsEqualToSomething() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        PeriodeAnnee rempotage;
        if (TestUtil.findAll(em, PeriodeAnnee.class).isEmpty()) {
            rempotage = PeriodeAnneeResourceIT.createEntity(em);
            em.persist(rempotage);
            em.flush();
        } else {
            rempotage = TestUtil.findAll(em, PeriodeAnnee.class).get(0);
        }
        em.persist(rempotage);
        em.flush();
        cycleDeVie.setRempotage(rempotage);
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        Long rempotageId = rempotage.getId();

        // Get all the cycleDeVieList where rempotage equals to rempotageId
        defaultCycleDeVieShouldBeFound("rempotageId.equals=" + rempotageId);

        // Get all the cycleDeVieList where rempotage equals to (rempotageId + 1)
        defaultCycleDeVieShouldNotBeFound("rempotageId.equals=" + (rempotageId + 1));
    }

    @Test
    @Transactional
    void getAllCycleDeViesByReproductionIsEqualToSomething() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        Reproduction reproduction;
        if (TestUtil.findAll(em, Reproduction.class).isEmpty()) {
            reproduction = ReproductionResourceIT.createEntity(em);
            em.persist(reproduction);
            em.flush();
        } else {
            reproduction = TestUtil.findAll(em, Reproduction.class).get(0);
        }
        em.persist(reproduction);
        em.flush();
        cycleDeVie.setReproduction(reproduction);
        cycleDeVieRepository.saveAndFlush(cycleDeVie);
        Long reproductionId = reproduction.getId();

        // Get all the cycleDeVieList where reproduction equals to reproductionId
        defaultCycleDeVieShouldBeFound("reproductionId.equals=" + reproductionId);

        // Get all the cycleDeVieList where reproduction equals to (reproductionId + 1)
        defaultCycleDeVieShouldNotBeFound("reproductionId.equals=" + (reproductionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCycleDeVieShouldBeFound(String filter) throws Exception {
        restCycleDeVieMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cycleDeVie.getId().intValue())));

        // Check, that the count call also returns 1
        restCycleDeVieMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCycleDeVieShouldNotBeFound(String filter) throws Exception {
        restCycleDeVieMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCycleDeVieMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCycleDeVie() throws Exception {
        // Get the cycleDeVie
        restCycleDeVieMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCycleDeVie() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);

        int databaseSizeBeforeUpdate = cycleDeVieRepository.findAll().size();

        // Update the cycleDeVie
        CycleDeVie updatedCycleDeVie = cycleDeVieRepository.findById(cycleDeVie.getId()).get();
        // Disconnect from session so that the updates on updatedCycleDeVie are not directly saved in db
        em.detach(updatedCycleDeVie);

        restCycleDeVieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCycleDeVie.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCycleDeVie))
            )
            .andExpect(status().isOk());

        // Validate the CycleDeVie in the database
        List<CycleDeVie> cycleDeVieList = cycleDeVieRepository.findAll();
        assertThat(cycleDeVieList).hasSize(databaseSizeBeforeUpdate);
        CycleDeVie testCycleDeVie = cycleDeVieList.get(cycleDeVieList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingCycleDeVie() throws Exception {
        int databaseSizeBeforeUpdate = cycleDeVieRepository.findAll().size();
        cycleDeVie.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCycleDeVieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cycleDeVie.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cycleDeVie))
            )
            .andExpect(status().isBadRequest());

        // Validate the CycleDeVie in the database
        List<CycleDeVie> cycleDeVieList = cycleDeVieRepository.findAll();
        assertThat(cycleDeVieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCycleDeVie() throws Exception {
        int databaseSizeBeforeUpdate = cycleDeVieRepository.findAll().size();
        cycleDeVie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCycleDeVieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cycleDeVie))
            )
            .andExpect(status().isBadRequest());

        // Validate the CycleDeVie in the database
        List<CycleDeVie> cycleDeVieList = cycleDeVieRepository.findAll();
        assertThat(cycleDeVieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCycleDeVie() throws Exception {
        int databaseSizeBeforeUpdate = cycleDeVieRepository.findAll().size();
        cycleDeVie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCycleDeVieMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cycleDeVie)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CycleDeVie in the database
        List<CycleDeVie> cycleDeVieList = cycleDeVieRepository.findAll();
        assertThat(cycleDeVieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCycleDeVieWithPatch() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);

        int databaseSizeBeforeUpdate = cycleDeVieRepository.findAll().size();

        // Update the cycleDeVie using partial update
        CycleDeVie partialUpdatedCycleDeVie = new CycleDeVie();
        partialUpdatedCycleDeVie.setId(cycleDeVie.getId());

        restCycleDeVieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCycleDeVie.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCycleDeVie))
            )
            .andExpect(status().isOk());

        // Validate the CycleDeVie in the database
        List<CycleDeVie> cycleDeVieList = cycleDeVieRepository.findAll();
        assertThat(cycleDeVieList).hasSize(databaseSizeBeforeUpdate);
        CycleDeVie testCycleDeVie = cycleDeVieList.get(cycleDeVieList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateCycleDeVieWithPatch() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);

        int databaseSizeBeforeUpdate = cycleDeVieRepository.findAll().size();

        // Update the cycleDeVie using partial update
        CycleDeVie partialUpdatedCycleDeVie = new CycleDeVie();
        partialUpdatedCycleDeVie.setId(cycleDeVie.getId());

        restCycleDeVieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCycleDeVie.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCycleDeVie))
            )
            .andExpect(status().isOk());

        // Validate the CycleDeVie in the database
        List<CycleDeVie> cycleDeVieList = cycleDeVieRepository.findAll();
        assertThat(cycleDeVieList).hasSize(databaseSizeBeforeUpdate);
        CycleDeVie testCycleDeVie = cycleDeVieList.get(cycleDeVieList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingCycleDeVie() throws Exception {
        int databaseSizeBeforeUpdate = cycleDeVieRepository.findAll().size();
        cycleDeVie.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCycleDeVieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cycleDeVie.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cycleDeVie))
            )
            .andExpect(status().isBadRequest());

        // Validate the CycleDeVie in the database
        List<CycleDeVie> cycleDeVieList = cycleDeVieRepository.findAll();
        assertThat(cycleDeVieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCycleDeVie() throws Exception {
        int databaseSizeBeforeUpdate = cycleDeVieRepository.findAll().size();
        cycleDeVie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCycleDeVieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cycleDeVie))
            )
            .andExpect(status().isBadRequest());

        // Validate the CycleDeVie in the database
        List<CycleDeVie> cycleDeVieList = cycleDeVieRepository.findAll();
        assertThat(cycleDeVieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCycleDeVie() throws Exception {
        int databaseSizeBeforeUpdate = cycleDeVieRepository.findAll().size();
        cycleDeVie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCycleDeVieMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(cycleDeVie))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CycleDeVie in the database
        List<CycleDeVie> cycleDeVieList = cycleDeVieRepository.findAll();
        assertThat(cycleDeVieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCycleDeVie() throws Exception {
        // Initialize the database
        cycleDeVieRepository.saveAndFlush(cycleDeVie);

        int databaseSizeBeforeDelete = cycleDeVieRepository.findAll().size();

        // Delete the cycleDeVie
        restCycleDeVieMockMvc
            .perform(delete(ENTITY_API_URL_ID, cycleDeVie.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CycleDeVie> cycleDeVieList = cycleDeVieRepository.findAll();
        assertThat(cycleDeVieList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
