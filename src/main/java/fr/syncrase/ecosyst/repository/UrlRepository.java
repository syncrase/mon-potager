package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Url entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UrlRepository extends JpaRepository<Url, Long>, JpaSpecificationExecutor<Url> {
    Url findOneByUrl(String url);
}
