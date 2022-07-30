package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.repository.CronquistRankRepository;
import fr.syncrase.ecosyst.service.criteria.CronquistRankCriteria;
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
 * Service for executing complex queries for {@link CronquistRank} entities in the database.
 * The main input is a {@link CronquistRankCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CronquistRank} or a {@link Page} of {@link CronquistRank} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CronquistRankQueryService extends QueryService<CronquistRank> {

    private final Logger log = LoggerFactory.getLogger(CronquistRankQueryService.class);

    private final CronquistRankRepository cronquistRankRepository;

    public CronquistRankQueryService(CronquistRankRepository cronquistRankRepository) {
        this.cronquistRankRepository = cronquistRankRepository;
    }

    /**
     * Return a {@link List} of {@link CronquistRank} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CronquistRank> findByCriteria(CronquistRankCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<CronquistRank> specification = createSpecification(criteria);
        return cronquistRankRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link CronquistRank} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CronquistRank> findByCriteria(CronquistRankCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CronquistRank> specification = createSpecification(criteria);
        return cronquistRankRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CronquistRankCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<CronquistRank> specification = createSpecification(criteria);
        return cronquistRankRepository.count(specification);
    }

    /**
     * Function to convert {@link CronquistRankCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CronquistRank> createSpecification(CronquistRankCriteria criteria) {
        Specification<CronquistRank> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), CronquistRank_.id));
            }
            if (criteria.getRank() != null) {
                specification = specification.and(buildSpecification(criteria.getRank(), CronquistRank_.rank));
            }
            if (criteria.getChildrenId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getChildrenId(),
                            root -> root.join(CronquistRank_.children, JoinType.LEFT).get(CronquistRank_.id)
                        )
                    );
            }
            if (criteria.getParentId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getParentId(),
                            root -> root.join(CronquistRank_.parent, JoinType.LEFT).get(CronquistRank_.id)
                        )
                    );
            }
            if (criteria.getPlanteId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPlanteId(), root -> root.join(CronquistRank_.plante, JoinType.LEFT).get(Plante_.id))
                    );
            }
        }
        return specification;
    }
}
