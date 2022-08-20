package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Candolle;
import fr.syncrase.ecosyst.domain.Candolle_;
import fr.syncrase.ecosyst.domain.Classification_;
import fr.syncrase.ecosyst.repository.CandolleRepository;
import fr.syncrase.ecosyst.service.criteria.CandolleCriteria;
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
 * Service for executing complex queries for {@link Candolle} entities in the database.
 * The main input is a {@link CandolleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Candolle} or a {@link Page} of {@link Candolle} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CandolleQueryService extends QueryService<Candolle> {

    private final Logger log = LoggerFactory.getLogger(CandolleQueryService.class);

    private final CandolleRepository candolleRepository;

    public CandolleQueryService(CandolleRepository candolleRepository) {
        this.candolleRepository = candolleRepository;
    }

    /**
     * Return a {@link List} of {@link Candolle} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Candolle> findByCriteria(CandolleCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Candolle> specification = createSpecification(criteria);
        return candolleRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Candolle} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Candolle> findByCriteria(CandolleCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Candolle> specification = createSpecification(criteria);
        return candolleRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CandolleCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Candolle> specification = createSpecification(criteria);
        return candolleRepository.count(specification);
    }

    /**
     * Function to convert {@link CandolleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Candolle> createSpecification(CandolleCriteria criteria) {
        Specification<Candolle> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Candolle_.id));
            }
            if (criteria.getClassificationId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getClassificationId(),
                            root -> root.join(Candolle_.classification, JoinType.LEFT).get(Classification_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
