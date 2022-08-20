package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Dahlgren;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Dahlgren entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DahlgrenRepository extends JpaRepository<Dahlgren, Long>, JpaSpecificationExecutor<Dahlgren> {}
