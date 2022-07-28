package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.*; // for static metamodels
import fr.syncrase.ecosyst.domain.NomVernaculaire;
import fr.syncrase.ecosyst.repository.NomVernaculaireRepository;
import fr.syncrase.ecosyst.service.criteria.NomVernaculaireCriteria;
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
 * Service for executing complex queries for {@link NomVernaculaire} entities in the database.
 * The main input is a {@link NomVernaculaireCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link NomVernaculaire} or a {@link Page} of {@link NomVernaculaire} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NomVernaculaireQueryService extends QueryService<NomVernaculaire> {

    private final Logger log = LoggerFactory.getLogger(NomVernaculaireQueryService.class);

    private final NomVernaculaireRepository nomVernaculaireRepository;

    public NomVernaculaireQueryService(NomVernaculaireRepository nomVernaculaireRepository) {
        this.nomVernaculaireRepository = nomVernaculaireRepository;
    }

    /**
     * Return a {@link List} of {@link NomVernaculaire} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<NomVernaculaire> findByCriteria(NomVernaculaireCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<NomVernaculaire> specification = createSpecification(criteria);
        return nomVernaculaireRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link NomVernaculaire} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<NomVernaculaire> findByCriteria(NomVernaculaireCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<NomVernaculaire> specification = createSpecification(criteria);
        return nomVernaculaireRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(NomVernaculaireCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<NomVernaculaire> specification = createSpecification(criteria);
        return nomVernaculaireRepository.count(specification);
    }

    /**
     * Function to convert {@link NomVernaculaireCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<NomVernaculaire> createSpecification(NomVernaculaireCriteria criteria) {
        Specification<NomVernaculaire> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), NomVernaculaire_.id));
            }
            if (criteria.getNom() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNom(), NomVernaculaire_.nom));
            }
            if (criteria.getCronquistRankId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCronquistRankId(),
                            root -> root.join(NomVernaculaire_.cronquistRank, JoinType.LEFT).get(CronquistRank_.id)
                        )
                    );
            }
            if (criteria.getPlantesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPlantesId(),
                            root -> root.join(NomVernaculaire_.plantes, JoinType.LEFT).get(Plante_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
