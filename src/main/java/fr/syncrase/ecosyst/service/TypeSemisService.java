package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.TypeSemis;
import fr.syncrase.ecosyst.repository.TypeSemisRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TypeSemis}.
 */
@Service
@Transactional
public class TypeSemisService {

    private final Logger log = LoggerFactory.getLogger(TypeSemisService.class);

    private final TypeSemisRepository typeSemisRepository;

    public TypeSemisService(TypeSemisRepository typeSemisRepository) {
        this.typeSemisRepository = typeSemisRepository;
    }

    /**
     * Save a typeSemis.
     *
     * @param typeSemis the entity to save.
     * @return the persisted entity.
     */
    public TypeSemis save(TypeSemis typeSemis) {
        log.debug("Request to save TypeSemis : {}", typeSemis);
        return typeSemisRepository.save(typeSemis);
    }

    /**
     * Update a typeSemis.
     *
     * @param typeSemis the entity to save.
     * @return the persisted entity.
     */
    public TypeSemis update(TypeSemis typeSemis) {
        log.debug("Request to save TypeSemis : {}", typeSemis);
        return typeSemisRepository.save(typeSemis);
    }

    /**
     * Partially update a typeSemis.
     *
     * @param typeSemis the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TypeSemis> partialUpdate(TypeSemis typeSemis) {
        log.debug("Request to partially update TypeSemis : {}", typeSemis);

        return typeSemisRepository
            .findById(typeSemis.getId())
            .map(existingTypeSemis -> {
                if (typeSemis.getType() != null) {
                    existingTypeSemis.setType(typeSemis.getType());
                }
                if (typeSemis.getDescription() != null) {
                    existingTypeSemis.setDescription(typeSemis.getDescription());
                }

                return existingTypeSemis;
            })
            .map(typeSemisRepository::save);
    }

    /**
     * Get all the typeSemis.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TypeSemis> findAll(Pageable pageable) {
        log.debug("Request to get all TypeSemis");
        return typeSemisRepository.findAll(pageable);
    }

    /**
     * Get one typeSemis by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TypeSemis> findOne(Long id) {
        log.debug("Request to get TypeSemis : {}", id);
        return typeSemisRepository.findById(id);
    }

    /**
     * Delete the typeSemis by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TypeSemis : {}", id);
        typeSemisRepository.deleteById(id);
    }
}
