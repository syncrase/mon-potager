package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.BenthamHooker;
import fr.syncrase.ecosyst.domain.BenthamHooker_;
import fr.syncrase.ecosyst.domain.Classification_;
import fr.syncrase.ecosyst.repository.BenthamHookerRepository;
import fr.syncrase.ecosyst.service.criteria.BenthamHookerCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

import javax.persistence.criteria.JoinType;
import java.util.List;

/**
 * Service for executing complex queries for {@link BenthamHooker} entities in the database.
 * The main input is a {@link BenthamHookerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BenthamHooker} or a {@link Page} of {@link BenthamHooker} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BenthamHookerQueryService extends QueryService<BenthamHooker> {

    private final Logger log = LoggerFactory.getLogger(BenthamHookerQueryService.class);

    private final BenthamHookerRepository benthamHookerRepository;

    public BenthamHookerQueryService(BenthamHookerRepository benthamHookerRepository) {
        this.benthamHookerRepository = benthamHookerRepository;
    }

    /**
     * Return a {@link List} of {@link BenthamHooker} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BenthamHooker> findByCriteria(BenthamHookerCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<BenthamHooker> specification = createSpecification(criteria);
        return benthamHookerRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link BenthamHooker} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BenthamHooker> findByCriteria(BenthamHookerCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BenthamHooker> specification = createSpecification(criteria);
        return benthamHookerRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BenthamHookerCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<BenthamHooker> specification = createSpecification(criteria);
        return benthamHookerRepository.count(specification);
    }

    /**
     * Function to convert {@link BenthamHookerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BenthamHooker> createSpecification(BenthamHookerCriteria criteria) {
        Specification<BenthamHooker> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BenthamHooker_.id));
            }
            if (criteria.getClassificationId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getClassificationId(),
                            root -> root.join(BenthamHooker_.classification, JoinType.LEFT).get(Classification_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
