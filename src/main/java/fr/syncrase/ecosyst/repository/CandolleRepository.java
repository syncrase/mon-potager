package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Candolle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Candolle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CandolleRepository extends JpaRepository<Candolle, Long>, JpaSpecificationExecutor<Candolle> {}
