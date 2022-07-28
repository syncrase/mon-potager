package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.Ensoleillement;
import fr.syncrase.ecosyst.repository.EnsoleillementRepository;
import fr.syncrase.ecosyst.service.criteria.EnsoleillementCriteria;
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
 * Service for executing complex queries for {@link Ensoleillement} entities in the database.
 * The main input is a {@link EnsoleillementCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Ensoleillement} or a {@link Page} of {@link Ensoleillement} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EnsoleillementQueryService extends QueryService<Ensoleillement> {

    private final Logger log = LoggerFactory.getLogger(EnsoleillementQueryService.class);

    private final EnsoleillementRepository ensoleillementRepository;

    public EnsoleillementQueryService(EnsoleillementRepository ensoleillementRepository) {
        this.ensoleillementRepository = ensoleillementRepository;
    }

    /**
     * Return a {@link List} of {@link Ensoleillement} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Ensoleillement> findByCriteria(EnsoleillementCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Ensoleillement> specification = createSpecification(criteria);
        return ensoleillementRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Ensoleillement} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Ensoleillement> findByCriteria(EnsoleillementCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Ensoleillement> specification = createSpecification(criteria);
        return ensoleillementRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EnsoleillementCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Ensoleillement> specification = createSpecification(criteria);
        return ensoleillementRepository.count(specification);
    }

    /**
     * Function to convert {@link EnsoleillementCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Ensoleillement> createSpecification(EnsoleillementCriteria criteria) {
        Specification<Ensoleillement> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Ensoleillement_.id));
            }
            if (criteria.getOrientation() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOrientation(), Ensoleillement_.orientation));
            }
            if (criteria.getEnsoleilement() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEnsoleilement(), Ensoleillement_.ensoleilement));
            }
            if (criteria.getPlanteId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPlanteId(), root -> root.join(Ensoleillement_.plante, JoinType.LEFT).get(Plante_.id))
                    );
            }
        }
        return specification;
    }
}
