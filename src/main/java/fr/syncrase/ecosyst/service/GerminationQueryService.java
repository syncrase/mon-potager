package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.Germination;
import fr.syncrase.ecosyst.repository.GerminationRepository;
import fr.syncrase.ecosyst.service.criteria.GerminationCriteria;
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
 * Service for executing complex queries for {@link Germination} entities in the database.
 * The main input is a {@link GerminationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Germination} or a {@link Page} of {@link Germination} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class GerminationQueryService extends QueryService<Germination> {

    private final Logger log = LoggerFactory.getLogger(GerminationQueryService.class);

    private final GerminationRepository germinationRepository;

    public GerminationQueryService(GerminationRepository germinationRepository) {
        this.germinationRepository = germinationRepository;
    }

    /**
     * Return a {@link List} of {@link Germination} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Germination> findByCriteria(GerminationCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Germination> specification = createSpecification(criteria);
        return germinationRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Germination} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Germination> findByCriteria(GerminationCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Germination> specification = createSpecification(criteria);
        return germinationRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(GerminationCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Germination> specification = createSpecification(criteria);
        return germinationRepository.count(specification);
    }

    /**
     * Function to convert {@link GerminationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Germination> createSpecification(GerminationCriteria criteria) {
        Specification<Germination> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Germination_.id));
            }
            if (criteria.getTempsDeGermination() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getTempsDeGermination(), Germination_.tempsDeGermination));
            }
            if (criteria.getConditionDeGermination() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getConditionDeGermination(), Germination_.conditionDeGermination));
            }
        }
        return specification;
    }
}
