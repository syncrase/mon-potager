package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.NomVernaculaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the NomVernaculaire entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NomVernaculaireRepository extends JpaRepository<NomVernaculaire, Long>, JpaSpecificationExecutor<NomVernaculaire> {
    NomVernaculaire findByNom(String nom);
}
