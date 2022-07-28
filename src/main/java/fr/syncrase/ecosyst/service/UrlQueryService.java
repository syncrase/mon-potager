package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.Url;
import fr.syncrase.ecosyst.repository.UrlRepository;
import fr.syncrase.ecosyst.service.criteria.UrlCriteria;
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
 * Service for executing complex queries for {@link Url} entities in the database.
 * The main input is a {@link UrlCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Url} or a {@link Page} of {@link Url} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UrlQueryService extends QueryService<Url> {

    private final Logger log = LoggerFactory.getLogger(UrlQueryService.class);

    private final UrlRepository urlRepository;

    public UrlQueryService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    /**
     * Return a {@link List} of {@link Url} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Url> findByCriteria(UrlCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Url> specification = createSpecification(criteria);
        return urlRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Url} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Url> findByCriteria(UrlCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Url> specification = createSpecification(criteria);
        return urlRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UrlCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Url> specification = createSpecification(criteria);
        return urlRepository.count(specification);
    }

    /**
     * Function to convert {@link UrlCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Url> createSpecification(UrlCriteria criteria) {
        Specification<Url> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Url_.id));
            }
            if (criteria.getUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUrl(), Url_.url));
            }
            if (criteria.getCronquistRankId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCronquistRankId(),
                            root -> root.join(Url_.cronquistRank, JoinType.LEFT).get(CronquistRank_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
