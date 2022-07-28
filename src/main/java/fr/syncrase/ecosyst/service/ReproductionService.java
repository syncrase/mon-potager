package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Reproduction;
import fr.syncrase.ecosyst.repository.ReproductionRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Reproduction}.
 */
@Service
@Transactional
public class ReproductionService {

    private final Logger log = LoggerFactory.getLogger(ReproductionService.class);

    private final ReproductionRepository reproductionRepository;

    public ReproductionService(ReproductionRepository reproductionRepository) {
        this.reproductionRepository = reproductionRepository;
    }

    /**
     * Save a reproduction.
     *
     * @param reproduction the entity to save.
     * @return the persisted entity.
     */
    public Reproduction save(Reproduction reproduction) {
        log.debug("Request to save Reproduction : {}", reproduction);
        return reproductionRepository.save(reproduction);
    }

    /**
     * Update a reproduction.
     *
     * @param reproduction the entity to save.
     * @return the persisted entity.
     */
    public Reproduction update(Reproduction reproduction) {
        log.debug("Request to save Reproduction : {}", reproduction);
        return reproductionRepository.save(reproduction);
    }

    /**
     * Partially update a reproduction.
     *
     * @param reproduction the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Reproduction> partialUpdate(Reproduction reproduction) {
        log.debug("Request to partially update Reproduction : {}", reproduction);

        return reproductionRepository
            .findById(reproduction.getId())
            .map(existingReproduction -> {
                if (reproduction.getVitesse() != null) {
                    existingReproduction.setVitesse(reproduction.getVitesse());
                }
                if (reproduction.getType() != null) {
                    existingReproduction.setType(reproduction.getType());
                }

                return existingReproduction;
            })
            .map(reproductionRepository::save);
    }

    /**
     * Get all the reproductions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Reproduction> findAll(Pageable pageable) {
        log.debug("Request to get all Reproductions");
        return reproductionRepository.findAll(pageable);
    }

    /**
     * Get one reproduction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Reproduction> findOne(Long id) {
        log.debug("Request to get Reproduction : {}", id);
        return reproductionRepository.findById(id);
    }

    /**
     * Delete the reproduction by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Reproduction : {}", id);
        reproductionRepository.deleteById(id);
    }
}
