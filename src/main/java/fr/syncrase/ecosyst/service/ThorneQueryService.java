package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.Thorne;
import fr.syncrase.ecosyst.repository.ThorneRepository;
import fr.syncrase.ecosyst.service.criteria.ThorneCriteria;
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
 * Service for executing complex queries for {@link Thorne} entities in the database.
 * The main input is a {@link ThorneCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Thorne} or a {@link Page} of {@link Thorne} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ThorneQueryService extends QueryService<Thorne> {

    private final Logger log = LoggerFactory.getLogger(ThorneQueryService.class);

    private final ThorneRepository thorneRepository;

    public ThorneQueryService(ThorneRepository thorneRepository) {
        this.thorneRepository = thorneRepository;
    }

    /**
     * Return a {@link List} of {@link Thorne} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Thorne> findByCriteria(ThorneCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Thorne> specification = createSpecification(criteria);
        return thorneRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Thorne} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Thorne> findByCriteria(ThorneCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Thorne> specification = createSpecification(criteria);
        return thorneRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ThorneCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Thorne> specification = createSpecification(criteria);
        return thorneRepository.count(specification);
    }

    /**
     * Function to convert {@link ThorneCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Thorne> createSpecification(ThorneCriteria criteria) {
        Specification<Thorne> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Thorne_.id));
            }
            if (criteria.getClassificationId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getClassificationId(),
                            root -> root.join(Thorne_.classification, JoinType.LEFT).get(Classification_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
