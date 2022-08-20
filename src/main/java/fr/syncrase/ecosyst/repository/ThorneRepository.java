package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Thorne;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Thorne entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ThorneRepository extends JpaRepository<Thorne, Long>, JpaSpecificationExecutor<Thorne> {}
