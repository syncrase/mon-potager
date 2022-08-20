package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Classification_;
import fr.syncrase.ecosyst.domain.Dahlgren;
import fr.syncrase.ecosyst.domain.Dahlgren_;
import fr.syncrase.ecosyst.repository.DahlgrenRepository;
import fr.syncrase.ecosyst.service.criteria.DahlgrenCriteria;
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
 * Service for executing complex queries for {@link Dahlgren} entities in the database.
 * The main input is a {@link DahlgrenCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Dahlgren} or a {@link Page} of {@link Dahlgren} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DahlgrenQueryService extends QueryService<Dahlgren> {

    private final Logger log = LoggerFactory.getLogger(DahlgrenQueryService.class);

    private final DahlgrenRepository dahlgrenRepository;

    public DahlgrenQueryService(DahlgrenRepository dahlgrenRepository) {
        this.dahlgrenRepository = dahlgrenRepository;
    }

    /**
     * Return a {@link List} of {@link Dahlgren} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Dahlgren> findByCriteria(DahlgrenCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Dahlgren> specification = createSpecification(criteria);
        return dahlgrenRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Dahlgren} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Dahlgren> findByCriteria(DahlgrenCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Dahlgren> specification = createSpecification(criteria);
        return dahlgrenRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DahlgrenCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Dahlgren> specification = createSpecification(criteria);
        return dahlgrenRepository.count(specification);
    }

    /**
     * Function to convert {@link DahlgrenCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Dahlgren> createSpecification(DahlgrenCriteria criteria) {
        Specification<Dahlgren> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Dahlgren_.id));
            }
            if (criteria.getClassificationId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getClassificationId(),
                            root -> root.join(Dahlgren_.classification, JoinType.LEFT).get(Classification_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
