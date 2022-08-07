package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.repository.CronquistRankRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CronquistRank}.
 */
@Service
@Transactional
public class CronquistRankService {

    private final Logger log = LoggerFactory.getLogger(CronquistRankService.class);

    private final CronquistRankRepository cronquistRankRepository;

    public CronquistRankService(CronquistRankRepository cronquistRankRepository) {
        this.cronquistRankRepository = cronquistRankRepository;
    }

    /**
     * Save a cronquistRank.
     *
     * @param cronquistRank the entity to save.
     * @return the persisted entity.
     */
    public CronquistRank save(CronquistRank cronquistRank) {
        log.debug("Request to save CronquistRank : {}", cronquistRank);
        return cronquistRankRepository.save(cronquistRank);
    }

    /**
     * Update a cronquistRank.
     *
     * @param cronquistRank the entity to save.
     * @return the persisted entity.
     */
    public CronquistRank update(CronquistRank cronquistRank) {
        log.debug("Request to save CronquistRank : {}", cronquistRank);
        return cronquistRankRepository.save(cronquistRank);
    }

    /**
     * Partially update a cronquistRank.
     *
     * @param cronquistRank the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CronquistRank> partialUpdate(CronquistRank cronquistRank) {
        log.debug("Request to partially update CronquistRank : {}", cronquistRank);

        return cronquistRankRepository
            .findById(cronquistRank.getId())
            .map(existingCronquistRank -> {
                if (cronquistRank.getRank() != null) {
                    existingCronquistRank.setRank(cronquistRank.getRank());
                }
                if (cronquistRank.getNom() != null) {
                    existingCronquistRank.setNom(cronquistRank.getNom());
                }

                return existingCronquistRank;
            })
            .map(cronquistRankRepository::save);
    }

    /**
     * Get all the cronquistRanks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CronquistRank> findAll(Pageable pageable) {
        log.debug("Request to get all CronquistRanks");
        return cronquistRankRepository.findAll(pageable);
    }

    /**
     * Get one cronquistRank by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CronquistRank> findOne(Long id) {
        log.debug("Request to get CronquistRank : {}", id);
        return cronquistRankRepository.findById(id);
    }

    /**
     * Delete the cronquistRank by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete CronquistRank : {}", id);
        cronquistRankRepository.deleteById(id);
    }
}
