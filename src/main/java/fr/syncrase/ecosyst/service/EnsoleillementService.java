package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Ensoleillement;
import fr.syncrase.ecosyst.repository.EnsoleillementRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Ensoleillement}.
 */
@Service
@Transactional
public class EnsoleillementService {

    private final Logger log = LoggerFactory.getLogger(EnsoleillementService.class);

    private final EnsoleillementRepository ensoleillementRepository;

    public EnsoleillementService(EnsoleillementRepository ensoleillementRepository) {
        this.ensoleillementRepository = ensoleillementRepository;
    }

    /**
     * Save a ensoleillement.
     *
     * @param ensoleillement the entity to save.
     * @return the persisted entity.
     */
    public Ensoleillement save(Ensoleillement ensoleillement) {
        log.debug("Request to save Ensoleillement : {}", ensoleillement);
        return ensoleillementRepository.save(ensoleillement);
    }

    /**
     * Update a ensoleillement.
     *
     * @param ensoleillement the entity to save.
     * @return the persisted entity.
     */
    public Ensoleillement update(Ensoleillement ensoleillement) {
        log.debug("Request to save Ensoleillement : {}", ensoleillement);
        return ensoleillementRepository.save(ensoleillement);
    }

    /**
     * Partially update a ensoleillement.
     *
     * @param ensoleillement the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Ensoleillement> partialUpdate(Ensoleillement ensoleillement) {
        log.debug("Request to partially update Ensoleillement : {}", ensoleillement);

        return ensoleillementRepository
            .findById(ensoleillement.getId())
            .map(existingEnsoleillement -> {
                if (ensoleillement.getOrientation() != null) {
                    existingEnsoleillement.setOrientation(ensoleillement.getOrientation());
                }
                if (ensoleillement.getEnsoleilement() != null) {
                    existingEnsoleillement.setEnsoleilement(ensoleillement.getEnsoleilement());
                }

                return existingEnsoleillement;
            })
            .map(ensoleillementRepository::save);
    }

    /**
     * Get all the ensoleillements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Ensoleillement> findAll(Pageable pageable) {
        log.debug("Request to get all Ensoleillements");
        return ensoleillementRepository.findAll(pageable);
    }

    /**
     * Get one ensoleillement by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Ensoleillement> findOne(Long id) {
        log.debug("Request to get Ensoleillement : {}", id);
        return ensoleillementRepository.findById(id);
    }

    /**
     * Delete the ensoleillement by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Ensoleillement : {}", id);
        ensoleillementRepository.deleteById(id);
    }
}
