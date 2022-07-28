package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Semis;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Semis entity.
 */
@Repository
public interface SemisRepository extends JpaRepository<Semis, Long>, JpaSpecificationExecutor<Semis> {
    default Optional<Semis> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Semis> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Semis> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct semis from Semis semis left join fetch semis.typeSemis",
        countQuery = "select count(distinct semis) from Semis semis"
    )
    Page<Semis> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct semis from Semis semis left join fetch semis.typeSemis")
    List<Semis> findAllWithToOneRelationships();

    @Query("select semis from Semis semis left join fetch semis.typeSemis where semis.id =:id")
    Optional<Semis> findOneWithToOneRelationships(@Param("id") Long id);
}
