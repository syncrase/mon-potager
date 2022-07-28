package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Semis;
import fr.syncrase.ecosyst.repository.SemisRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Semis}.
 */
@Service
@Transactional
public class SemisService {

    private final Logger log = LoggerFactory.getLogger(SemisService.class);

    private final SemisRepository semisRepository;

    public SemisService(SemisRepository semisRepository) {
        this.semisRepository = semisRepository;
    }

    /**
     * Save a semis.
     *
     * @param semis the entity to save.
     * @return the persisted entity.
     */
    public Semis save(Semis semis) {
        log.debug("Request to save Semis : {}", semis);
        return semisRepository.save(semis);
    }

    /**
     * Update a semis.
     *
     * @param semis the entity to save.
     * @return the persisted entity.
     */
    public Semis update(Semis semis) {
        log.debug("Request to save Semis : {}", semis);
        return semisRepository.save(semis);
    }

    /**
     * Partially update a semis.
     *
     * @param semis the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Semis> partialUpdate(Semis semis) {
        log.debug("Request to partially update Semis : {}", semis);

        return semisRepository
            .findById(semis.getId())
            .map(existingSemis -> {
                return existingSemis;
            })
            .map(semisRepository::save);
    }

    /**
     * Get all the semis.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Semis> findAll(Pageable pageable) {
        log.debug("Request to get all Semis");
        return semisRepository.findAll(pageable);
    }

    /**
     * Get all the semis with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Semis> findAllWithEagerRelationships(Pageable pageable) {
        return semisRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one semis by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Semis> findOne(Long id) {
        log.debug("Request to get Semis : {}", id);
        return semisRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the semis by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Semis : {}", id);
        semisRepository.deleteById(id);
    }
}
