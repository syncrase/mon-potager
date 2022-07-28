package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.PeriodeAnnee;
import fr.syncrase.ecosyst.repository.PeriodeAnneeRepository;
import fr.syncrase.ecosyst.service.criteria.PeriodeAnneeCriteria;
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
 * Service for executing complex queries for {@link PeriodeAnnee} entities in the database.
 * The main input is a {@link PeriodeAnneeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PeriodeAnnee} or a {@link Page} of {@link PeriodeAnnee} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PeriodeAnneeQueryService extends QueryService<PeriodeAnnee> {

    private final Logger log = LoggerFactory.getLogger(PeriodeAnneeQueryService.class);

    private final PeriodeAnneeRepository periodeAnneeRepository;

    public PeriodeAnneeQueryService(PeriodeAnneeRepository periodeAnneeRepository) {
        this.periodeAnneeRepository = periodeAnneeRepository;
    }

    /**
     * Return a {@link List} of {@link PeriodeAnnee} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PeriodeAnnee> findByCriteria(PeriodeAnneeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<PeriodeAnnee> specification = createSpecification(criteria);
        return periodeAnneeRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link PeriodeAnnee} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PeriodeAnnee> findByCriteria(PeriodeAnneeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PeriodeAnnee> specification = createSpecification(criteria);
        return periodeAnneeRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PeriodeAnneeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<PeriodeAnnee> specification = createSpecification(criteria);
        return periodeAnneeRepository.count(specification);
    }

    /**
     * Function to convert {@link PeriodeAnneeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PeriodeAnnee> createSpecification(PeriodeAnneeCriteria criteria) {
        Specification<PeriodeAnnee> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PeriodeAnnee_.id));
            }
            if (criteria.getDebutId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getDebutId(), root -> root.join(PeriodeAnnee_.debut, JoinType.LEFT).get(Mois_.id))
                    );
            }
            if (criteria.getFinId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getFinId(), root -> root.join(PeriodeAnnee_.fin, JoinType.LEFT).get(Mois_.id))
                    );
            }
        }
        return specification;
    }
}
