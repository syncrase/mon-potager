package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Feuillage;
import fr.syncrase.ecosyst.repository.FeuillageRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Feuillage}.
 */
@Service
@Transactional
public class FeuillageService {

    private final Logger log = LoggerFactory.getLogger(FeuillageService.class);

    private final FeuillageRepository feuillageRepository;

    public FeuillageService(FeuillageRepository feuillageRepository) {
        this.feuillageRepository = feuillageRepository;
    }

    /**
     * Save a feuillage.
     *
     * @param feuillage the entity to save.
     * @return the persisted entity.
     */
    public Feuillage save(Feuillage feuillage) {
        log.debug("Request to save Feuillage : {}", feuillage);
        return feuillageRepository.save(feuillage);
    }

    /**
     * Update a feuillage.
     *
     * @param feuillage the entity to save.
     * @return the persisted entity.
     */
    public Feuillage update(Feuillage feuillage) {
        log.debug("Request to save Feuillage : {}", feuillage);
        return feuillageRepository.save(feuillage);
    }

    /**
     * Partially update a feuillage.
     *
     * @param feuillage the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Feuillage> partialUpdate(Feuillage feuillage) {
        log.debug("Request to partially update Feuillage : {}", feuillage);

        return feuillageRepository
            .findById(feuillage.getId())
            .map(existingFeuillage -> {
                if (feuillage.getType() != null) {
                    existingFeuillage.setType(feuillage.getType());
                }

                return existingFeuillage;
            })
            .map(feuillageRepository::save);
    }

    /**
     * Get all the feuillages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Feuillage> findAll(Pageable pageable) {
        log.debug("Request to get all Feuillages");
        return feuillageRepository.findAll(pageable);
    }

    /**
     * Get one feuillage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Feuillage> findOne(Long id) {
        log.debug("Request to get Feuillage : {}", id);
        return feuillageRepository.findById(id);
    }

    /**
     * Delete the feuillage by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Feuillage : {}", id);
        feuillageRepository.deleteById(id);
    }
}
