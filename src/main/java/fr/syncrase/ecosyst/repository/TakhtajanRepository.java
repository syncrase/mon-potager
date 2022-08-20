package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Takhtajan;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Takhtajan entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TakhtajanRepository extends JpaRepository<Takhtajan, Long>, JpaSpecificationExecutor<Takhtajan> {}
