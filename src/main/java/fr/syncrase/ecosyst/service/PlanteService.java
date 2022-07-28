package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.repository.PlanteRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Plante}.
 */
@Service
@Transactional
public class PlanteService {

    private final Logger log = LoggerFactory.getLogger(PlanteService.class);

    private final PlanteRepository planteRepository;

    public PlanteService(PlanteRepository planteRepository) {
        this.planteRepository = planteRepository;
    }

    /**
     * Save a plante.
     *
     * @param plante the entity to save.
     * @return the persisted entity.
     */
    public Plante save(Plante plante) {
        log.debug("Request to save Plante : {}", plante);
        return planteRepository.save(plante);
    }

    /**
     * Update a plante.
     *
     * @param plante the entity to save.
     * @return the persisted entity.
     */
    public Plante update(Plante plante) {
        log.debug("Request to save Plante : {}", plante);
        return planteRepository.save(plante);
    }

    /**
     * Partially update a plante.
     *
     * @param plante the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Plante> partialUpdate(Plante plante) {
        log.debug("Request to partially update Plante : {}", plante);

        return planteRepository
            .findById(plante.getId())
            .map(existingPlante -> {
                if (plante.getEntretien() != null) {
                    existingPlante.setEntretien(plante.getEntretien());
                }
                if (plante.getHistoire() != null) {
                    existingPlante.setHistoire(plante.getHistoire());
                }
                if (plante.getVitesseCroissance() != null) {
                    existingPlante.setVitesseCroissance(plante.getVitesseCroissance());
                }
                if (plante.getExposition() != null) {
                    existingPlante.setExposition(plante.getExposition());
                }

                return existingPlante;
            })
            .map(planteRepository::save);
    }

    /**
     * Get all the plantes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Plante> findAll(Pageable pageable) {
        log.debug("Request to get all Plantes");
        return planteRepository.findAll(pageable);
    }

    /**
     * Get all the plantes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Plante> findAllWithEagerRelationships(Pageable pageable) {
        return planteRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one plante by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Plante> findOne(Long id) {
        log.debug("Request to get Plante : {}", id);
        return planteRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the plante by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Plante : {}", id);
        planteRepository.deleteById(id);
    }
}
