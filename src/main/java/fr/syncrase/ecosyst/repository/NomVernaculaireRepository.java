package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.NomVernaculaire;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the NomVernaculaire entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NomVernaculaireRepository extends JpaRepository<NomVernaculaire, Long>, JpaSpecificationExecutor<NomVernaculaire> {}
