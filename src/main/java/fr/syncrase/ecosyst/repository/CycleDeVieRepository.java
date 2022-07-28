package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.CycleDeVie;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the CycleDeVie entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CycleDeVieRepository extends JpaRepository<CycleDeVie, Long>, JpaSpecificationExecutor<CycleDeVie> {}
