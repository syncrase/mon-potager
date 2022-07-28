package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Strate;
import fr.syncrase.ecosyst.repository.StrateRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Strate}.
 */
@Service
@Transactional
public class StrateService {

    private final Logger log = LoggerFactory.getLogger(StrateService.class);

    private final StrateRepository strateRepository;

    public StrateService(StrateRepository strateRepository) {
        this.strateRepository = strateRepository;
    }

    /**
     * Save a strate.
     *
     * @param strate the entity to save.
     * @return the persisted entity.
     */
    public Strate save(Strate strate) {
        log.debug("Request to save Strate : {}", strate);
        return strateRepository.save(strate);
    }

    /**
     * Update a strate.
     *
     * @param strate the entity to save.
     * @return the persisted entity.
     */
    public Strate update(Strate strate) {
        log.debug("Request to save Strate : {}", strate);
        return strateRepository.save(strate);
    }

    /**
     * Partially update a strate.
     *
     * @param strate the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Strate> partialUpdate(Strate strate) {
        log.debug("Request to partially update Strate : {}", strate);

        return strateRepository
            .findById(strate.getId())
            .map(existingStrate -> {
                if (strate.getType() != null) {
                    existingStrate.setType(strate.getType());
                }

                return existingStrate;
            })
            .map(strateRepository::save);
    }

    /**
     * Get all the strates.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Strate> findAll(Pageable pageable) {
        log.debug("Request to get all Strates");
        return strateRepository.findAll(pageable);
    }

    /**
     * Get one strate by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Strate> findOne(Long id) {
        log.debug("Request to get Strate : {}", id);
        return strateRepository.findById(id);
    }

    /**
     * Delete the strate by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Strate : {}", id);
        strateRepository.deleteById(id);
    }
}
