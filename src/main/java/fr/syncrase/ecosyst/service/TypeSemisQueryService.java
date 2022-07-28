package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.TypeSemis;
import fr.syncrase.ecosyst.repository.TypeSemisRepository;
import fr.syncrase.ecosyst.service.criteria.TypeSemisCriteria;
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
 * Service for executing complex queries for {@link TypeSemis} entities in the database.
 * The main input is a {@link TypeSemisCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TypeSemis} or a {@link Page} of {@link TypeSemis} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TypeSemisQueryService extends QueryService<TypeSemis> {

    private final Logger log = LoggerFactory.getLogger(TypeSemisQueryService.class);

    private final TypeSemisRepository typeSemisRepository;

    public TypeSemisQueryService(TypeSemisRepository typeSemisRepository) {
        this.typeSemisRepository = typeSemisRepository;
    }

    /**
     * Return a {@link List} of {@link TypeSemis} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TypeSemis> findByCriteria(TypeSemisCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TypeSemis> specification = createSpecification(criteria);
        return typeSemisRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link TypeSemis} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TypeSemis> findByCriteria(TypeSemisCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TypeSemis> specification = createSpecification(criteria);
        return typeSemisRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TypeSemisCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TypeSemis> specification = createSpecification(criteria);
        return typeSemisRepository.count(specification);
    }

    /**
     * Function to convert {@link TypeSemisCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TypeSemis> createSpecification(TypeSemisCriteria criteria) {
        Specification<TypeSemis> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TypeSemis_.id));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), TypeSemis_.type));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), TypeSemis_.description));
            }
        }
        return specification;
    }
}
