package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.CycleDeVie;
import fr.syncrase.ecosyst.domain.Ensoleillement;
import fr.syncrase.ecosyst.domain.Feuillage;
import fr.syncrase.ecosyst.domain.NomVernaculaire;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.domain.Racine;
import fr.syncrase.ecosyst.domain.Ressemblance;
import fr.syncrase.ecosyst.domain.Sol;
import fr.syncrase.ecosyst.domain.Strate;
import fr.syncrase.ecosyst.domain.Temperature;
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

    private static final String DEFAULT_ENTRETIEN = "AAAAAAAAAA";
    private static final String UPDATED_ENTRETIEN = "BBBBBBBBBB";

    private static final String DEFAULT_HISTOIRE = "AAAAAAAAAA";
    private static final String UPDATED_HISTOIRE = "BBBBBBBBBB";

    private static final String DEFAULT_VITESSE_CROISSANCE = "AAAAAAAAAA";
    private static final String UPDATED_VITESSE_CROISSANCE = "BBBBBBBBBB";

    private static final String DEFAULT_EXPOSITION = "AAAAAAAAAA";
    private static final String UPDATED_EXPOSITION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/plantes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

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
        Plante plante = new Plante()
            .entretien(DEFAULT_ENTRETIEN)
            .histoire(DEFAULT_HISTOIRE)
            .vitesseCroissance(DEFAULT_VITESSE_CROISSANCE)
            .exposition(DEFAULT_EXPOSITION);
        return plante;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plante createUpdatedEntity(EntityManager em) {
        Plante plante = new Plante()
            .entretien(UPDATED_ENTRETIEN)
            .histoire(UPDATED_HISTOIRE)
            .vitesseCroissance(UPDATED_VITESSE_CROISSANCE)
            .exposition(UPDATED_EXPOSITION);
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
        assertThat(testPlante.getEntretien()).isEqualTo(DEFAULT_ENTRETIEN);
        assertThat(testPlante.getHistoire()).isEqualTo(DEFAULT_HISTOIRE);
        assertThat(testPlante.getVitesseCroissance()).isEqualTo(DEFAULT_VITESSE_CROISSANCE);
        assertThat(testPlante.getExposition()).isEqualTo(DEFAULT_EXPOSITION);
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
            .andExpect(jsonPath("$.[*].id").value(hasItem(plante.getId().intValue())))
            .andExpect(jsonPath("$.[*].entretien").value(hasItem(DEFAULT_ENTRETIEN)))
            .andExpect(jsonPath("$.[*].histoire").value(hasItem(DEFAULT_HISTOIRE)))
            .andExpect(jsonPath("$.[*].vitesseCroissance").value(hasItem(DEFAULT_VITESSE_CROISSANCE)))
            .andExpect(jsonPath("$.[*].exposition").value(hasItem(DEFAULT_EXPOSITION)));
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
            .andExpect(jsonPath("$.id").value(plante.getId().intValue()))
            .andExpect(jsonPath("$.entretien").value(DEFAULT_ENTRETIEN))
            .andExpect(jsonPath("$.histoire").value(DEFAULT_HISTOIRE))
            .andExpect(jsonPath("$.vitesseCroissance").value(DEFAULT_VITESSE_CROISSANCE))
            .andExpect(jsonPath("$.exposition").value(DEFAULT_EXPOSITION));
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
    void getAllPlantesByEntretienIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where entretien equals to DEFAULT_ENTRETIEN
        defaultPlanteShouldBeFound("entretien.equals=" + DEFAULT_ENTRETIEN);

        // Get all the planteList where entretien equals to UPDATED_ENTRETIEN
        defaultPlanteShouldNotBeFound("entretien.equals=" + UPDATED_ENTRETIEN);
    }

    @Test
    @Transactional
    void getAllPlantesByEntretienIsNotEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where entretien not equals to DEFAULT_ENTRETIEN
        defaultPlanteShouldNotBeFound("entretien.notEquals=" + DEFAULT_ENTRETIEN);

        // Get all the planteList where entretien not equals to UPDATED_ENTRETIEN
        defaultPlanteShouldBeFound("entretien.notEquals=" + UPDATED_ENTRETIEN);
    }

    @Test
    @Transactional
    void getAllPlantesByEntretienIsInShouldWork() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where entretien in DEFAULT_ENTRETIEN or UPDATED_ENTRETIEN
        defaultPlanteShouldBeFound("entretien.in=" + DEFAULT_ENTRETIEN + "," + UPDATED_ENTRETIEN);

        // Get all the planteList where entretien equals to UPDATED_ENTRETIEN
        defaultPlanteShouldNotBeFound("entretien.in=" + UPDATED_ENTRETIEN);
    }

    @Test
    @Transactional
    void getAllPlantesByEntretienIsNullOrNotNull() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where entretien is not null
        defaultPlanteShouldBeFound("entretien.specified=true");

        // Get all the planteList where entretien is null
        defaultPlanteShouldNotBeFound("entretien.specified=false");
    }

    @Test
    @Transactional
    void getAllPlantesByEntretienContainsSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where entretien contains DEFAULT_ENTRETIEN
        defaultPlanteShouldBeFound("entretien.contains=" + DEFAULT_ENTRETIEN);

        // Get all the planteList where entretien contains UPDATED_ENTRETIEN
        defaultPlanteShouldNotBeFound("entretien.contains=" + UPDATED_ENTRETIEN);
    }

    @Test
    @Transactional
    void getAllPlantesByEntretienNotContainsSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where entretien does not contain DEFAULT_ENTRETIEN
        defaultPlanteShouldNotBeFound("entretien.doesNotContain=" + DEFAULT_ENTRETIEN);

        // Get all the planteList where entretien does not contain UPDATED_ENTRETIEN
        defaultPlanteShouldBeFound("entretien.doesNotContain=" + UPDATED_ENTRETIEN);
    }

    @Test
    @Transactional
    void getAllPlantesByHistoireIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where histoire equals to DEFAULT_HISTOIRE
        defaultPlanteShouldBeFound("histoire.equals=" + DEFAULT_HISTOIRE);

        // Get all the planteList where histoire equals to UPDATED_HISTOIRE
        defaultPlanteShouldNotBeFound("histoire.equals=" + UPDATED_HISTOIRE);
    }

    @Test
    @Transactional
    void getAllPlantesByHistoireIsNotEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where histoire not equals to DEFAULT_HISTOIRE
        defaultPlanteShouldNotBeFound("histoire.notEquals=" + DEFAULT_HISTOIRE);

        // Get all the planteList where histoire not equals to UPDATED_HISTOIRE
        defaultPlanteShouldBeFound("histoire.notEquals=" + UPDATED_HISTOIRE);
    }

    @Test
    @Transactional
    void getAllPlantesByHistoireIsInShouldWork() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where histoire in DEFAULT_HISTOIRE or UPDATED_HISTOIRE
        defaultPlanteShouldBeFound("histoire.in=" + DEFAULT_HISTOIRE + "," + UPDATED_HISTOIRE);

        // Get all the planteList where histoire equals to UPDATED_HISTOIRE
        defaultPlanteShouldNotBeFound("histoire.in=" + UPDATED_HISTOIRE);
    }

    @Test
    @Transactional
    void getAllPlantesByHistoireIsNullOrNotNull() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where histoire is not null
        defaultPlanteShouldBeFound("histoire.specified=true");

        // Get all the planteList where histoire is null
        defaultPlanteShouldNotBeFound("histoire.specified=false");
    }

    @Test
    @Transactional
    void getAllPlantesByHistoireContainsSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where histoire contains DEFAULT_HISTOIRE
        defaultPlanteShouldBeFound("histoire.contains=" + DEFAULT_HISTOIRE);

        // Get all the planteList where histoire contains UPDATED_HISTOIRE
        defaultPlanteShouldNotBeFound("histoire.contains=" + UPDATED_HISTOIRE);
    }

    @Test
    @Transactional
    void getAllPlantesByHistoireNotContainsSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where histoire does not contain DEFAULT_HISTOIRE
        defaultPlanteShouldNotBeFound("histoire.doesNotContain=" + DEFAULT_HISTOIRE);

        // Get all the planteList where histoire does not contain UPDATED_HISTOIRE
        defaultPlanteShouldBeFound("histoire.doesNotContain=" + UPDATED_HISTOIRE);
    }

    @Test
    @Transactional
    void getAllPlantesByVitesseCroissanceIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where vitesseCroissance equals to DEFAULT_VITESSE_CROISSANCE
        defaultPlanteShouldBeFound("vitesseCroissance.equals=" + DEFAULT_VITESSE_CROISSANCE);

        // Get all the planteList where vitesseCroissance equals to UPDATED_VITESSE_CROISSANCE
        defaultPlanteShouldNotBeFound("vitesseCroissance.equals=" + UPDATED_VITESSE_CROISSANCE);
    }

    @Test
    @Transactional
    void getAllPlantesByVitesseCroissanceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where vitesseCroissance not equals to DEFAULT_VITESSE_CROISSANCE
        defaultPlanteShouldNotBeFound("vitesseCroissance.notEquals=" + DEFAULT_VITESSE_CROISSANCE);

        // Get all the planteList where vitesseCroissance not equals to UPDATED_VITESSE_CROISSANCE
        defaultPlanteShouldBeFound("vitesseCroissance.notEquals=" + UPDATED_VITESSE_CROISSANCE);
    }

    @Test
    @Transactional
    void getAllPlantesByVitesseCroissanceIsInShouldWork() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where vitesseCroissance in DEFAULT_VITESSE_CROISSANCE or UPDATED_VITESSE_CROISSANCE
        defaultPlanteShouldBeFound("vitesseCroissance.in=" + DEFAULT_VITESSE_CROISSANCE + "," + UPDATED_VITESSE_CROISSANCE);

        // Get all the planteList where vitesseCroissance equals to UPDATED_VITESSE_CROISSANCE
        defaultPlanteShouldNotBeFound("vitesseCroissance.in=" + UPDATED_VITESSE_CROISSANCE);
    }

    @Test
    @Transactional
    void getAllPlantesByVitesseCroissanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where vitesseCroissance is not null
        defaultPlanteShouldBeFound("vitesseCroissance.specified=true");

        // Get all the planteList where vitesseCroissance is null
        defaultPlanteShouldNotBeFound("vitesseCroissance.specified=false");
    }

    @Test
    @Transactional
    void getAllPlantesByVitesseCroissanceContainsSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where vitesseCroissance contains DEFAULT_VITESSE_CROISSANCE
        defaultPlanteShouldBeFound("vitesseCroissance.contains=" + DEFAULT_VITESSE_CROISSANCE);

        // Get all the planteList where vitesseCroissance contains UPDATED_VITESSE_CROISSANCE
        defaultPlanteShouldNotBeFound("vitesseCroissance.contains=" + UPDATED_VITESSE_CROISSANCE);
    }

    @Test
    @Transactional
    void getAllPlantesByVitesseCroissanceNotContainsSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where vitesseCroissance does not contain DEFAULT_VITESSE_CROISSANCE
        defaultPlanteShouldNotBeFound("vitesseCroissance.doesNotContain=" + DEFAULT_VITESSE_CROISSANCE);

        // Get all the planteList where vitesseCroissance does not contain UPDATED_VITESSE_CROISSANCE
        defaultPlanteShouldBeFound("vitesseCroissance.doesNotContain=" + UPDATED_VITESSE_CROISSANCE);
    }

    @Test
    @Transactional
    void getAllPlantesByExpositionIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where exposition equals to DEFAULT_EXPOSITION
        defaultPlanteShouldBeFound("exposition.equals=" + DEFAULT_EXPOSITION);

        // Get all the planteList where exposition equals to UPDATED_EXPOSITION
        defaultPlanteShouldNotBeFound("exposition.equals=" + UPDATED_EXPOSITION);
    }

    @Test
    @Transactional
    void getAllPlantesByExpositionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where exposition not equals to DEFAULT_EXPOSITION
        defaultPlanteShouldNotBeFound("exposition.notEquals=" + DEFAULT_EXPOSITION);

        // Get all the planteList where exposition not equals to UPDATED_EXPOSITION
        defaultPlanteShouldBeFound("exposition.notEquals=" + UPDATED_EXPOSITION);
    }

    @Test
    @Transactional
    void getAllPlantesByExpositionIsInShouldWork() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where exposition in DEFAULT_EXPOSITION or UPDATED_EXPOSITION
        defaultPlanteShouldBeFound("exposition.in=" + DEFAULT_EXPOSITION + "," + UPDATED_EXPOSITION);

        // Get all the planteList where exposition equals to UPDATED_EXPOSITION
        defaultPlanteShouldNotBeFound("exposition.in=" + UPDATED_EXPOSITION);
    }

    @Test
    @Transactional
    void getAllPlantesByExpositionIsNullOrNotNull() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where exposition is not null
        defaultPlanteShouldBeFound("exposition.specified=true");

        // Get all the planteList where exposition is null
        defaultPlanteShouldNotBeFound("exposition.specified=false");
    }

    @Test
    @Transactional
    void getAllPlantesByExpositionContainsSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where exposition contains DEFAULT_EXPOSITION
        defaultPlanteShouldBeFound("exposition.contains=" + DEFAULT_EXPOSITION);

        // Get all the planteList where exposition contains UPDATED_EXPOSITION
        defaultPlanteShouldNotBeFound("exposition.contains=" + UPDATED_EXPOSITION);
    }

    @Test
    @Transactional
    void getAllPlantesByExpositionNotContainsSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);

        // Get all the planteList where exposition does not contain DEFAULT_EXPOSITION
        defaultPlanteShouldNotBeFound("exposition.doesNotContain=" + DEFAULT_EXPOSITION);

        // Get all the planteList where exposition does not contain UPDATED_EXPOSITION
        defaultPlanteShouldBeFound("exposition.doesNotContain=" + UPDATED_EXPOSITION);
    }

    @Test
    @Transactional
    void getAllPlantesByConfusionsIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);
        Ressemblance confusions;
        if (TestUtil.findAll(em, Ressemblance.class).isEmpty()) {
            confusions = RessemblanceResourceIT.createEntity(em);
            em.persist(confusions);
            em.flush();
        } else {
            confusions = TestUtil.findAll(em, Ressemblance.class).get(0);
        }
        em.persist(confusions);
        em.flush();
        plante.addConfusions(confusions);
        planteRepository.saveAndFlush(plante);
        Long confusionsId = confusions.getId();

        // Get all the planteList where confusions equals to confusionsId
        defaultPlanteShouldBeFound("confusionsId.equals=" + confusionsId);

        // Get all the planteList where confusions equals to (confusionsId + 1)
        defaultPlanteShouldNotBeFound("confusionsId.equals=" + (confusionsId + 1));
    }

    @Test
    @Transactional
    void getAllPlantesByEnsoleillementsIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);
        Ensoleillement ensoleillements;
        if (TestUtil.findAll(em, Ensoleillement.class).isEmpty()) {
            ensoleillements = EnsoleillementResourceIT.createEntity(em);
            em.persist(ensoleillements);
            em.flush();
        } else {
            ensoleillements = TestUtil.findAll(em, Ensoleillement.class).get(0);
        }
        em.persist(ensoleillements);
        em.flush();
        plante.addEnsoleillements(ensoleillements);
        planteRepository.saveAndFlush(plante);
        Long ensoleillementsId = ensoleillements.getId();

        // Get all the planteList where ensoleillements equals to ensoleillementsId
        defaultPlanteShouldBeFound("ensoleillementsId.equals=" + ensoleillementsId);

        // Get all the planteList where ensoleillements equals to (ensoleillementsId + 1)
        defaultPlanteShouldNotBeFound("ensoleillementsId.equals=" + (ensoleillementsId + 1));
    }

    @Test
    @Transactional
    void getAllPlantesByPlantesPotageresIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);
        Plante plantesPotageres;
        if (TestUtil.findAll(em, Plante.class).isEmpty()) {
            plantesPotageres = PlanteResourceIT.createEntity(em);
            em.persist(plantesPotageres);
            em.flush();
        } else {
            plantesPotageres = TestUtil.findAll(em, Plante.class).get(0);
        }
        em.persist(plantesPotageres);
        em.flush();
        plante.addPlantesPotageres(plantesPotageres);
        planteRepository.saveAndFlush(plante);
        Long plantesPotageresId = plantesPotageres.getId();

        // Get all the planteList where plantesPotageres equals to plantesPotageresId
        defaultPlanteShouldBeFound("plantesPotageresId.equals=" + plantesPotageresId);

        // Get all the planteList where plantesPotageres equals to (plantesPotageresId + 1)
        defaultPlanteShouldNotBeFound("plantesPotageresId.equals=" + (plantesPotageresId + 1));
    }

    @Test
    @Transactional
    void getAllPlantesByCycleDeVieIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);
        CycleDeVie cycleDeVie;
        if (TestUtil.findAll(em, CycleDeVie.class).isEmpty()) {
            cycleDeVie = CycleDeVieResourceIT.createEntity(em);
            em.persist(cycleDeVie);
            em.flush();
        } else {
            cycleDeVie = TestUtil.findAll(em, CycleDeVie.class).get(0);
        }
        em.persist(cycleDeVie);
        em.flush();
        plante.setCycleDeVie(cycleDeVie);
        planteRepository.saveAndFlush(plante);
        Long cycleDeVieId = cycleDeVie.getId();

        // Get all the planteList where cycleDeVie equals to cycleDeVieId
        defaultPlanteShouldBeFound("cycleDeVieId.equals=" + cycleDeVieId);

        // Get all the planteList where cycleDeVie equals to (cycleDeVieId + 1)
        defaultPlanteShouldNotBeFound("cycleDeVieId.equals=" + (cycleDeVieId + 1));
    }

    @Test
    @Transactional
    void getAllPlantesBySolIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);
        Sol sol;
        if (TestUtil.findAll(em, Sol.class).isEmpty()) {
            sol = SolResourceIT.createEntity(em);
            em.persist(sol);
            em.flush();
        } else {
            sol = TestUtil.findAll(em, Sol.class).get(0);
        }
        em.persist(sol);
        em.flush();
        plante.setSol(sol);
        planteRepository.saveAndFlush(plante);
        Long solId = sol.getId();

        // Get all the planteList where sol equals to solId
        defaultPlanteShouldBeFound("solId.equals=" + solId);

        // Get all the planteList where sol equals to (solId + 1)
        defaultPlanteShouldNotBeFound("solId.equals=" + (solId + 1));
    }

    @Test
    @Transactional
    void getAllPlantesByTemperatureIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);
        Temperature temperature;
        if (TestUtil.findAll(em, Temperature.class).isEmpty()) {
            temperature = TemperatureResourceIT.createEntity(em);
            em.persist(temperature);
            em.flush();
        } else {
            temperature = TestUtil.findAll(em, Temperature.class).get(0);
        }
        em.persist(temperature);
        em.flush();
        plante.setTemperature(temperature);
        planteRepository.saveAndFlush(plante);
        Long temperatureId = temperature.getId();

        // Get all the planteList where temperature equals to temperatureId
        defaultPlanteShouldBeFound("temperatureId.equals=" + temperatureId);

        // Get all the planteList where temperature equals to (temperatureId + 1)
        defaultPlanteShouldNotBeFound("temperatureId.equals=" + (temperatureId + 1));
    }

    @Test
    @Transactional
    void getAllPlantesByRacineIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);
        Racine racine;
        if (TestUtil.findAll(em, Racine.class).isEmpty()) {
            racine = RacineResourceIT.createEntity(em);
            em.persist(racine);
            em.flush();
        } else {
            racine = TestUtil.findAll(em, Racine.class).get(0);
        }
        em.persist(racine);
        em.flush();
        plante.setRacine(racine);
        planteRepository.saveAndFlush(plante);
        Long racineId = racine.getId();

        // Get all the planteList where racine equals to racineId
        defaultPlanteShouldBeFound("racineId.equals=" + racineId);

        // Get all the planteList where racine equals to (racineId + 1)
        defaultPlanteShouldNotBeFound("racineId.equals=" + (racineId + 1));
    }

    @Test
    @Transactional
    void getAllPlantesByStrateIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);
        Strate strate;
        if (TestUtil.findAll(em, Strate.class).isEmpty()) {
            strate = StrateResourceIT.createEntity(em);
            em.persist(strate);
            em.flush();
        } else {
            strate = TestUtil.findAll(em, Strate.class).get(0);
        }
        em.persist(strate);
        em.flush();
        plante.setStrate(strate);
        planteRepository.saveAndFlush(plante);
        Long strateId = strate.getId();

        // Get all the planteList where strate equals to strateId
        defaultPlanteShouldBeFound("strateId.equals=" + strateId);

        // Get all the planteList where strate equals to (strateId + 1)
        defaultPlanteShouldNotBeFound("strateId.equals=" + (strateId + 1));
    }

    @Test
    @Transactional
    void getAllPlantesByFeuillageIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);
        Feuillage feuillage;
        if (TestUtil.findAll(em, Feuillage.class).isEmpty()) {
            feuillage = FeuillageResourceIT.createEntity(em);
            em.persist(feuillage);
            em.flush();
        } else {
            feuillage = TestUtil.findAll(em, Feuillage.class).get(0);
        }
        em.persist(feuillage);
        em.flush();
        plante.setFeuillage(feuillage);
        planteRepository.saveAndFlush(plante);
        Long feuillageId = feuillage.getId();

        // Get all the planteList where feuillage equals to feuillageId
        defaultPlanteShouldBeFound("feuillageId.equals=" + feuillageId);

        // Get all the planteList where feuillage equals to (feuillageId + 1)
        defaultPlanteShouldNotBeFound("feuillageId.equals=" + (feuillageId + 1));
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
    void getAllPlantesByPlanteBotaniqueIsEqualToSomething() throws Exception {
        // Initialize the database
        planteRepository.saveAndFlush(plante);
        Plante planteBotanique;
        if (TestUtil.findAll(em, Plante.class).isEmpty()) {
            planteBotanique = PlanteResourceIT.createEntity(em);
            em.persist(planteBotanique);
            em.flush();
        } else {
            planteBotanique = TestUtil.findAll(em, Plante.class).get(0);
        }
        em.persist(planteBotanique);
        em.flush();
        plante.setPlanteBotanique(planteBotanique);
        planteRepository.saveAndFlush(plante);
        Long planteBotaniqueId = planteBotanique.getId();

        // Get all the planteList where planteBotanique equals to planteBotaniqueId
        defaultPlanteShouldBeFound("planteBotaniqueId.equals=" + planteBotaniqueId);

        // Get all the planteList where planteBotanique equals to (planteBotaniqueId + 1)
        defaultPlanteShouldNotBeFound("planteBotaniqueId.equals=" + (planteBotaniqueId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPlanteShouldBeFound(String filter) throws Exception {
        restPlanteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(plante.getId().intValue())))
            .andExpect(jsonPath("$.[*].entretien").value(hasItem(DEFAULT_ENTRETIEN)))
            .andExpect(jsonPath("$.[*].histoire").value(hasItem(DEFAULT_HISTOIRE)))
            .andExpect(jsonPath("$.[*].vitesseCroissance").value(hasItem(DEFAULT_VITESSE_CROISSANCE)))
            .andExpect(jsonPath("$.[*].exposition").value(hasItem(DEFAULT_EXPOSITION)));

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
        updatedPlante
            .entretien(UPDATED_ENTRETIEN)
            .histoire(UPDATED_HISTOIRE)
            .vitesseCroissance(UPDATED_VITESSE_CROISSANCE)
            .exposition(UPDATED_EXPOSITION);

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
        assertThat(testPlante.getEntretien()).isEqualTo(UPDATED_ENTRETIEN);
        assertThat(testPlante.getHistoire()).isEqualTo(UPDATED_HISTOIRE);
        assertThat(testPlante.getVitesseCroissance()).isEqualTo(UPDATED_VITESSE_CROISSANCE);
        assertThat(testPlante.getExposition()).isEqualTo(UPDATED_EXPOSITION);
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

        partialUpdatedPlante.vitesseCroissance(UPDATED_VITESSE_CROISSANCE);

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
        assertThat(testPlante.getEntretien()).isEqualTo(DEFAULT_ENTRETIEN);
        assertThat(testPlante.getHistoire()).isEqualTo(DEFAULT_HISTOIRE);
        assertThat(testPlante.getVitesseCroissance()).isEqualTo(UPDATED_VITESSE_CROISSANCE);
        assertThat(testPlante.getExposition()).isEqualTo(DEFAULT_EXPOSITION);
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

        partialUpdatedPlante
            .entretien(UPDATED_ENTRETIEN)
            .histoire(UPDATED_HISTOIRE)
            .vitesseCroissance(UPDATED_VITESSE_CROISSANCE)
            .exposition(UPDATED_EXPOSITION);

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
        assertThat(testPlante.getEntretien()).isEqualTo(UPDATED_ENTRETIEN);
        assertThat(testPlante.getHistoire()).isEqualTo(UPDATED_HISTOIRE);
        assertThat(testPlante.getVitesseCroissance()).isEqualTo(UPDATED_VITESSE_CROISSANCE);
        assertThat(testPlante.getExposition()).isEqualTo(UPDATED_EXPOSITION);
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
