package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Wettstein;
import fr.syncrase.ecosyst.repository.WettsteinRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Wettstein}.
 */
@Service
@Transactional
public class WettsteinService {

    private final Logger log = LoggerFactory.getLogger(WettsteinService.class);

    private final WettsteinRepository wettsteinRepository;

    public WettsteinService(WettsteinRepository wettsteinRepository) {
        this.wettsteinRepository = wettsteinRepository;
    }

    /**
     * Save a wettstein.
     *
     * @param wettstein the entity to save.
     * @return the persisted entity.
     */
    public Wettstein save(Wettstein wettstein) {
        log.debug("Request to save Wettstein : {}", wettstein);
        return wettsteinRepository.save(wettstein);
    }

    /**
     * Update a wettstein.
     *
     * @param wettstein the entity to save.
     * @return the persisted entity.
     */
    public Wettstein update(Wettstein wettstein) {
        log.debug("Request to save Wettstein : {}", wettstein);
        return wettsteinRepository.save(wettstein);
    }

    /**
     * Partially update a wettstein.
     *
     * @param wettstein the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Wettstein> partialUpdate(Wettstein wettstein) {
        log.debug("Request to partially update Wettstein : {}", wettstein);

        return wettsteinRepository
            .findById(wettstein.getId())
            .map(existingWettstein -> {
                return existingWettstein;
            })
            .map(wettsteinRepository::save);
    }

    /**
     * Get all the wettsteins.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Wettstein> findAll(Pageable pageable) {
        log.debug("Request to get all Wettsteins");
        return wettsteinRepository.findAll(pageable);
    }

    /**
     * Get one wettstein by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Wettstein> findOne(Long id) {
        log.debug("Request to get Wettstein : {}", id);
        return wettsteinRepository.findById(id);
    }

    /**
     * Delete the wettstein by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Wettstein : {}", id);
        wettsteinRepository.deleteById(id);
    }
}
