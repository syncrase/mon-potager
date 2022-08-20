package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.Engler;
import fr.syncrase.ecosyst.repository.EnglerRepository;
import fr.syncrase.ecosyst.service.criteria.EnglerCriteria;
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
 * Service for executing complex queries for {@link Engler} entities in the database.
 * The main input is a {@link EnglerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Engler} or a {@link Page} of {@link Engler} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EnglerQueryService extends QueryService<Engler> {

    private final Logger log = LoggerFactory.getLogger(EnglerQueryService.class);

    private final EnglerRepository englerRepository;

    public EnglerQueryService(EnglerRepository englerRepository) {
        this.englerRepository = englerRepository;
    }

    /**
     * Return a {@link List} of {@link Engler} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Engler> findByCriteria(EnglerCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Engler> specification = createSpecification(criteria);
        return englerRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Engler} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Engler> findByCriteria(EnglerCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Engler> specification = createSpecification(criteria);
        return englerRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EnglerCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Engler> specification = createSpecification(criteria);
        return englerRepository.count(specification);
    }

    /**
     * Function to convert {@link EnglerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Engler> createSpecification(EnglerCriteria criteria) {
        Specification<Engler> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Engler_.id));
            }
            if (criteria.getClassificationId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getClassificationId(),
                            root -> root.join(Engler_.classification, JoinType.LEFT).get(Classification_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
