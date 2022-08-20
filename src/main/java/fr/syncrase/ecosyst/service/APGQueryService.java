package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.APG;
import fr.syncrase.ecosyst.domain.APG_;
import fr.syncrase.ecosyst.domain.Classification_;
import fr.syncrase.ecosyst.repository.APGRepository;
import fr.syncrase.ecosyst.service.criteria.APGCriteria;
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
 * Service for executing complex queries for {@link APG} entities in the database.
 * The main input is a {@link APGCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link APG} or a {@link Page} of {@link APG} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class APGQueryService extends QueryService<APG> {

    private final Logger log = LoggerFactory.getLogger(APGQueryService.class);

    private final APGRepository aPGRepository;

    public APGQueryService(APGRepository aPGRepository) {
        this.aPGRepository = aPGRepository;
    }

    /**
     * Return a {@link List} of {@link APG} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<APG> findByCriteria(APGCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<APG> specification = createSpecification(criteria);
        return aPGRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link APG} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<APG> findByCriteria(APGCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<APG> specification = createSpecification(criteria);
        return aPGRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(APGCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<APG> specification = createSpecification(criteria);
        return aPGRepository.count(specification);
    }

    /**
     * Function to convert {@link APGCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<APG> createSpecification(APGCriteria criteria) {
        Specification<APG> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), APG_.id));
            }
            if (criteria.getClassificationId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getClassificationId(),
                            root -> root.join(APG_.classification, JoinType.LEFT).get(Classification_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
