package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Allelopathie;
import fr.syncrase.ecosyst.repository.AllelopathieRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Allelopathie}.
 */
@Service
@Transactional
public class AllelopathieService {

    private final Logger log = LoggerFactory.getLogger(AllelopathieService.class);

    private final AllelopathieRepository allelopathieRepository;

    public AllelopathieService(AllelopathieRepository allelopathieRepository) {
        this.allelopathieRepository = allelopathieRepository;
    }

    /**
     * Save a allelopathie.
     *
     * @param allelopathie the entity to save.
     * @return the persisted entity.
     */
    public Allelopathie save(Allelopathie allelopathie) {
        log.debug("Request to save Allelopathie : {}", allelopathie);
        return allelopathieRepository.save(allelopathie);
    }

    /**
     * Update a allelopathie.
     *
     * @param allelopathie the entity to save.
     * @return the persisted entity.
     */
    public Allelopathie update(Allelopathie allelopathie) {
        log.debug("Request to save Allelopathie : {}", allelopathie);
        return allelopathieRepository.save(allelopathie);
    }

    /**
     * Partially update a allelopathie.
     *
     * @param allelopathie the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Allelopathie> partialUpdate(Allelopathie allelopathie) {
        log.debug("Request to partially update Allelopathie : {}", allelopathie);

        return allelopathieRepository
            .findById(allelopathie.getId())
            .map(existingAllelopathie -> {
                if (allelopathie.getType() != null) {
                    existingAllelopathie.setType(allelopathie.getType());
                }
                if (allelopathie.getDescription() != null) {
                    existingAllelopathie.setDescription(allelopathie.getDescription());
                }
                if (allelopathie.getImpact() != null) {
                    existingAllelopathie.setImpact(allelopathie.getImpact());
                }

                return existingAllelopathie;
            })
            .map(allelopathieRepository::save);
    }

    /**
     * Get all the allelopathies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Allelopathie> findAll(Pageable pageable) {
        log.debug("Request to get all Allelopathies");
        return allelopathieRepository.findAll(pageable);
    }

    /**
     * Get one allelopathie by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Allelopathie> findOne(Long id) {
        log.debug("Request to get Allelopathie : {}", id);
        return allelopathieRepository.findById(id);
    }

    /**
     * Delete the allelopathie by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Allelopathie : {}", id);
        allelopathieRepository.deleteById(id);
    }
}
