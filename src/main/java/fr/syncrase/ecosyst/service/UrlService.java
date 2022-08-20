package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Url;
import fr.syncrase.ecosyst.repository.UrlRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Url}.
 */
@Service
@Transactional
public class UrlService {

    private final Logger log = LoggerFactory.getLogger(UrlService.class);

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    /**
     * Save a url.
     *
     * @param url the entity to save.
     * @return the persisted entity.
     */
    public Url save(Url url) {
        log.debug("Request to save Url : {}", url);
        return urlRepository.save(url);
    }

    /**
     * Update a url.
     *
     * @param url the entity to save.
     * @return the persisted entity.
     */
    public Url update(Url url) {
        log.debug("Request to save Url : {}", url);
        return urlRepository.save(url);
    }

    /**
     * Partially update a url.
     *
     * @param url the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Url> partialUpdate(Url url) {
        log.debug("Request to partially update Url : {}", url);

        return urlRepository
            .findById(url.getId())
            .map(existingUrl -> {
                if (url.getUrl() != null) {
                    existingUrl.setUrl(url.getUrl());
                }

                return existingUrl;
            })
            .map(urlRepository::save);
    }

    /**
     * Get all the urls.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Url> findAll(Pageable pageable) {
        log.debug("Request to get all Urls");
        return urlRepository.findAll(pageable);
    }

    /**
     * Get one url by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Url> findOne(Long id) {
        log.debug("Request to get Url : {}", id);
        return urlRepository.findById(id);
    }

    /**
     * Delete the url by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Url : {}", id);
        urlRepository.deleteById(id);
    }
}
