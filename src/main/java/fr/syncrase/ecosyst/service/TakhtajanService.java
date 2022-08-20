package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Takhtajan;
import fr.syncrase.ecosyst.repository.TakhtajanRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Takhtajan}.
 */
@Service
@Transactional
public class TakhtajanService {

    private final Logger log = LoggerFactory.getLogger(TakhtajanService.class);

    private final TakhtajanRepository takhtajanRepository;

    public TakhtajanService(TakhtajanRepository takhtajanRepository) {
        this.takhtajanRepository = takhtajanRepository;
    }

    /**
     * Save a takhtajan.
     *
     * @param takhtajan the entity to save.
     * @return the persisted entity.
     */
    public Takhtajan save(Takhtajan takhtajan) {
        log.debug("Request to save Takhtajan : {}", takhtajan);
        return takhtajanRepository.save(takhtajan);
    }

    /**
     * Update a takhtajan.
     *
     * @param takhtajan the entity to save.
     * @return the persisted entity.
     */
    public Takhtajan update(Takhtajan takhtajan) {
        log.debug("Request to save Takhtajan : {}", takhtajan);
        return takhtajanRepository.save(takhtajan);
    }

    /**
     * Partially update a takhtajan.
     *
     * @param takhtajan the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Takhtajan> partialUpdate(Takhtajan takhtajan) {
        log.debug("Request to partially update Takhtajan : {}", takhtajan);

        return takhtajanRepository
            .findById(takhtajan.getId())
            .map(existingTakhtajan -> {
                return existingTakhtajan;
            })
            .map(takhtajanRepository::save);
    }

    /**
     * Get all the takhtajans.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Takhtajan> findAll(Pageable pageable) {
        log.debug("Request to get all Takhtajans");
        return takhtajanRepository.findAll(pageable);
    }

    /**
     * Get one takhtajan by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Takhtajan> findOne(Long id) {
        log.debug("Request to get Takhtajan : {}", id);
        return takhtajanRepository.findById(id);
    }

    /**
     * Delete the takhtajan by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Takhtajan : {}", id);
        takhtajanRepository.deleteById(id);
    }
}
