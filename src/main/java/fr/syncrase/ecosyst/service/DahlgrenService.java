package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Dahlgren;
import fr.syncrase.ecosyst.repository.DahlgrenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Dahlgren}.
 */
@Service
@Transactional
public class DahlgrenService {

    private final Logger log = LoggerFactory.getLogger(DahlgrenService.class);

    private final DahlgrenRepository dahlgrenRepository;

    public DahlgrenService(DahlgrenRepository dahlgrenRepository) {
        this.dahlgrenRepository = dahlgrenRepository;
    }

    /**
     * Save a dahlgren.
     *
     * @param dahlgren the entity to save.
     * @return the persisted entity.
     */
    public Dahlgren save(Dahlgren dahlgren) {
        log.debug("Request to save Dahlgren : {}", dahlgren);
        return dahlgrenRepository.save(dahlgren);
    }

    /**
     * Update a dahlgren.
     *
     * @param dahlgren the entity to save.
     * @return the persisted entity.
     */
    public Dahlgren update(Dahlgren dahlgren) {
        log.debug("Request to save Dahlgren : {}", dahlgren);
        return dahlgrenRepository.save(dahlgren);
    }

    /**
     * Partially update a dahlgren.
     *
     * @param dahlgren the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Dahlgren> partialUpdate(Dahlgren dahlgren) {
        log.debug("Request to partially update Dahlgren : {}", dahlgren);

        return dahlgrenRepository
            .findById(dahlgren.getId())
            .map(existingDahlgren -> {
                return existingDahlgren;
            })
            .map(dahlgrenRepository::save);
    }

    /**
     * Get all the dahlgrens.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Dahlgren> findAll(Pageable pageable) {
        log.debug("Request to get all Dahlgrens");
        return dahlgrenRepository.findAll(pageable);
    }

    /**
     * Get one dahlgren by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Dahlgren> findOne(Long id) {
        log.debug("Request to get Dahlgren : {}", id);
        return dahlgrenRepository.findById(id);
    }

    /**
     * Delete the dahlgren by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Dahlgren : {}", id);
        dahlgrenRepository.deleteById(id);
    }
}
