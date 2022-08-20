package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Engler;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Engler entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EnglerRepository extends JpaRepository<Engler, Long>, JpaSpecificationExecutor<Engler> {}
