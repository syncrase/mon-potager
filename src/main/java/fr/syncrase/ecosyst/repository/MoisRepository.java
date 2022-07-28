package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Mois;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Mois entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MoisRepository extends JpaRepository<Mois, Long>, JpaSpecificationExecutor<Mois> {}
