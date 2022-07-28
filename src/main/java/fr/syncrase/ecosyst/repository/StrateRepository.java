package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Strate;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Strate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StrateRepository extends JpaRepository<Strate, Long>, JpaSpecificationExecutor<Strate> {}
