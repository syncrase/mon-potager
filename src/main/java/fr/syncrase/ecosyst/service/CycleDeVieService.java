package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.CycleDeVie;
import fr.syncrase.ecosyst.repository.CycleDeVieRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CycleDeVie}.
 */
@Service
@Transactional
public class CycleDeVieService {

    private final Logger log = LoggerFactory.getLogger(CycleDeVieService.class);

    private final CycleDeVieRepository cycleDeVieRepository;

    public CycleDeVieService(CycleDeVieRepository cycleDeVieRepository) {
        this.cycleDeVieRepository = cycleDeVieRepository;
    }

    /**
     * Save a cycleDeVie.
     *
     * @param cycleDeVie the entity to save.
     * @return the persisted entity.
     */
    public CycleDeVie save(CycleDeVie cycleDeVie) {
        log.debug("Request to save CycleDeVie : {}", cycleDeVie);
        return cycleDeVieRepository.save(cycleDeVie);
    }

    /**
     * Update a cycleDeVie.
     *
     * @param cycleDeVie the entity to save.
     * @return the persisted entity.
     */
    public CycleDeVie update(CycleDeVie cycleDeVie) {
        log.debug("Request to save CycleDeVie : {}", cycleDeVie);
        return cycleDeVieRepository.save(cycleDeVie);
    }

    /**
     * Partially update a cycleDeVie.
     *
     * @param cycleDeVie the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CycleDeVie> partialUpdate(CycleDeVie cycleDeVie) {
        log.debug("Request to partially update CycleDeVie : {}", cycleDeVie);

        return cycleDeVieRepository
            .findById(cycleDeVie.getId())
            .map(existingCycleDeVie -> {
                return existingCycleDeVie;
            })
            .map(cycleDeVieRepository::save);
    }

    /**
     * Get all the cycleDeVies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CycleDeVie> findAll(Pageable pageable) {
        log.debug("Request to get all CycleDeVies");
        return cycleDeVieRepository.findAll(pageable);
    }

    /**
     * Get one cycleDeVie by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CycleDeVie> findOne(Long id) {
        log.debug("Request to get CycleDeVie : {}", id);
        return cycleDeVieRepository.findById(id);
    }

    /**
     * Delete the cycleDeVie by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete CycleDeVie : {}", id);
        cycleDeVieRepository.deleteById(id);
    }
}
