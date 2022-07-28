package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Germination;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Germination entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GerminationRepository extends JpaRepository<Germination, Long>, JpaSpecificationExecutor<Germination> {}
