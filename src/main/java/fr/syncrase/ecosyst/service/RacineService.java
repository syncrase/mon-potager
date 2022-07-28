package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Racine;
import fr.syncrase.ecosyst.repository.RacineRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Racine}.
 */
@Service
@Transactional
public class RacineService {

    private final Logger log = LoggerFactory.getLogger(RacineService.class);

    private final RacineRepository racineRepository;

    public RacineService(RacineRepository racineRepository) {
        this.racineRepository = racineRepository;
    }

    /**
     * Save a racine.
     *
     * @param racine the entity to save.
     * @return the persisted entity.
     */
    public Racine save(Racine racine) {
        log.debug("Request to save Racine : {}", racine);
        return racineRepository.save(racine);
    }

    /**
     * Update a racine.
     *
     * @param racine the entity to save.
     * @return the persisted entity.
     */
    public Racine update(Racine racine) {
        log.debug("Request to save Racine : {}", racine);
        return racineRepository.save(racine);
    }

    /**
     * Partially update a racine.
     *
     * @param racine the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Racine> partialUpdate(Racine racine) {
        log.debug("Request to partially update Racine : {}", racine);

        return racineRepository
            .findById(racine.getId())
            .map(existingRacine -> {
                if (racine.getType() != null) {
                    existingRacine.setType(racine.getType());
                }

                return existingRacine;
            })
            .map(racineRepository::save);
    }

    /**
     * Get all the racines.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Racine> findAll(Pageable pageable) {
        log.debug("Request to get all Racines");
        return racineRepository.findAll(pageable);
    }

    /**
     * Get one racine by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Racine> findOne(Long id) {
        log.debug("Request to get Racine : {}", id);
        return racineRepository.findById(id);
    }

    /**
     * Delete the racine by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Racine : {}", id);
        racineRepository.deleteById(id);
    }
}
