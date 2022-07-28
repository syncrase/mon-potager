package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Mois;
import fr.syncrase.ecosyst.repository.MoisRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Mois}.
 */
@Service
@Transactional
public class MoisService {

    private final Logger log = LoggerFactory.getLogger(MoisService.class);

    private final MoisRepository moisRepository;

    public MoisService(MoisRepository moisRepository) {
        this.moisRepository = moisRepository;
    }

    /**
     * Save a mois.
     *
     * @param mois the entity to save.
     * @return the persisted entity.
     */
    public Mois save(Mois mois) {
        log.debug("Request to save Mois : {}", mois);
        return moisRepository.save(mois);
    }

    /**
     * Update a mois.
     *
     * @param mois the entity to save.
     * @return the persisted entity.
     */
    public Mois update(Mois mois) {
        log.debug("Request to save Mois : {}", mois);
        return moisRepository.save(mois);
    }

    /**
     * Partially update a mois.
     *
     * @param mois the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Mois> partialUpdate(Mois mois) {
        log.debug("Request to partially update Mois : {}", mois);

        return moisRepository
            .findById(mois.getId())
            .map(existingMois -> {
                if (mois.getNumero() != null) {
                    existingMois.setNumero(mois.getNumero());
                }
                if (mois.getNom() != null) {
                    existingMois.setNom(mois.getNom());
                }

                return existingMois;
            })
            .map(moisRepository::save);
    }

    /**
     * Get all the mois.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Mois> findAll(Pageable pageable) {
        log.debug("Request to get all Mois");
        return moisRepository.findAll(pageable);
    }

    /**
     * Get one mois by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Mois> findOne(Long id) {
        log.debug("Request to get Mois : {}", id);
        return moisRepository.findById(id);
    }

    /**
     * Delete the mois by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Mois : {}", id);
        moisRepository.deleteById(id);
    }
}
