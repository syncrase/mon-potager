package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Thorne;
import fr.syncrase.ecosyst.repository.ThorneRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Thorne}.
 */
@Service
@Transactional
public class ThorneService {

    private final Logger log = LoggerFactory.getLogger(ThorneService.class);

    private final ThorneRepository thorneRepository;

    public ThorneService(ThorneRepository thorneRepository) {
        this.thorneRepository = thorneRepository;
    }

    /**
     * Save a thorne.
     *
     * @param thorne the entity to save.
     * @return the persisted entity.
     */
    public Thorne save(Thorne thorne) {
        log.debug("Request to save Thorne : {}", thorne);
        return thorneRepository.save(thorne);
    }

    /**
     * Update a thorne.
     *
     * @param thorne the entity to save.
     * @return the persisted entity.
     */
    public Thorne update(Thorne thorne) {
        log.debug("Request to save Thorne : {}", thorne);
        return thorneRepository.save(thorne);
    }

    /**
     * Partially update a thorne.
     *
     * @param thorne the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Thorne> partialUpdate(Thorne thorne) {
        log.debug("Request to partially update Thorne : {}", thorne);

        return thorneRepository
            .findById(thorne.getId())
            .map(existingThorne -> {
                return existingThorne;
            })
            .map(thorneRepository::save);
    }

    /**
     * Get all the thornes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Thorne> findAll(Pageable pageable) {
        log.debug("Request to get all Thornes");
        return thorneRepository.findAll(pageable);
    }

    /**
     * Get one thorne by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Thorne> findOne(Long id) {
        log.debug("Request to get Thorne : {}", id);
        return thorneRepository.findById(id);
    }

    /**
     * Delete the thorne by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Thorne : {}", id);
        thorneRepository.deleteById(id);
    }
}
