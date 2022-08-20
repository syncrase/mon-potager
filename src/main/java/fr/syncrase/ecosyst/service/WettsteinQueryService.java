package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.Wettstein;
import fr.syncrase.ecosyst.repository.WettsteinRepository;
import fr.syncrase.ecosyst.service.criteria.WettsteinCriteria;
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
 * Service for executing complex queries for {@link Wettstein} entities in the database.
 * The main input is a {@link WettsteinCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Wettstein} or a {@link Page} of {@link Wettstein} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WettsteinQueryService extends QueryService<Wettstein> {

    private final Logger log = LoggerFactory.getLogger(WettsteinQueryService.class);

    private final WettsteinRepository wettsteinRepository;

    public WettsteinQueryService(WettsteinRepository wettsteinRepository) {
        this.wettsteinRepository = wettsteinRepository;
    }

    /**
     * Return a {@link List} of {@link Wettstein} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Wettstein> findByCriteria(WettsteinCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Wettstein> specification = createSpecification(criteria);
        return wettsteinRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Wettstein} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Wettstein> findByCriteria(WettsteinCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Wettstein> specification = createSpecification(criteria);
        return wettsteinRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WettsteinCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Wettstein> specification = createSpecification(criteria);
        return wettsteinRepository.count(specification);
    }

    /**
     * Function to convert {@link WettsteinCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Wettstein> createSpecification(WettsteinCriteria criteria) {
        Specification<Wettstein> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Wettstein_.id));
            }
            if (criteria.getClassificationId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getClassificationId(),
                            root -> root.join(Wettstein_.classification, JoinType.LEFT).get(Classification_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
