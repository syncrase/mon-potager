package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Wettstein;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Wettstein entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WettsteinRepository extends JpaRepository<Wettstein, Long>, JpaSpecificationExecutor<Wettstein> {}
