package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.PeriodeAnnee;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the PeriodeAnnee entity.
 */
@Repository
public interface PeriodeAnneeRepository extends JpaRepository<PeriodeAnnee, Long>, JpaSpecificationExecutor<PeriodeAnnee> {
    default Optional<PeriodeAnnee> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PeriodeAnnee> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PeriodeAnnee> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct periodeAnnee from PeriodeAnnee periodeAnnee left join fetch periodeAnnee.debut left join fetch periodeAnnee.fin",
        countQuery = "select count(distinct periodeAnnee) from PeriodeAnnee periodeAnnee"
    )
    Page<PeriodeAnnee> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct periodeAnnee from PeriodeAnnee periodeAnnee left join fetch periodeAnnee.debut left join fetch periodeAnnee.fin"
    )
    List<PeriodeAnnee> findAllWithToOneRelationships();

    @Query(
        "select periodeAnnee from PeriodeAnnee periodeAnnee left join fetch periodeAnnee.debut left join fetch periodeAnnee.fin where periodeAnnee.id =:id"
    )
    Optional<PeriodeAnnee> findOneWithToOneRelationships(@Param("id") Long id);
}
