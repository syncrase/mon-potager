package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Candolle;
import fr.syncrase.ecosyst.repository.CandolleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Candolle}.
 */
@Service
@Transactional
public class CandolleService {

    private final Logger log = LoggerFactory.getLogger(CandolleService.class);

    private final CandolleRepository candolleRepository;

    public CandolleService(CandolleRepository candolleRepository) {
        this.candolleRepository = candolleRepository;
    }

    /**
     * Save a candolle.
     *
     * @param candolle the entity to save.
     * @return the persisted entity.
     */
    public Candolle save(Candolle candolle) {
        log.debug("Request to save Candolle : {}", candolle);
        return candolleRepository.save(candolle);
    }

    /**
     * Update a candolle.
     *
     * @param candolle the entity to save.
     * @return the persisted entity.
     */
    public Candolle update(Candolle candolle) {
        log.debug("Request to save Candolle : {}", candolle);
        return candolleRepository.save(candolle);
    }

    /**
     * Partially update a candolle.
     *
     * @param candolle the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Candolle> partialUpdate(Candolle candolle) {
        log.debug("Request to partially update Candolle : {}", candolle);

        return candolleRepository
            .findById(candolle.getId())
            .map(existingCandolle -> {
                return existingCandolle;
            })
            .map(candolleRepository::save);
    }

    /**
     * Get all the candolles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Candolle> findAll(Pageable pageable) {
        log.debug("Request to get all Candolles");
        return candolleRepository.findAll(pageable);
    }

    /**
     * Get one candolle by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Candolle> findOne(Long id) {
        log.debug("Request to get Candolle : {}", id);
        return candolleRepository.findById(id);
    }

    /**
     * Delete the candolle by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Candolle : {}", id);
        candolleRepository.deleteById(id);
    }
}
