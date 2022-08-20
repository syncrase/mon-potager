package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.APG;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the APG entity.
 */
@SuppressWarnings("unused")
@Repository
public interface APGRepository extends JpaRepository<APG, Long>, JpaSpecificationExecutor<APG> {}
