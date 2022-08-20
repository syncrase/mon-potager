package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*;
import fr.syncrase.ecosyst.repository.ClassificationRepository;
import fr.syncrase.ecosyst.service.criteria.ClassificationCriteria;
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
 * Service for executing complex queries for {@link Classification} entities in the database.
 * The main input is a {@link ClassificationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Classification} or a {@link Page} of {@link Classification} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClassificationQueryService extends QueryService<Classification> {

    private final Logger log = LoggerFactory.getLogger(ClassificationQueryService.class);

    private final ClassificationRepository classificationRepository;

    public ClassificationQueryService(ClassificationRepository classificationRepository) {
        this.classificationRepository = classificationRepository;
    }

    /**
     * Return a {@link List} of {@link Classification} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Classification> findByCriteria(ClassificationCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Classification> specification = createSpecification(criteria);
        return classificationRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Classification} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Classification> findByCriteria(ClassificationCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Classification> specification = createSpecification(criteria);
        return classificationRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ClassificationCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Classification> specification = createSpecification(criteria);
        return classificationRepository.count(specification);
    }

    /**
     * Function to convert {@link ClassificationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Classification> createSpecification(ClassificationCriteria criteria) {
        Specification<Classification> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Classification_.id));
            }
            if (criteria.getCronquistId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCronquistId(),
                            root -> root.join(Classification_.cronquist, JoinType.LEFT).get(CronquistRank_.id)
                        )
                    );
            }
            if (criteria.getApgId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getApgId(), root -> root.join(Classification_.apg, JoinType.LEFT).get(APG_.id))
                    );
            }
            if (criteria.getBenthamHookerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getBenthamHookerId(),
                            root -> root.join(Classification_.benthamHooker, JoinType.LEFT).get(BenthamHooker_.id)
                        )
                    );
            }
            if (criteria.getWettsteinId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getWettsteinId(),
                            root -> root.join(Classification_.wettstein, JoinType.LEFT).get(Wettstein_.id)
                        )
                    );
            }
            if (criteria.getThorneId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getThorneId(), root -> root.join(Classification_.thorne, JoinType.LEFT).get(Thorne_.id))
                    );
            }
            if (criteria.getTakhtajanId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTakhtajanId(),
                            root -> root.join(Classification_.takhtajan, JoinType.LEFT).get(Takhtajan_.id)
                        )
                    );
            }
            if (criteria.getEnglerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getEnglerId(), root -> root.join(Classification_.engler, JoinType.LEFT).get(Engler_.id))
                    );
            }
            if (criteria.getCandolleId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCandolleId(),
                            root -> root.join(Classification_.candolle, JoinType.LEFT).get(Candolle_.id)
                        )
                    );
            }
            if (criteria.getDahlgrenId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getDahlgrenId(),
                            root -> root.join(Classification_.dahlgren, JoinType.LEFT).get(Dahlgren_.id)
                        )
                    );
            }
            if (criteria.getPlantesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPlantesId(),
                            root -> root.join(Classification_.plantes, JoinType.LEFT).get(Plante_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
