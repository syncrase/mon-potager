package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.CronquistRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the CronquistRank entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CronquistRankRepository extends JpaRepository<CronquistRank, Long>, JpaSpecificationExecutor<CronquistRank> {}
