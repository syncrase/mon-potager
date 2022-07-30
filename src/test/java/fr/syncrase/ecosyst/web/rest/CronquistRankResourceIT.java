package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomikRanks;
import fr.syncrase.ecosyst.repository.CronquistRankRepository;
import fr.syncrase.ecosyst.service.criteria.CronquistRankCriteria;
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
 * Integration tests for the {@link CronquistRankResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CronquistRankResourceIT {

    private static final CronquistTaxonomikRanks DEFAULT_RANK = CronquistTaxonomikRanks.DOMAINE;
    private static final CronquistTaxonomikRanks UPDATED_RANK = CronquistTaxonomikRanks.SOUSDOMAINE;

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cronquist-ranks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CronquistRankRepository cronquistRankRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCronquistRankMockMvc;

    private CronquistRank cronquistRank;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CronquistRank createEntity(EntityManager em) {
        CronquistRank cronquistRank = new CronquistRank().rank(DEFAULT_RANK).nom(DEFAULT_NOM);
        return cronquistRank;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CronquistRank createUpdatedEntity(EntityManager em) {
        CronquistRank cronquistRank = new CronquistRank().rank(UPDATED_RANK).nom(UPDATED_NOM);
        return cronquistRank;
    }

    @BeforeEach
    public void initTest() {
        cronquistRank = createEntity(em);
    }

    @Test
    @Transactional
    void createCronquistRank() throws Exception {
        int databaseSizeBeforeCreate = cronquistRankRepository.findAll().size();
        // Create the CronquistRank
        restCronquistRankMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cronquistRank)))
            .andExpect(status().isCreated());

        // Validate the CronquistRank in the database
        List<CronquistRank> cronquistRankList = cronquistRankRepository.findAll();
        assertThat(cronquistRankList).hasSize(databaseSizeBeforeCreate + 1);
        CronquistRank testCronquistRank = cronquistRankList.get(cronquistRankList.size() - 1);
        assertThat(testCronquistRank.getRank()).isEqualTo(DEFAULT_RANK);
        assertThat(testCronquistRank.getNom()).isEqualTo(DEFAULT_NOM);
    }

    @Test
    @Transactional
    void createCronquistRankWithExistingId() throws Exception {
        // Create the CronquistRank with an existing ID
        cronquistRank.setId(1L);

        int databaseSizeBeforeCreate = cronquistRankRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCronquistRankMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cronquistRank)))
            .andExpect(status().isBadRequest());

        // Validate the CronquistRank in the database
        List<CronquistRank> cronquistRankList = cronquistRankRepository.findAll();
        assertThat(cronquistRankList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRankIsRequired() throws Exception {
        int databaseSizeBeforeTest = cronquistRankRepository.findAll().size();
        // set the field null
        cronquistRank.setRank(null);

        // Create the CronquistRank, which fails.

        restCronquistRankMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cronquistRank)))
            .andExpect(status().isBadRequest());

        List<CronquistRank> cronquistRankList = cronquistRankRepository.findAll();
        assertThat(cronquistRankList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = cronquistRankRepository.findAll().size();
        // set the field null
        cronquistRank.setNom(null);

        // Create the CronquistRank, which fails.

        restCronquistRankMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cronquistRank)))
            .andExpect(status().isBadRequest());

        List<CronquistRank> cronquistRankList = cronquistRankRepository.findAll();
        assertThat(cronquistRankList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCronquistRanks() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        // Get all the cronquistRankList
        restCronquistRankMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cronquistRank.getId().intValue())))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK.toString())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)));
    }

    @Test
    @Transactional
    void getCronquistRank() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        // Get the cronquistRank
        restCronquistRankMockMvc
            .perform(get(ENTITY_API_URL_ID, cronquistRank.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cronquistRank.getId().intValue()))
            .andExpect(jsonPath("$.rank").value(DEFAULT_RANK.toString()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM));
    }

    @Test
    @Transactional
    void getCronquistRanksByIdFiltering() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        Long id = cronquistRank.getId();

        defaultCronquistRankShouldBeFound("id.equals=" + id);
        defaultCronquistRankShouldNotBeFound("id.notEquals=" + id);

        defaultCronquistRankShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCronquistRankShouldNotBeFound("id.greaterThan=" + id);

        defaultCronquistRankShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCronquistRankShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCronquistRanksByRankIsEqualToSomething() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        // Get all the cronquistRankList where rank equals to DEFAULT_RANK
        defaultCronquistRankShouldBeFound("rank.equals=" + DEFAULT_RANK);

        // Get all the cronquistRankList where rank equals to UPDATED_RANK
        defaultCronquistRankShouldNotBeFound("rank.equals=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    void getAllCronquistRanksByRankIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        // Get all the cronquistRankList where rank not equals to DEFAULT_RANK
        defaultCronquistRankShouldNotBeFound("rank.notEquals=" + DEFAULT_RANK);

        // Get all the cronquistRankList where rank not equals to UPDATED_RANK
        defaultCronquistRankShouldBeFound("rank.notEquals=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    void getAllCronquistRanksByRankIsInShouldWork() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        // Get all the cronquistRankList where rank in DEFAULT_RANK or UPDATED_RANK
        defaultCronquistRankShouldBeFound("rank.in=" + DEFAULT_RANK + "," + UPDATED_RANK);

        // Get all the cronquistRankList where rank equals to UPDATED_RANK
        defaultCronquistRankShouldNotBeFound("rank.in=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    void getAllCronquistRanksByRankIsNullOrNotNull() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        // Get all the cronquistRankList where rank is not null
        defaultCronquistRankShouldBeFound("rank.specified=true");

        // Get all the cronquistRankList where rank is null
        defaultCronquistRankShouldNotBeFound("rank.specified=false");
    }

    @Test
    @Transactional
    void getAllCronquistRanksByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        // Get all the cronquistRankList where nom equals to DEFAULT_NOM
        defaultCronquistRankShouldBeFound("nom.equals=" + DEFAULT_NOM);

        // Get all the cronquistRankList where nom equals to UPDATED_NOM
        defaultCronquistRankShouldNotBeFound("nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllCronquistRanksByNomIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        // Get all the cronquistRankList where nom not equals to DEFAULT_NOM
        defaultCronquistRankShouldNotBeFound("nom.notEquals=" + DEFAULT_NOM);

        // Get all the cronquistRankList where nom not equals to UPDATED_NOM
        defaultCronquistRankShouldBeFound("nom.notEquals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllCronquistRanksByNomIsInShouldWork() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        // Get all the cronquistRankList where nom in DEFAULT_NOM or UPDATED_NOM
        defaultCronquistRankShouldBeFound("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM);

        // Get all the cronquistRankList where nom equals to UPDATED_NOM
        defaultCronquistRankShouldNotBeFound("nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllCronquistRanksByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        // Get all the cronquistRankList where nom is not null
        defaultCronquistRankShouldBeFound("nom.specified=true");

        // Get all the cronquistRankList where nom is null
        defaultCronquistRankShouldNotBeFound("nom.specified=false");
    }

    @Test
    @Transactional
    void getAllCronquistRanksByNomContainsSomething() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        // Get all the cronquistRankList where nom contains DEFAULT_NOM
        defaultCronquistRankShouldBeFound("nom.contains=" + DEFAULT_NOM);

        // Get all the cronquistRankList where nom contains UPDATED_NOM
        defaultCronquistRankShouldNotBeFound("nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllCronquistRanksByNomNotContainsSomething() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        // Get all the cronquistRankList where nom does not contain DEFAULT_NOM
        defaultCronquistRankShouldNotBeFound("nom.doesNotContain=" + DEFAULT_NOM);

        // Get all the cronquistRankList where nom does not contain UPDATED_NOM
        defaultCronquistRankShouldBeFound("nom.doesNotContain=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllCronquistRanksByChildrenIsEqualToSomething() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);
        CronquistRank children;
        if (TestUtil.findAll(em, CronquistRank.class).isEmpty()) {
            children = CronquistRankResourceIT.createEntity(em);
            em.persist(children);
            em.flush();
        } else {
            children = TestUtil.findAll(em, CronquistRank.class).get(0);
        }
        em.persist(children);
        em.flush();
        cronquistRank.addChildren(children);
        cronquistRankRepository.saveAndFlush(cronquistRank);
        Long childrenId = children.getId();

        // Get all the cronquistRankList where children equals to childrenId
        defaultCronquistRankShouldBeFound("childrenId.equals=" + childrenId);

        // Get all the cronquistRankList where children equals to (childrenId + 1)
        defaultCronquistRankShouldNotBeFound("childrenId.equals=" + (childrenId + 1));
    }

    @Test
    @Transactional
    void getAllCronquistRanksByParentIsEqualToSomething() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);
        CronquistRank parent;
        if (TestUtil.findAll(em, CronquistRank.class).isEmpty()) {
            parent = CronquistRankResourceIT.createEntity(em);
            em.persist(parent);
            em.flush();
        } else {
            parent = TestUtil.findAll(em, CronquistRank.class).get(0);
        }
        em.persist(parent);
        em.flush();
        cronquistRank.setParent(parent);
        cronquistRankRepository.saveAndFlush(cronquistRank);
        Long parentId = parent.getId();

        // Get all the cronquistRankList where parent equals to parentId
        defaultCronquistRankShouldBeFound("parentId.equals=" + parentId);

        // Get all the cronquistRankList where parent equals to (parentId + 1)
        defaultCronquistRankShouldNotBeFound("parentId.equals=" + (parentId + 1));
    }

    @Test
    @Transactional
    void getAllCronquistRanksByPlanteIsEqualToSomething() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);
        Plante plante;
        if (TestUtil.findAll(em, Plante.class).isEmpty()) {
            plante = PlanteResourceIT.createEntity(em);
            em.persist(plante);
            em.flush();
        } else {
            plante = TestUtil.findAll(em, Plante.class).get(0);
        }
        em.persist(plante);
        em.flush();
        cronquistRank.setPlante(plante);
        cronquistRankRepository.saveAndFlush(cronquistRank);
        Long planteId = plante.getId();

        // Get all the cronquistRankList where plante equals to planteId
        defaultCronquistRankShouldBeFound("planteId.equals=" + planteId);

        // Get all the cronquistRankList where plante equals to (planteId + 1)
        defaultCronquistRankShouldNotBeFound("planteId.equals=" + (planteId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCronquistRankShouldBeFound(String filter) throws Exception {
        restCronquistRankMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cronquistRank.getId().intValue())))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK.toString())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)));

        // Check, that the count call also returns 1
        restCronquistRankMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCronquistRankShouldNotBeFound(String filter) throws Exception {
        restCronquistRankMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCronquistRankMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCronquistRank() throws Exception {
        // Get the cronquistRank
        restCronquistRankMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCronquistRank() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        int databaseSizeBeforeUpdate = cronquistRankRepository.findAll().size();

        // Update the cronquistRank
        CronquistRank updatedCronquistRank = cronquistRankRepository.findById(cronquistRank.getId()).get();
        // Disconnect from session so that the updates on updatedCronquistRank are not directly saved in db
        em.detach(updatedCronquistRank);
        updatedCronquistRank.rank(UPDATED_RANK).nom(UPDATED_NOM);

        restCronquistRankMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCronquistRank.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCronquistRank))
            )
            .andExpect(status().isOk());

        // Validate the CronquistRank in the database
        List<CronquistRank> cronquistRankList = cronquistRankRepository.findAll();
        assertThat(cronquistRankList).hasSize(databaseSizeBeforeUpdate);
        CronquistRank testCronquistRank = cronquistRankList.get(cronquistRankList.size() - 1);
        assertThat(testCronquistRank.getRank()).isEqualTo(UPDATED_RANK);
        assertThat(testCronquistRank.getNom()).isEqualTo(UPDATED_NOM);
    }

    @Test
    @Transactional
    void putNonExistingCronquistRank() throws Exception {
        int databaseSizeBeforeUpdate = cronquistRankRepository.findAll().size();
        cronquistRank.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCronquistRankMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cronquistRank.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cronquistRank))
            )
            .andExpect(status().isBadRequest());

        // Validate the CronquistRank in the database
        List<CronquistRank> cronquistRankList = cronquistRankRepository.findAll();
        assertThat(cronquistRankList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCronquistRank() throws Exception {
        int databaseSizeBeforeUpdate = cronquistRankRepository.findAll().size();
        cronquistRank.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCronquistRankMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cronquistRank))
            )
            .andExpect(status().isBadRequest());

        // Validate the CronquistRank in the database
        List<CronquistRank> cronquistRankList = cronquistRankRepository.findAll();
        assertThat(cronquistRankList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCronquistRank() throws Exception {
        int databaseSizeBeforeUpdate = cronquistRankRepository.findAll().size();
        cronquistRank.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCronquistRankMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cronquistRank)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CronquistRank in the database
        List<CronquistRank> cronquistRankList = cronquistRankRepository.findAll();
        assertThat(cronquistRankList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCronquistRankWithPatch() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        int databaseSizeBeforeUpdate = cronquistRankRepository.findAll().size();

        // Update the cronquistRank using partial update
        CronquistRank partialUpdatedCronquistRank = new CronquistRank();
        partialUpdatedCronquistRank.setId(cronquistRank.getId());

        restCronquistRankMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCronquistRank.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCronquistRank))
            )
            .andExpect(status().isOk());

        // Validate the CronquistRank in the database
        List<CronquistRank> cronquistRankList = cronquistRankRepository.findAll();
        assertThat(cronquistRankList).hasSize(databaseSizeBeforeUpdate);
        CronquistRank testCronquistRank = cronquistRankList.get(cronquistRankList.size() - 1);
        assertThat(testCronquistRank.getRank()).isEqualTo(DEFAULT_RANK);
        assertThat(testCronquistRank.getNom()).isEqualTo(DEFAULT_NOM);
    }

    @Test
    @Transactional
    void fullUpdateCronquistRankWithPatch() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        int databaseSizeBeforeUpdate = cronquistRankRepository.findAll().size();

        // Update the cronquistRank using partial update
        CronquistRank partialUpdatedCronquistRank = new CronquistRank();
        partialUpdatedCronquistRank.setId(cronquistRank.getId());

        partialUpdatedCronquistRank.rank(UPDATED_RANK).nom(UPDATED_NOM);

        restCronquistRankMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCronquistRank.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCronquistRank))
            )
            .andExpect(status().isOk());

        // Validate the CronquistRank in the database
        List<CronquistRank> cronquistRankList = cronquistRankRepository.findAll();
        assertThat(cronquistRankList).hasSize(databaseSizeBeforeUpdate);
        CronquistRank testCronquistRank = cronquistRankList.get(cronquistRankList.size() - 1);
        assertThat(testCronquistRank.getRank()).isEqualTo(UPDATED_RANK);
        assertThat(testCronquistRank.getNom()).isEqualTo(UPDATED_NOM);
    }

    @Test
    @Transactional
    void patchNonExistingCronquistRank() throws Exception {
        int databaseSizeBeforeUpdate = cronquistRankRepository.findAll().size();
        cronquistRank.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCronquistRankMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cronquistRank.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cronquistRank))
            )
            .andExpect(status().isBadRequest());

        // Validate the CronquistRank in the database
        List<CronquistRank> cronquistRankList = cronquistRankRepository.findAll();
        assertThat(cronquistRankList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCronquistRank() throws Exception {
        int databaseSizeBeforeUpdate = cronquistRankRepository.findAll().size();
        cronquistRank.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCronquistRankMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cronquistRank))
            )
            .andExpect(status().isBadRequest());

        // Validate the CronquistRank in the database
        List<CronquistRank> cronquistRankList = cronquistRankRepository.findAll();
        assertThat(cronquistRankList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCronquistRank() throws Exception {
        int databaseSizeBeforeUpdate = cronquistRankRepository.findAll().size();
        cronquistRank.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCronquistRankMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(cronquistRank))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CronquistRank in the database
        List<CronquistRank> cronquistRankList = cronquistRankRepository.findAll();
        assertThat(cronquistRankList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCronquistRank() throws Exception {
        // Initialize the database
        cronquistRankRepository.saveAndFlush(cronquistRank);

        int databaseSizeBeforeDelete = cronquistRankRepository.findAll().size();

        // Delete the cronquistRank
        restCronquistRankMockMvc
            .perform(delete(ENTITY_API_URL_ID, cronquistRank.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CronquistRank> cronquistRankList = cronquistRankRepository.findAll();
        assertThat(cronquistRankList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
