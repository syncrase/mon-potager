package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.PeriodeAnnee;
import fr.syncrase.ecosyst.repository.PeriodeAnneeRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PeriodeAnnee}.
 */
@Service
@Transactional
public class PeriodeAnneeService {

    private final Logger log = LoggerFactory.getLogger(PeriodeAnneeService.class);

    private final PeriodeAnneeRepository periodeAnneeRepository;

    public PeriodeAnneeService(PeriodeAnneeRepository periodeAnneeRepository) {
        this.periodeAnneeRepository = periodeAnneeRepository;
    }

    /**
     * Save a periodeAnnee.
     *
     * @param periodeAnnee the entity to save.
     * @return the persisted entity.
     */
    public PeriodeAnnee save(PeriodeAnnee periodeAnnee) {
        log.debug("Request to save PeriodeAnnee : {}", periodeAnnee);
        return periodeAnneeRepository.save(periodeAnnee);
    }

    /**
     * Update a periodeAnnee.
     *
     * @param periodeAnnee the entity to save.
     * @return the persisted entity.
     */
    public PeriodeAnnee update(PeriodeAnnee periodeAnnee) {
        log.debug("Request to save PeriodeAnnee : {}", periodeAnnee);
        return periodeAnneeRepository.save(periodeAnnee);
    }

    /**
     * Partially update a periodeAnnee.
     *
     * @param periodeAnnee the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PeriodeAnnee> partialUpdate(PeriodeAnnee periodeAnnee) {
        log.debug("Request to partially update PeriodeAnnee : {}", periodeAnnee);

        return periodeAnneeRepository
            .findById(periodeAnnee.getId())
            .map(existingPeriodeAnnee -> {
                return existingPeriodeAnnee;
            })
            .map(periodeAnneeRepository::save);
    }

    /**
     * Get all the periodeAnnees.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PeriodeAnnee> findAll(Pageable pageable) {
        log.debug("Request to get all PeriodeAnnees");
        return periodeAnneeRepository.findAll(pageable);
    }

    /**
     * Get all the periodeAnnees with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PeriodeAnnee> findAllWithEagerRelationships(Pageable pageable) {
        return periodeAnneeRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one periodeAnnee by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PeriodeAnnee> findOne(Long id) {
        log.debug("Request to get PeriodeAnnee : {}", id);
        return periodeAnneeRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the periodeAnnee by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PeriodeAnnee : {}", id);
        periodeAnneeRepository.deleteById(id);
    }
}
