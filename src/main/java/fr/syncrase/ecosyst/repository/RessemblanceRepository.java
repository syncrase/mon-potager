package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Ressemblance;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Ressemblance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RessemblanceRepository extends JpaRepository<Ressemblance, Long>, JpaSpecificationExecutor<Ressemblance> {}
