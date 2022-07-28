package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.Strate;
import fr.syncrase.ecosyst.repository.StrateRepository;
import fr.syncrase.ecosyst.service.criteria.StrateCriteria;
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
 * Service for executing complex queries for {@link Strate} entities in the database.
 * The main input is a {@link StrateCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Strate} or a {@link Page} of {@link Strate} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StrateQueryService extends QueryService<Strate> {

    private final Logger log = LoggerFactory.getLogger(StrateQueryService.class);

    private final StrateRepository strateRepository;

    public StrateQueryService(StrateRepository strateRepository) {
        this.strateRepository = strateRepository;
    }

    /**
     * Return a {@link List} of {@link Strate} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Strate> findByCriteria(StrateCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Strate> specification = createSpecification(criteria);
        return strateRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Strate} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Strate> findByCriteria(StrateCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Strate> specification = createSpecification(criteria);
        return strateRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StrateCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Strate> specification = createSpecification(criteria);
        return strateRepository.count(specification);
    }

    /**
     * Function to convert {@link StrateCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Strate> createSpecification(StrateCriteria criteria) {
        Specification<Strate> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Strate_.id));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), Strate_.type));
            }
        }
        return specification;
    }
}
