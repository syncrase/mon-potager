package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Engler;
import fr.syncrase.ecosyst.repository.EnglerRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Engler}.
 */
@Service
@Transactional
public class EnglerService {

    private final Logger log = LoggerFactory.getLogger(EnglerService.class);

    private final EnglerRepository englerRepository;

    public EnglerService(EnglerRepository englerRepository) {
        this.englerRepository = englerRepository;
    }

    /**
     * Save a engler.
     *
     * @param engler the entity to save.
     * @return the persisted entity.
     */
    public Engler save(Engler engler) {
        log.debug("Request to save Engler : {}", engler);
        return englerRepository.save(engler);
    }

    /**
     * Update a engler.
     *
     * @param engler the entity to save.
     * @return the persisted entity.
     */
    public Engler update(Engler engler) {
        log.debug("Request to save Engler : {}", engler);
        return englerRepository.save(engler);
    }

    /**
     * Partially update a engler.
     *
     * @param engler the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Engler> partialUpdate(Engler engler) {
        log.debug("Request to partially update Engler : {}", engler);

        return englerRepository
            .findById(engler.getId())
            .map(existingEngler -> {
                return existingEngler;
            })
            .map(englerRepository::save);
    }

    /**
     * Get all the englers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Engler> findAll(Pageable pageable) {
        log.debug("Request to get all Englers");
        return englerRepository.findAll(pageable);
    }

    /**
     * Get one engler by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Engler> findOne(Long id) {
        log.debug("Request to get Engler : {}", id);
        return englerRepository.findById(id);
    }

    /**
     * Delete the engler by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Engler : {}", id);
        englerRepository.deleteById(id);
    }
}
