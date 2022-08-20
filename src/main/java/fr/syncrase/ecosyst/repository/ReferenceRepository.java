package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Reference;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Reference entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReferenceRepository extends JpaRepository<Reference, Long>, JpaSpecificationExecutor<Reference> {}
