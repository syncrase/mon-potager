package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Classification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Classification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClassificationRepository extends JpaRepository<Classification, Long>, JpaSpecificationExecutor<Classification> {}
