package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.Ressemblance;
import fr.syncrase.ecosyst.repository.RessemblanceRepository;
import fr.syncrase.ecosyst.service.criteria.RessemblanceCriteria;
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
 * Service for executing complex queries for {@link Ressemblance} entities in the database.
 * The main input is a {@link RessemblanceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Ressemblance} or a {@link Page} of {@link Ressemblance} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RessemblanceQueryService extends QueryService<Ressemblance> {

    private final Logger log = LoggerFactory.getLogger(RessemblanceQueryService.class);

    private final RessemblanceRepository ressemblanceRepository;

    public RessemblanceQueryService(RessemblanceRepository ressemblanceRepository) {
        this.ressemblanceRepository = ressemblanceRepository;
    }

    /**
     * Return a {@link List} of {@link Ressemblance} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Ressemblance> findByCriteria(RessemblanceCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Ressemblance> specification = createSpecification(criteria);
        return ressemblanceRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Ressemblance} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Ressemblance> findByCriteria(RessemblanceCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Ressemblance> specification = createSpecification(criteria);
        return ressemblanceRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RessemblanceCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Ressemblance> specification = createSpecification(criteria);
        return ressemblanceRepository.count(specification);
    }

    /**
     * Function to convert {@link RessemblanceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Ressemblance> createSpecification(RessemblanceCriteria criteria) {
        Specification<Ressemblance> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Ressemblance_.id));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Ressemblance_.description));
            }
            if (criteria.getPlanteRessemblantId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPlanteRessemblantId(),
                            root -> root.join(Ressemblance_.planteRessemblant, JoinType.LEFT).get(Plante_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
