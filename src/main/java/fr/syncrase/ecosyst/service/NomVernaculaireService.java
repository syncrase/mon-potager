package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.NomVernaculaire;
import fr.syncrase.ecosyst.repository.NomVernaculaireRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link NomVernaculaire}.
 */
@Service
@Transactional
public class NomVernaculaireService {

    private final Logger log = LoggerFactory.getLogger(NomVernaculaireService.class);

    private final NomVernaculaireRepository nomVernaculaireRepository;

    public NomVernaculaireService(NomVernaculaireRepository nomVernaculaireRepository) {
        this.nomVernaculaireRepository = nomVernaculaireRepository;
    }

    /**
     * Save a nomVernaculaire.
     *
     * @param nomVernaculaire the entity to save.
     * @return the persisted entity.
     */
    public NomVernaculaire save(NomVernaculaire nomVernaculaire) {
        log.debug("Request to save NomVernaculaire : {}", nomVernaculaire);
        return nomVernaculaireRepository.save(nomVernaculaire);
    }

    /**
     * Update a nomVernaculaire.
     *
     * @param nomVernaculaire the entity to save.
     * @return the persisted entity.
     */
    public NomVernaculaire update(NomVernaculaire nomVernaculaire) {
        log.debug("Request to save NomVernaculaire : {}", nomVernaculaire);
        return nomVernaculaireRepository.save(nomVernaculaire);
    }

    /**
     * Partially update a nomVernaculaire.
     *
     * @param nomVernaculaire the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<NomVernaculaire> partialUpdate(NomVernaculaire nomVernaculaire) {
        log.debug("Request to partially update NomVernaculaire : {}", nomVernaculaire);

        return nomVernaculaireRepository
            .findById(nomVernaculaire.getId())
            .map(existingNomVernaculaire -> {
                if (nomVernaculaire.getNom() != null) {
                    existingNomVernaculaire.setNom(nomVernaculaire.getNom());
                }

                return existingNomVernaculaire;
            })
            .map(nomVernaculaireRepository::save);
    }

    /**
     * Get all the nomVernaculaires.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<NomVernaculaire> findAll(Pageable pageable) {
        log.debug("Request to get all NomVernaculaires");
        return nomVernaculaireRepository.findAll(pageable);
    }

    /**
     * Get one nomVernaculaire by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NomVernaculaire> findOne(Long id) {
        log.debug("Request to get NomVernaculaire : {}", id);
        return nomVernaculaireRepository.findById(id);
    }

    /**
     * Delete the nomVernaculaire by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete NomVernaculaire : {}", id);
        nomVernaculaireRepository.deleteById(id);
    }
}
