package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Plante;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class PlanteRepositoryWithBagRelationshipsImpl implements PlanteRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Plante> fetchBagRelationships(Optional<Plante> plante) {
        return plante.map(this::fetchNomsVernaculaires);
    }

    @Override
    public Page<Plante> fetchBagRelationships(Page<Plante> plantes) {
        return new PageImpl<>(fetchBagRelationships(plantes.getContent()), plantes.getPageable(), plantes.getTotalElements());
    }

    @Override
    public List<Plante> fetchBagRelationships(List<Plante> plantes) {
        return Optional.of(plantes).map(this::fetchNomsVernaculaires).orElse(Collections.emptyList());
    }

    Plante fetchNomsVernaculaires(Plante result) {
        return entityManager
            .createQuery("select plante from Plante plante left join fetch plante.nomsVernaculaires where plante is :plante", Plante.class)
            .setParameter("plante", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Plante> fetchNomsVernaculaires(List<Plante> plantes) {
        return entityManager
            .createQuery(
                "select distinct plante from Plante plante left join fetch plante.nomsVernaculaires where plante in :plantes",
                Plante.class
            )
            .setParameter("plantes", plantes)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
