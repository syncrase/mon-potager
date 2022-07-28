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
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.Ensoleillement} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.EnsoleillementResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ensoleillements?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class EnsoleillementCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter orientation;

    private DoubleFilter ensoleilement;

    private LongFilter planteId;

    private Boolean distinct;

    public EnsoleillementCriteria() {}

    public EnsoleillementCriteria(EnsoleillementCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.orientation = other.orientation == null ? null : other.orientation.copy();
        this.ensoleilement = other.ensoleilement == null ? null : other.ensoleilement.copy();
        this.planteId = other.planteId == null ? null : other.planteId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public EnsoleillementCriteria copy() {
        return new EnsoleillementCriteria(this);
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

    public StringFilter getOrientation() {
        return orientation;
    }

    public StringFilter orientation() {
        if (orientation == null) {
            orientation = new StringFilter();
        }
        return orientation;
    }

    public void setOrientation(StringFilter orientation) {
        this.orientation = orientation;
    }

    public DoubleFilter getEnsoleilement() {
        return ensoleilement;
    }

    public DoubleFilter ensoleilement() {
        if (ensoleilement == null) {
            ensoleilement = new DoubleFilter();
        }
        return ensoleilement;
    }

    public void setEnsoleilement(DoubleFilter ensoleilement) {
        this.ensoleilement = ensoleilement;
    }

    public LongFilter getPlanteId() {
        return planteId;
    }

    public LongFilter planteId() {
        if (planteId == null) {
            planteId = new LongFilter();
        }
        return planteId;
    }

    public void setPlanteId(LongFilter planteId) {
        this.planteId = planteId;
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
        final EnsoleillementCriteria that = (EnsoleillementCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(orientation, that.orientation) &&
            Objects.equals(ensoleilement, that.ensoleilement) &&
            Objects.equals(planteId, that.planteId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orientation, ensoleilement, planteId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnsoleillementCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (orientation != null ? "orientation=" + orientation + ", " : "") +
            (ensoleilement != null ? "ensoleilement=" + ensoleilement + ", " : "") +
            (planteId != null ? "planteId=" + planteId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
