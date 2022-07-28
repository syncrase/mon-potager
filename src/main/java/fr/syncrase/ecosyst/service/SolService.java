package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Sol;
import fr.syncrase.ecosyst.repository.SolRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Sol}.
 */
@Service
@Transactional
public class SolService {

    private final Logger log = LoggerFactory.getLogger(SolService.class);

    private final SolRepository solRepository;

    public SolService(SolRepository solRepository) {
        this.solRepository = solRepository;
    }

    /**
     * Save a sol.
     *
     * @param sol the entity to save.
     * @return the persisted entity.
     */
    public Sol save(Sol sol) {
        log.debug("Request to save Sol : {}", sol);
        return solRepository.save(sol);
    }

    /**
     * Update a sol.
     *
     * @param sol the entity to save.
     * @return the persisted entity.
     */
    public Sol update(Sol sol) {
        log.debug("Request to save Sol : {}", sol);
        return solRepository.save(sol);
    }

    /**
     * Partially update a sol.
     *
     * @param sol the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Sol> partialUpdate(Sol sol) {
        log.debug("Request to partially update Sol : {}", sol);

        return solRepository
            .findById(sol.getId())
            .map(existingSol -> {
                if (sol.getPhMin() != null) {
                    existingSol.setPhMin(sol.getPhMin());
                }
                if (sol.getPhMax() != null) {
                    existingSol.setPhMax(sol.getPhMax());
                }
                if (sol.getType() != null) {
                    existingSol.setType(sol.getType());
                }
                if (sol.getRichesse() != null) {
                    existingSol.setRichesse(sol.getRichesse());
                }

                return existingSol;
            })
            .map(solRepository::save);
    }

    /**
     * Get all the sols.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Sol> findAll(Pageable pageable) {
        log.debug("Request to get all Sols");
        return solRepository.findAll(pageable);
    }

    /**
     * Get one sol by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Sol> findOne(Long id) {
        log.debug("Request to get Sol : {}", id);
        return solRepository.findById(id);
    }

    /**
     * Delete the sol by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Sol : {}", id);
        solRepository.deleteById(id);
    }
}
