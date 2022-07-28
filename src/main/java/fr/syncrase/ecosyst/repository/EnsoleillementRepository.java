package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Ensoleillement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Ensoleillement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EnsoleillementRepository extends JpaRepository<Ensoleillement, Long>, JpaSpecificationExecutor<Ensoleillement> {}
