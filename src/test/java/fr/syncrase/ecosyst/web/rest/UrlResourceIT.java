package fr.syncrase.ecosyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.syncrase.ecosyst.IntegrationTest;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.Url;
import fr.syncrase.ecosyst.repository.UrlRepository;
import fr.syncrase.ecosyst.service.criteria.UrlCriteria;
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
 * Integration tests for the {@link UrlResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UrlResourceIT {

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/urls";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUrlMockMvc;

    private Url url;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Url createEntity(EntityManager em) {
        Url url = new Url().url(DEFAULT_URL);
        return url;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Url createUpdatedEntity(EntityManager em) {
        Url url = new Url().url(UPDATED_URL);
        return url;
    }

    @BeforeEach
    public void initTest() {
        url = createEntity(em);
    }

    @Test
    @Transactional
    void createUrl() throws Exception {
        int databaseSizeBeforeCreate = urlRepository.findAll().size();
        // Create the Url
        restUrlMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(url)))
            .andExpect(status().isCreated());

        // Validate the Url in the database
        List<Url> urlList = urlRepository.findAll();
        assertThat(urlList).hasSize(databaseSizeBeforeCreate + 1);
        Url testUrl = urlList.get(urlList.size() - 1);
        assertThat(testUrl.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    void createUrlWithExistingId() throws Exception {
        // Create the Url with an existing ID
        url.setId(1L);

        int databaseSizeBeforeCreate = urlRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUrlMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(url)))
            .andExpect(status().isBadRequest());

        // Validate the Url in the database
        List<Url> urlList = urlRepository.findAll();
        assertThat(urlList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = urlRepository.findAll().size();
        // set the field null
        url.setUrl(null);

        // Create the Url, which fails.

        restUrlMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(url)))
            .andExpect(status().isBadRequest());

        List<Url> urlList = urlRepository.findAll();
        assertThat(urlList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUrls() throws Exception {
        // Initialize the database
        urlRepository.saveAndFlush(url);

        // Get all the urlList
        restUrlMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(url.getId().intValue())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)));
    }

    @Test
    @Transactional
    void getUrl() throws Exception {
        // Initialize the database
        urlRepository.saveAndFlush(url);

        // Get the url
        restUrlMockMvc
            .perform(get(ENTITY_API_URL_ID, url.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(url.getId().intValue()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL));
    }

    @Test
    @Transactional
    void getUrlsByIdFiltering() throws Exception {
        // Initialize the database
        urlRepository.saveAndFlush(url);

        Long id = url.getId();

        defaultUrlShouldBeFound("id.equals=" + id);
        defaultUrlShouldNotBeFound("id.notEquals=" + id);

        defaultUrlShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUrlShouldNotBeFound("id.greaterThan=" + id);

        defaultUrlShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUrlShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUrlsByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        urlRepository.saveAndFlush(url);

        // Get all the urlList where url equals to DEFAULT_URL
        defaultUrlShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the urlList where url equals to UPDATED_URL
        defaultUrlShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllUrlsByUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        urlRepository.saveAndFlush(url);

        // Get all the urlList where url not equals to DEFAULT_URL
        defaultUrlShouldNotBeFound("url.notEquals=" + DEFAULT_URL);

        // Get all the urlList where url not equals to UPDATED_URL
        defaultUrlShouldBeFound("url.notEquals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllUrlsByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        urlRepository.saveAndFlush(url);

        // Get all the urlList where url in DEFAULT_URL or UPDATED_URL
        defaultUrlShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the urlList where url equals to UPDATED_URL
        defaultUrlShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllUrlsByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        urlRepository.saveAndFlush(url);

        // Get all the urlList where url is not null
        defaultUrlShouldBeFound("url.specified=true");

        // Get all the urlList where url is null
        defaultUrlShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    void getAllUrlsByUrlContainsSomething() throws Exception {
        // Initialize the database
        urlRepository.saveAndFlush(url);

        // Get all the urlList where url contains DEFAULT_URL
        defaultUrlShouldBeFound("url.contains=" + DEFAULT_URL);

        // Get all the urlList where url contains UPDATED_URL
        defaultUrlShouldNotBeFound("url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllUrlsByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        urlRepository.saveAndFlush(url);

        // Get all the urlList where url does not contain DEFAULT_URL
        defaultUrlShouldNotBeFound("url.doesNotContain=" + DEFAULT_URL);

        // Get all the urlList where url does not contain UPDATED_URL
        defaultUrlShouldBeFound("url.doesNotContain=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllUrlsByCronquistRankIsEqualToSomething() throws Exception {
        // Initialize the database
        urlRepository.saveAndFlush(url);
        CronquistRank cronquistRank;
        if (TestUtil.findAll(em, CronquistRank.class).isEmpty()) {
            cronquistRank = CronquistRankResourceIT.createEntity(em);
            em.persist(cronquistRank);
            em.flush();
        } else {
            cronquistRank = TestUtil.findAll(em, CronquistRank.class).get(0);
        }
        em.persist(cronquistRank);
        em.flush();
        url.setCronquistRank(cronquistRank);
        urlRepository.saveAndFlush(url);
        Long cronquistRankId = cronquistRank.getId();

        // Get all the urlList where cronquistRank equals to cronquistRankId
        defaultUrlShouldBeFound("cronquistRankId.equals=" + cronquistRankId);

        // Get all the urlList where cronquistRank equals to (cronquistRankId + 1)
        defaultUrlShouldNotBeFound("cronquistRankId.equals=" + (cronquistRankId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUrlShouldBeFound(String filter) throws Exception {
        restUrlMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(url.getId().intValue())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)));

        // Check, that the count call also returns 1
        restUrlMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUrlShouldNotBeFound(String filter) throws Exception {
        restUrlMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUrlMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUrl() throws Exception {
        // Get the url
        restUrlMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUrl() throws Exception {
        // Initialize the database
        urlRepository.saveAndFlush(url);

        int databaseSizeBeforeUpdate = urlRepository.findAll().size();

        // Update the url
        Url updatedUrl = urlRepository.findById(url.getId()).get();
        // Disconnect from session so that the updates on updatedUrl are not directly saved in db
        em.detach(updatedUrl);
        updatedUrl.url(UPDATED_URL);

        restUrlMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUrl.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUrl))
            )
            .andExpect(status().isOk());

        // Validate the Url in the database
        List<Url> urlList = urlRepository.findAll();
        assertThat(urlList).hasSize(databaseSizeBeforeUpdate);
        Url testUrl = urlList.get(urlList.size() - 1);
        assertThat(testUrl.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void putNonExistingUrl() throws Exception {
        int databaseSizeBeforeUpdate = urlRepository.findAll().size();
        url.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUrlMockMvc
            .perform(
                put(ENTITY_API_URL_ID, url.getId()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(url))
            )
            .andExpect(status().isBadRequest());

        // Validate the Url in the database
        List<Url> urlList = urlRepository.findAll();
        assertThat(urlList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUrl() throws Exception {
        int databaseSizeBeforeUpdate = urlRepository.findAll().size();
        url.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUrlMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(url))
            )
            .andExpect(status().isBadRequest());

        // Validate the Url in the database
        List<Url> urlList = urlRepository.findAll();
        assertThat(urlList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUrl() throws Exception {
        int databaseSizeBeforeUpdate = urlRepository.findAll().size();
        url.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUrlMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(url)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Url in the database
        List<Url> urlList = urlRepository.findAll();
        assertThat(urlList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUrlWithPatch() throws Exception {
        // Initialize the database
        urlRepository.saveAndFlush(url);

        int databaseSizeBeforeUpdate = urlRepository.findAll().size();

        // Update the url using partial update
        Url partialUpdatedUrl = new Url();
        partialUpdatedUrl.setId(url.getId());

        partialUpdatedUrl.url(UPDATED_URL);

        restUrlMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUrl.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUrl))
            )
            .andExpect(status().isOk());

        // Validate the Url in the database
        List<Url> urlList = urlRepository.findAll();
        assertThat(urlList).hasSize(databaseSizeBeforeUpdate);
        Url testUrl = urlList.get(urlList.size() - 1);
        assertThat(testUrl.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void fullUpdateUrlWithPatch() throws Exception {
        // Initialize the database
        urlRepository.saveAndFlush(url);

        int databaseSizeBeforeUpdate = urlRepository.findAll().size();

        // Update the url using partial update
        Url partialUpdatedUrl = new Url();
        partialUpdatedUrl.setId(url.getId());

        partialUpdatedUrl.url(UPDATED_URL);

        restUrlMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUrl.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUrl))
            )
            .andExpect(status().isOk());

        // Validate the Url in the database
        List<Url> urlList = urlRepository.findAll();
        assertThat(urlList).hasSize(databaseSizeBeforeUpdate);
        Url testUrl = urlList.get(urlList.size() - 1);
        assertThat(testUrl.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void patchNonExistingUrl() throws Exception {
        int databaseSizeBeforeUpdate = urlRepository.findAll().size();
        url.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUrlMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, url.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(url))
            )
            .andExpect(status().isBadRequest());

        // Validate the Url in the database
        List<Url> urlList = urlRepository.findAll();
        assertThat(urlList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUrl() throws Exception {
        int databaseSizeBeforeUpdate = urlRepository.findAll().size();
        url.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUrlMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(url))
            )
            .andExpect(status().isBadRequest());

        // Validate the Url in the database
        List<Url> urlList = urlRepository.findAll();
        assertThat(urlList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUrl() throws Exception {
        int databaseSizeBeforeUpdate = urlRepository.findAll().size();
        url.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUrlMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(url)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Url in the database
        List<Url> urlList = urlRepository.findAll();
        assertThat(urlList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUrl() throws Exception {
        // Initialize the database
        urlRepository.saveAndFlush(url);

        int databaseSizeBeforeDelete = urlRepository.findAll().size();

        // Delete the url
        restUrlMockMvc.perform(delete(ENTITY_API_URL_ID, url.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Url> urlList = urlRepository.findAll();
        assertThat(urlList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
