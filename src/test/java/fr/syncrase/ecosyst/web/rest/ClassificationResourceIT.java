package fr.syncrase.ecosyst.web.rest;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.*;
import fr.syncrase.ecosyst.repository.ClassificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ClassificationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClassificationResourceIT {

    private static final String ENTITY_API_URL = "/api/classifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClassificationMockMvc;

    private Classification classification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Classification createEntity(EntityManager em) {
        Classification classification = new Classification();
        return classification;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Classification createUpdatedEntity(EntityManager em) {
        Classification classification = new Classification();
        return classification;
    }

    @BeforeEach
    public void initTest() {
        classification = createEntity(em);
    }

    @Test
    @Transactional
    void createClassification() throws Exception {
        int databaseSizeBeforeCreate = classificationRepository.findAll().size();
        // Create the Classification
        restClassificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(classification))
            )
            .andExpect(status().isCreated());

        // Validate the Classification in the database
        List<Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeCreate + 1);
        Classification testClassification = classificationList.get(classificationList.size() - 1);
    }

    @Test
    @Transactional
    void createClassificationWithExistingId() throws Exception {
        // Create the Classification with an existing ID
        classification.setId(1L);

        int databaseSizeBeforeCreate = classificationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClassificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(classification))
            )
            .andExpect(status().isBadRequest());

        // Validate the Classification in the database
        List<Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClassifications() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);

        // Get all the classificationList
        restClassificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classification.getId().intValue())));
    }

    @Test
    @Transactional
    void getClassification() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);

        // Get the classification
        restClassificationMockMvc
            .perform(get(ENTITY_API_URL_ID, classification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(classification.getId().intValue()));
    }

    @Test
    @Transactional
    void getClassificationsByIdFiltering() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);

        Long id = classification.getId();

        defaultClassificationShouldBeFound("id.equals=" + id);
        defaultClassificationShouldNotBeFound("id.notEquals=" + id);

        defaultClassificationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultClassificationShouldNotBeFound("id.greaterThan=" + id);

        defaultClassificationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultClassificationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClassificationsByCronquistIsEqualToSomething() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);
        CronquistRank cronquist;
        if (TestUtil.findAll(em, CronquistRank.class).isEmpty()) {
            cronquist = CronquistRankResourceIT.createEntity(em);
            em.persist(cronquist);
            em.flush();
        } else {
            cronquist = TestUtil.findAll(em, CronquistRank.class).get(0);
        }
        em.persist(cronquist);
        em.flush();
        classification.setCronquist(cronquist);
        cronquist.setClassification(classification);
        classificationRepository.saveAndFlush(classification);
        Long cronquistId = cronquist.getId();

        // Get all the classificationList where cronquist equals to cronquistId
        defaultClassificationShouldBeFound("cronquistId.equals=" + cronquistId);

        // Get all the classificationList where cronquist equals to (cronquistId + 1)
        defaultClassificationShouldNotBeFound("cronquistId.equals=" + (cronquistId + 1));
    }

    @Test
    @Transactional
    void getAllClassificationsByApgIsEqualToSomething() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);
        APG apg;
        if (TestUtil.findAll(em, APG.class).isEmpty()) {
            apg = APGResourceIT.createEntity(em);
            em.persist(apg);
            em.flush();
        } else {
            apg = TestUtil.findAll(em, APG.class).get(0);
        }
        em.persist(apg);
        em.flush();
        classification.setApg(apg);
        apg.setClassification(classification);
        classificationRepository.saveAndFlush(classification);
        Long apgId = apg.getId();

        // Get all the classificationList where apg equals to apgId
        defaultClassificationShouldBeFound("apgId.equals=" + apgId);

        // Get all the classificationList where apg equals to (apgId + 1)
        defaultClassificationShouldNotBeFound("apgId.equals=" + (apgId + 1));
    }

    @Test
    @Transactional
    void getAllClassificationsByBenthamHookerIsEqualToSomething() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);
        BenthamHooker benthamHooker;
        if (TestUtil.findAll(em, BenthamHooker.class).isEmpty()) {
            benthamHooker = BenthamHookerResourceIT.createEntity(em);
            em.persist(benthamHooker);
            em.flush();
        } else {
            benthamHooker = TestUtil.findAll(em, BenthamHooker.class).get(0);
        }
        em.persist(benthamHooker);
        em.flush();
        classification.setBenthamHooker(benthamHooker);
        benthamHooker.setClassification(classification);
        classificationRepository.saveAndFlush(classification);
        Long benthamHookerId = benthamHooker.getId();

        // Get all the classificationList where benthamHooker equals to benthamHookerId
        defaultClassificationShouldBeFound("benthamHookerId.equals=" + benthamHookerId);

        // Get all the classificationList where benthamHooker equals to (benthamHookerId + 1)
        defaultClassificationShouldNotBeFound("benthamHookerId.equals=" + (benthamHookerId + 1));
    }

    @Test
    @Transactional
    void getAllClassificationsByWettsteinIsEqualToSomething() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);
        Wettstein wettstein;
        if (TestUtil.findAll(em, Wettstein.class).isEmpty()) {
            wettstein = WettsteinResourceIT.createEntity(em);
            em.persist(wettstein);
            em.flush();
        } else {
            wettstein = TestUtil.findAll(em, Wettstein.class).get(0);
        }
        em.persist(wettstein);
        em.flush();
        classification.setWettstein(wettstein);
        wettstein.setClassification(classification);
        classificationRepository.saveAndFlush(classification);
        Long wettsteinId = wettstein.getId();

        // Get all the classificationList where wettstein equals to wettsteinId
        defaultClassificationShouldBeFound("wettsteinId.equals=" + wettsteinId);

        // Get all the classificationList where wettstein equals to (wettsteinId + 1)
        defaultClassificationShouldNotBeFound("wettsteinId.equals=" + (wettsteinId + 1));
    }

    @Test
    @Transactional
    void getAllClassificationsByThorneIsEqualToSomething() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);
        Thorne thorne;
        if (TestUtil.findAll(em, Thorne.class).isEmpty()) {
            thorne = ThorneResourceIT.createEntity(em);
            em.persist(thorne);
            em.flush();
        } else {
            thorne = TestUtil.findAll(em, Thorne.class).get(0);
        }
        em.persist(thorne);
        em.flush();
        classification.setThorne(thorne);
        thorne.setClassification(classification);
        classificationRepository.saveAndFlush(classification);
        Long thorneId = thorne.getId();

        // Get all the classificationList where thorne equals to thorneId
        defaultClassificationShouldBeFound("thorneId.equals=" + thorneId);

        // Get all the classificationList where thorne equals to (thorneId + 1)
        defaultClassificationShouldNotBeFound("thorneId.equals=" + (thorneId + 1));
    }

    @Test
    @Transactional
    void getAllClassificationsByTakhtajanIsEqualToSomething() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);
        Takhtajan takhtajan;
        if (TestUtil.findAll(em, Takhtajan.class).isEmpty()) {
            takhtajan = TakhtajanResourceIT.createEntity(em);
            em.persist(takhtajan);
            em.flush();
        } else {
            takhtajan = TestUtil.findAll(em, Takhtajan.class).get(0);
        }
        em.persist(takhtajan);
        em.flush();
        classification.setTakhtajan(takhtajan);
        takhtajan.setClassification(classification);
        classificationRepository.saveAndFlush(classification);
        Long takhtajanId = takhtajan.getId();

        // Get all the classificationList where takhtajan equals to takhtajanId
        defaultClassificationShouldBeFound("takhtajanId.equals=" + takhtajanId);

        // Get all the classificationList where takhtajan equals to (takhtajanId + 1)
        defaultClassificationShouldNotBeFound("takhtajanId.equals=" + (takhtajanId + 1));
    }

    @Test
    @Transactional
    void getAllClassificationsByEnglerIsEqualToSomething() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);
        Engler engler;
        if (TestUtil.findAll(em, Engler.class).isEmpty()) {
            engler = EnglerResourceIT.createEntity(em);
            em.persist(engler);
            em.flush();
        } else {
            engler = TestUtil.findAll(em, Engler.class).get(0);
        }
        em.persist(engler);
        em.flush();
        classification.setEngler(engler);
        engler.setClassification(classification);
        classificationRepository.saveAndFlush(classification);
        Long englerId = engler.getId();

        // Get all the classificationList where engler equals to englerId
        defaultClassificationShouldBeFound("englerId.equals=" + englerId);

        // Get all the classificationList where engler equals to (englerId + 1)
        defaultClassificationShouldNotBeFound("englerId.equals=" + (englerId + 1));
    }

    @Test
    @Transactional
    void getAllClassificationsByCandolleIsEqualToSomething() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);
        Candolle candolle;
        if (TestUtil.findAll(em, Candolle.class).isEmpty()) {
            candolle = CandolleResourceIT.createEntity(em);
            em.persist(candolle);
            em.flush();
        } else {
            candolle = TestUtil.findAll(em, Candolle.class).get(0);
        }
        em.persist(candolle);
        em.flush();
        classification.setCandolle(candolle);
        candolle.setClassification(classification);
        classificationRepository.saveAndFlush(classification);
        Long candolleId = candolle.getId();

        // Get all the classificationList where candolle equals to candolleId
        defaultClassificationShouldBeFound("candolleId.equals=" + candolleId);

        // Get all the classificationList where candolle equals to (candolleId + 1)
        defaultClassificationShouldNotBeFound("candolleId.equals=" + (candolleId + 1));
    }

    @Test
    @Transactional
    void getAllClassificationsByDahlgrenIsEqualToSomething() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);
        Dahlgren dahlgren;
        if (TestUtil.findAll(em, Dahlgren.class).isEmpty()) {
            dahlgren = DahlgrenResourceIT.createEntity(em);
            em.persist(dahlgren);
            em.flush();
        } else {
            dahlgren = TestUtil.findAll(em, Dahlgren.class).get(0);
        }
        em.persist(dahlgren);
        em.flush();
        classification.setDahlgren(dahlgren);
        dahlgren.setClassification(classification);
        classificationRepository.saveAndFlush(classification);
        Long dahlgrenId = dahlgren.getId();

        // Get all the classificationList where dahlgren equals to dahlgrenId
        defaultClassificationShouldBeFound("dahlgrenId.equals=" + dahlgrenId);

        // Get all the classificationList where dahlgren equals to (dahlgrenId + 1)
        defaultClassificationShouldNotBeFound("dahlgrenId.equals=" + (dahlgrenId + 1));
    }

    @Test
    @Transactional
    void getAllClassificationsByPlantesIsEqualToSomething() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);
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
        classification.addPlantes(plantes);
        classificationRepository.saveAndFlush(classification);
        Long plantesId = plantes.getId();

        // Get all the classificationList where plantes equals to plantesId
        defaultClassificationShouldBeFound("plantesId.equals=" + plantesId);

        // Get all the classificationList where plantes equals to (plantesId + 1)
        defaultClassificationShouldNotBeFound("plantesId.equals=" + (plantesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClassificationShouldBeFound(String filter) throws Exception {
        restClassificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classification.getId().intValue())));

        // Check, that the count call also returns 1
        restClassificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClassificationShouldNotBeFound(String filter) throws Exception {
        restClassificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClassificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingClassification() throws Exception {
        // Get the classification
        restClassificationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewClassification() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);

        int databaseSizeBeforeUpdate = classificationRepository.findAll().size();

        // Update the classification
        Classification updatedClassification = classificationRepository.findById(classification.getId()).get();
        // Disconnect from session so that the updates on updatedClassification are not directly saved in db
        em.detach(updatedClassification);

        restClassificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClassification.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedClassification))
            )
            .andExpect(status().isOk());

        // Validate the Classification in the database
        List<Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeUpdate);
        Classification testClassification = classificationList.get(classificationList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingClassification() throws Exception {
        int databaseSizeBeforeUpdate = classificationRepository.findAll().size();
        classification.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClassificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, classification.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(classification))
            )
            .andExpect(status().isBadRequest());

        // Validate the Classification in the database
        List<Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClassification() throws Exception {
        int databaseSizeBeforeUpdate = classificationRepository.findAll().size();
        classification.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(classification))
            )
            .andExpect(status().isBadRequest());

        // Validate the Classification in the database
        List<Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClassification() throws Exception {
        int databaseSizeBeforeUpdate = classificationRepository.findAll().size();
        classification.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassificationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(classification)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Classification in the database
        List<Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClassificationWithPatch() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);

        int databaseSizeBeforeUpdate = classificationRepository.findAll().size();

        // Update the classification using partial update
        Classification partialUpdatedClassification = new Classification();
        partialUpdatedClassification.setId(classification.getId());

        restClassificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClassification.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClassification))
            )
            .andExpect(status().isOk());

        // Validate the Classification in the database
        List<Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeUpdate);
        Classification testClassification = classificationList.get(classificationList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateClassificationWithPatch() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);

        int databaseSizeBeforeUpdate = classificationRepository.findAll().size();

        // Update the classification using partial update
        Classification partialUpdatedClassification = new Classification();
        partialUpdatedClassification.setId(classification.getId());

        restClassificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClassification.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClassification))
            )
            .andExpect(status().isOk());

        // Validate the Classification in the database
        List<Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeUpdate);
        Classification testClassification = classificationList.get(classificationList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingClassification() throws Exception {
        int databaseSizeBeforeUpdate = classificationRepository.findAll().size();
        classification.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClassificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, classification.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(classification))
            )
            .andExpect(status().isBadRequest());

        // Validate the Classification in the database
        List<Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClassification() throws Exception {
        int databaseSizeBeforeUpdate = classificationRepository.findAll().size();
        classification.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(classification))
            )
            .andExpect(status().isBadRequest());

        // Validate the Classification in the database
        List<Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClassification() throws Exception {
        int databaseSizeBeforeUpdate = classificationRepository.findAll().size();
        classification.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassificationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(classification))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Classification in the database
        List<Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClassification() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);

        int databaseSizeBeforeDelete = classificationRepository.findAll().size();

        // Delete the classification
        restClassificationMockMvc
            .perform(delete(ENTITY_API_URL_ID, classification.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
