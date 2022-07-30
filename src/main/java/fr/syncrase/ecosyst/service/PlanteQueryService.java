package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.repository.PlanteRepository;
import fr.syncrase.ecosyst.service.criteria.PlanteCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Plante} entities in the database.
 * The main input is a {@link PlanteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Plante} or a {@link Page} of {@link Plante} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PlanteQueryService extends QueryService<Plante> {

    private final Logger log = LoggerFactory.getLogger(PlanteQueryService.class);

    private final PlanteRepository planteRepository;

    public PlanteQueryService(PlanteRepository planteRepository) {
        this.planteRepository = planteRepository;
    }

    /**
     * Return a {@link List} of {@link Plante} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Plante> findByCriteria(PlanteCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Plante> specification = createSpecification(criteria);
        return planteRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Plante} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Plante> findByCriteria(PlanteCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Plante> specification = createSpecification(criteria);
        return planteRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PlanteCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Plante> specification = createSpecification(criteria);
        return planteRepository.count(specification);
    }

    /**
     * Function to convert {@link PlanteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Plante> createSpecification(PlanteCriteria criteria) {
        Specification<Plante> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Plante_.id));
            }
            if (criteria.getLowestClassificationRankId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLowestClassificationRankId(),
                            root -> root.join(Plante_.lowestClassificationRanks, JoinType.LEFT).get(CronquistRank_.id)
                        )
                    );
            }
            if (criteria.getNomsVernaculairesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getNomsVernaculairesId(),
                            root -> root.join(Plante_.nomsVernaculaires, JoinType.LEFT).get(NomVernaculaire_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
