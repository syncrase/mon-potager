package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Allelopathie;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Allelopathie entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AllelopathieRepository extends JpaRepository<Allelopathie, Long>, JpaSpecificationExecutor<Allelopathie> {}
