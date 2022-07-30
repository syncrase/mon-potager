package fr.syncrase.ecosyst.service.criteria;

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
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.Plante} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.PlanteResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /plantes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class PlanteCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter lowestClassificationRankId;

    private LongFilter nomsVernaculairesId;

    private Boolean distinct;

    public PlanteCriteria() {}

    public PlanteCriteria(PlanteCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.lowestClassificationRankId = other.lowestClassificationRankId == null ? null : other.lowestClassificationRankId.copy();
        this.nomsVernaculairesId = other.nomsVernaculairesId == null ? null : other.nomsVernaculairesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PlanteCriteria copy() {
        return new PlanteCriteria(this);
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

    public LongFilter getLowestClassificationRankId() {
        return lowestClassificationRankId;
    }

    public LongFilter lowestClassificationRankId() {
        if (lowestClassificationRankId == null) {
            lowestClassificationRankId = new LongFilter();
        }
        return lowestClassificationRankId;
    }

    public void setLowestClassificationRankId(LongFilter lowestClassificationRankId) {
        this.lowestClassificationRankId = lowestClassificationRankId;
    }

    public LongFilter getNomsVernaculairesId() {
        return nomsVernaculairesId;
    }

    public LongFilter nomsVernaculairesId() {
        if (nomsVernaculairesId == null) {
            nomsVernaculairesId = new LongFilter();
        }
        return nomsVernaculairesId;
    }

    public void setNomsVernaculairesId(LongFilter nomsVernaculairesId) {
        this.nomsVernaculairesId = nomsVernaculairesId;
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
        final PlanteCriteria that = (PlanteCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(lowestClassificationRankId, that.lowestClassificationRankId) &&
            Objects.equals(nomsVernaculairesId, that.nomsVernaculairesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lowestClassificationRankId, nomsVernaculairesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlanteCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (lowestClassificationRankId != null ? "lowestClassificationRankId=" + lowestClassificationRankId + ", " : "") +
            (nomsVernaculairesId != null ? "nomsVernaculairesId=" + nomsVernaculairesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
