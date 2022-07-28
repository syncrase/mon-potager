package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.Feuillage;
import fr.syncrase.ecosyst.repository.FeuillageRepository;
import fr.syncrase.ecosyst.service.criteria.FeuillageCriteria;
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
 * Service for executing complex queries for {@link Feuillage} entities in the database.
 * The main input is a {@link FeuillageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Feuillage} or a {@link Page} of {@link Feuillage} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FeuillageQueryService extends QueryService<Feuillage> {

    private final Logger log = LoggerFactory.getLogger(FeuillageQueryService.class);

    private final FeuillageRepository feuillageRepository;

    public FeuillageQueryService(FeuillageRepository feuillageRepository) {
        this.feuillageRepository = feuillageRepository;
    }

    /**
     * Return a {@link List} of {@link Feuillage} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Feuillage> findByCriteria(FeuillageCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Feuillage> specification = createSpecification(criteria);
        return feuillageRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Feuillage} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Feuillage> findByCriteria(FeuillageCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Feuillage> specification = createSpecification(criteria);
        return feuillageRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FeuillageCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Feuillage> specification = createSpecification(criteria);
        return feuillageRepository.count(specification);
    }

    /**
     * Function to convert {@link FeuillageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Feuillage> createSpecification(FeuillageCriteria criteria) {
        Specification<Feuillage> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Feuillage_.id));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), Feuillage_.type));
            }
        }
        return specification;
    }
}
