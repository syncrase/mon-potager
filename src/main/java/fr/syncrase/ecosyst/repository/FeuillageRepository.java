package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Feuillage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Feuillage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FeuillageRepository extends JpaRepository<Feuillage, Long>, JpaSpecificationExecutor<Feuillage> {}
