package fr.syncrase.ecosyst.service.criteria;

import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.CronquistRank} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.CronquistRankResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cronquist-ranks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class CronquistRankCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CronquistTaxonomicRank
     */
    public static class CronquistTaxonomicRankFilter extends Filter<CronquistTaxonomicRank> {

        public CronquistTaxonomicRankFilter() {}

        public CronquistTaxonomicRankFilter(CronquistTaxonomicRankFilter filter) {
            super(filter);
        }

        @Override
        public CronquistTaxonomicRankFilter copy() {
            return new CronquistTaxonomicRankFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private CronquistTaxonomicRankFilter rank;

    private StringFilter nom;

    private LongFilter childrenId;

    private LongFilter parentId;

    private Boolean distinct;

    public CronquistRankCriteria() {}

    public CronquistRankCriteria(CronquistRankCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.rank = other.rank == null ? null : other.rank.copy();
        this.nom = other.nom == null ? null : other.nom.copy();
        this.childrenId = other.childrenId == null ? null : other.childrenId.copy();
        this.parentId = other.parentId == null ? null : other.parentId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public CronquistRankCriteria copy() {
        return new CronquistRankCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public CronquistTaxonomicRankFilter getRank() {
        return rank;
    }

    public CronquistTaxonomicRankFilter rank() {
        if (rank == null) {
            rank = new CronquistTaxonomicRankFilter();
        }
        return rank;
    }

    public void setRank(CronquistTaxonomicRankFilter rank) {
        this.rank = rank;
    }

    public StringFilter getNom() {
        return nom;
    }

    public StringFilter nom() {
        if (nom == null) {
            nom = new StringFilter();
        }
        return nom;
    }

    public void setNom(StringFilter nom) {
        this.nom = nom;
    }

    public LongFilter getChildrenId() {
        return childrenId;
    }

    public LongFilter childrenId() {
        if (childrenId == null) {
            childrenId = new LongFilter();
        }
        return childrenId;
    }

    public void setChildrenId(LongFilter childrenId) {
        this.childrenId = childrenId;
    }

    public LongFilter getParentId() {
        return parentId;
    }

    public LongFilter parentId() {
        if (parentId == null) {
            parentId = new LongFilter();
        }
        return parentId;
    }

    public void setParentId(LongFilter parentId) {
        this.parentId = parentId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CronquistRankCriteria that = (CronquistRankCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(rank, that.rank) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(childrenId, that.childrenId) &&
            Objects.equals(parentId, that.parentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rank, nom, childrenId, parentId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CronquistRankCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (rank != null ? "rank=" + rank + ", " : "") +
            (nom != null ? "nom=" + nom + ", " : "") +
            (childrenId != null ? "childrenId=" + childrenId + ", " : "") +
            (parentId != null ? "parentId=" + parentId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
