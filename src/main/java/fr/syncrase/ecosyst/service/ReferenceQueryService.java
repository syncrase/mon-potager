package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.Reference;
import fr.syncrase.ecosyst.repository.ReferenceRepository;
import fr.syncrase.ecosyst.service.criteria.ReferenceCriteria;
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
 * Service for executing complex queries for {@link Reference} entities in the database.
 * The main input is a {@link ReferenceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Reference} or a {@link Page} of {@link Reference} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReferenceQueryService extends QueryService<Reference> {

    private final Logger log = LoggerFactory.getLogger(ReferenceQueryService.class);

    private final ReferenceRepository referenceRepository;

    public ReferenceQueryService(ReferenceRepository referenceRepository) {
        this.referenceRepository = referenceRepository;
    }

    /**
     * Return a {@link List} of {@link Reference} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Reference> findByCriteria(ReferenceCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Reference> specification = createSpecification(criteria);
        return referenceRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Reference} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Reference> findByCriteria(ReferenceCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Reference> specification = createSpecification(criteria);
        return referenceRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReferenceCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Reference> specification = createSpecification(criteria);
        return referenceRepository.count(specification);
    }

    /**
     * Function to convert {@link ReferenceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Reference> createSpecification(ReferenceCriteria criteria) {
        Specification<Reference> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Reference_.id));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Reference_.description));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Reference_.type));
            }
            if (criteria.getUrlId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUrlId(), root -> root.join(Reference_.url, JoinType.LEFT).get(Url_.id))
                    );
            }
            if (criteria.getPlantesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPlantesId(), root -> root.join(Reference_.plantes, JoinType.LEFT).get(Plante_.id))
                    );
            }
        }
        return specification;
    }
}
