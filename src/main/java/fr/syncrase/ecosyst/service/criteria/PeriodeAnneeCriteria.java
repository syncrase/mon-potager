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
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.PeriodeAnnee} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.PeriodeAnneeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /periode-annees?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class PeriodeAnneeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter debutId;

    private LongFilter finId;

    private Boolean distinct;

    public PeriodeAnneeCriteria() {}

    public PeriodeAnneeCriteria(PeriodeAnneeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.debutId = other.debutId == null ? null : other.debutId.copy();
        this.finId = other.finId == null ? null : other.finId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PeriodeAnneeCriteria copy() {
        return new PeriodeAnneeCriteria(this);
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

    public LongFilter getDebutId() {
        return debutId;
    }

    public LongFilter debutId() {
        if (debutId == null) {
            debutId = new LongFilter();
        }
        return debutId;
    }

    public void setDebutId(LongFilter debutId) {
        this.debutId = debutId;
    }

    public LongFilter getFinId() {
        return finId;
    }

    public LongFilter finId() {
        if (finId == null) {
            finId = new LongFilter();
        }
        return finId;
    }

    public void setFinId(LongFilter finId) {
        this.finId = finId;
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
        final PeriodeAnneeCriteria that = (PeriodeAnneeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(debutId, that.debutId) &&
            Objects.equals(finId, that.finId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, debutId, finId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PeriodeAnneeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (debutId != null ? "debutId=" + debutId + ", " : "") +
            (finId != null ? "finId=" + finId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
