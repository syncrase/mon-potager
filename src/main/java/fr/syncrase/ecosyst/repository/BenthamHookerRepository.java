package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.BenthamHooker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the BenthamHooker entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BenthamHookerRepository extends JpaRepository<BenthamHooker, Long>, JpaSpecificationExecutor<BenthamHooker> {}
