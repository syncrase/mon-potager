package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.APG;
import fr.syncrase.ecosyst.repository.APGRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link APG}.
 */
@Service
@Transactional
public class APGService {

    private final Logger log = LoggerFactory.getLogger(APGService.class);

    private final APGRepository aPGRepository;

    public APGService(APGRepository aPGRepository) {
        this.aPGRepository = aPGRepository;
    }

    /**
     * Save a aPG.
     *
     * @param aPG the entity to save.
     * @return the persisted entity.
     */
    public APG save(APG aPG) {
        log.debug("Request to save APG : {}", aPG);
        return aPGRepository.save(aPG);
    }

    /**
     * Update a aPG.
     *
     * @param aPG the entity to save.
     * @return the persisted entity.
     */
    public APG update(APG aPG) {
        log.debug("Request to save APG : {}", aPG);
        return aPGRepository.save(aPG);
    }

    /**
     * Partially update a aPG.
     *
     * @param aPG the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<APG> partialUpdate(APG aPG) {
        log.debug("Request to partially update APG : {}", aPG);

        return aPGRepository
            .findById(aPG.getId())
            .map(existingAPG -> {
                return existingAPG;
            })
            .map(aPGRepository::save);
    }

    /**
     * Get all the aPGS.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<APG> findAll(Pageable pageable) {
        log.debug("Request to get all APGS");
        return aPGRepository.findAll(pageable);
    }

    /**
     * Get one aPG by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<APG> findOne(Long id) {
        log.debug("Request to get APG : {}", id);
        return aPGRepository.findById(id);
    }

    /**
     * Delete the aPG by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete APG : {}", id);
        aPGRepository.deleteById(id);
    }
}
