package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.Mois;
import fr.syncrase.ecosyst.repository.MoisRepository;
import fr.syncrase.ecosyst.service.criteria.MoisCriteria;
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
 * Service for executing complex queries for {@link Mois} entities in the database.
 * The main input is a {@link MoisCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Mois} or a {@link Page} of {@link Mois} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MoisQueryService extends QueryService<Mois> {

    private final Logger log = LoggerFactory.getLogger(MoisQueryService.class);

    private final MoisRepository moisRepository;

    public MoisQueryService(MoisRepository moisRepository) {
        this.moisRepository = moisRepository;
    }

    /**
     * Return a {@link List} of {@link Mois} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Mois> findByCriteria(MoisCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Mois> specification = createSpecification(criteria);
        return moisRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Mois} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Mois> findByCriteria(MoisCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Mois> specification = createSpecification(criteria);
        return moisRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MoisCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Mois> specification = createSpecification(criteria);
        return moisRepository.count(specification);
    }

    /**
     * Function to convert {@link MoisCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Mois> createSpecification(MoisCriteria criteria) {
        Specification<Mois> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Mois_.id));
            }
            if (criteria.getNumero() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNumero(), Mois_.numero));
            }
            if (criteria.getNom() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNom(), Mois_.nom));
            }
        }
        return specification;
    }
}
