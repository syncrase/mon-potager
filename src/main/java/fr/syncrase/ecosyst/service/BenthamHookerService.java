package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.BenthamHooker;
import fr.syncrase.ecosyst.repository.BenthamHookerRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BenthamHooker}.
 */
@Service
@Transactional
public class BenthamHookerService {

    private final Logger log = LoggerFactory.getLogger(BenthamHookerService.class);

    private final BenthamHookerRepository benthamHookerRepository;

    public BenthamHookerService(BenthamHookerRepository benthamHookerRepository) {
        this.benthamHookerRepository = benthamHookerRepository;
    }

    /**
     * Save a benthamHooker.
     *
     * @param benthamHooker the entity to save.
     * @return the persisted entity.
     */
    public BenthamHooker save(BenthamHooker benthamHooker) {
        log.debug("Request to save BenthamHooker : {}", benthamHooker);
        return benthamHookerRepository.save(benthamHooker);
    }

    /**
     * Update a benthamHooker.
     *
     * @param benthamHooker the entity to save.
     * @return the persisted entity.
     */
    public BenthamHooker update(BenthamHooker benthamHooker) {
        log.debug("Request to save BenthamHooker : {}", benthamHooker);
        return benthamHookerRepository.save(benthamHooker);
    }

    /**
     * Partially update a benthamHooker.
     *
     * @param benthamHooker the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BenthamHooker> partialUpdate(BenthamHooker benthamHooker) {
        log.debug("Request to partially update BenthamHooker : {}", benthamHooker);

        return benthamHookerRepository
            .findById(benthamHooker.getId())
            .map(existingBenthamHooker -> {
                return existingBenthamHooker;
            })
            .map(benthamHookerRepository::save);
    }

    /**
     * Get all the benthamHookers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BenthamHooker> findAll(Pageable pageable) {
        log.debug("Request to get all BenthamHookers");
        return benthamHookerRepository.findAll(pageable);
    }

    /**
     * Get one benthamHooker by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BenthamHooker> findOne(Long id) {
        log.debug("Request to get BenthamHooker : {}", id);
        return benthamHookerRepository.findById(id);
    }

    /**
     * Delete the benthamHooker by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BenthamHooker : {}", id);
        benthamHookerRepository.deleteById(id);
    }
}
