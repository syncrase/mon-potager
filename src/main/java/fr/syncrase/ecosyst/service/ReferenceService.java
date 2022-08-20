package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Reference;
import fr.syncrase.ecosyst.repository.ReferenceRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Reference}.
 */
@Service
@Transactional
public class ReferenceService {

    private final Logger log = LoggerFactory.getLogger(ReferenceService.class);

    private final ReferenceRepository referenceRepository;

    public ReferenceService(ReferenceRepository referenceRepository) {
        this.referenceRepository = referenceRepository;
    }

    /**
     * Save a reference.
     *
     * @param reference the entity to save.
     * @return the persisted entity.
     */
    public Reference save(Reference reference) {
        log.debug("Request to save Reference : {}", reference);
        return referenceRepository.save(reference);
    }

    /**
     * Update a reference.
     *
     * @param reference the entity to save.
     * @return the persisted entity.
     */
    public Reference update(Reference reference) {
        log.debug("Request to save Reference : {}", reference);
        return referenceRepository.save(reference);
    }

    /**
     * Partially update a reference.
     *
     * @param reference the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Reference> partialUpdate(Reference reference) {
        log.debug("Request to partially update Reference : {}", reference);

        return referenceRepository
            .findById(reference.getId())
            .map(existingReference -> {
                if (reference.getDescription() != null) {
                    existingReference.setDescription(reference.getDescription());
                }
                if (reference.getType() != null) {
                    existingReference.setType(reference.getType());
                }

                return existingReference;
            })
            .map(referenceRepository::save);
    }

    /**
     * Get all the references.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Reference> findAll(Pageable pageable) {
        log.debug("Request to get all References");
        return referenceRepository.findAll(pageable);
    }

    /**
     * Get one reference by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Reference> findOne(Long id) {
        log.debug("Request to get Reference : {}", id);
        return referenceRepository.findById(id);
    }

    /**
     * Delete the reference by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Reference : {}", id);
        referenceRepository.deleteById(id);
    }
}
