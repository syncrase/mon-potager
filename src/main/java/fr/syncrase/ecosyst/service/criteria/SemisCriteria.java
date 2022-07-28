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
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.Semis} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.SemisResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /semis?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class SemisCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter semisPleineTerreId;

    private LongFilter semisSousAbrisId;

    private LongFilter typeSemisId;

    private LongFilter germinationId;

    private Boolean distinct;

    public SemisCriteria() {}

    public SemisCriteria(SemisCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.semisPleineTerreId = other.semisPleineTerreId == null ? null : other.semisPleineTerreId.copy();
        this.semisSousAbrisId = other.semisSousAbrisId == null ? null : other.semisSousAbrisId.copy();
        this.typeSemisId = other.typeSemisId == null ? null : other.typeSemisId.copy();
        this.germinationId = other.germinationId == null ? null : other.germinationId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SemisCriteria copy() {
        return new SemisCriteria(this);
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

    public LongFilter getSemisPleineTerreId() {
        return semisPleineTerreId;
    }

    public LongFilter semisPleineTerreId() {
        if (semisPleineTerreId == null) {
            semisPleineTerreId = new LongFilter();
        }
        return semisPleineTerreId;
    }

    public void setSemisPleineTerreId(LongFilter semisPleineTerreId) {
        this.semisPleineTerreId = semisPleineTerreId;
    }

    public LongFilter getSemisSousAbrisId() {
        return semisSousAbrisId;
    }

    public LongFilter semisSousAbrisId() {
        if (semisSousAbrisId == null) {
            semisSousAbrisId = new LongFilter();
        }
        return semisSousAbrisId;
    }

    public void setSemisSousAbrisId(LongFilter semisSousAbrisId) {
        this.semisSousAbrisId = semisSousAbrisId;
    }

    public LongFilter getTypeSemisId() {
        return typeSemisId;
    }

    public LongFilter typeSemisId() {
        if (typeSemisId == null) {
            typeSemisId = new LongFilter();
        }
        return typeSemisId;
    }

    public void setTypeSemisId(LongFilter typeSemisId) {
        this.typeSemisId = typeSemisId;
    }

    public LongFilter getGerminationId() {
        return germinationId;
    }

    public LongFilter germinationId() {
        if (germinationId == null) {
            germinationId = new LongFilter();
        }
        return germinationId;
    }

    public void setGerminationId(LongFilter germinationId) {
        this.germinationId = germinationId;
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
        final SemisCriteria that = (SemisCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(semisPleineTerreId, that.semisPleineTerreId) &&
            Objects.equals(semisSousAbrisId, that.semisSousAbrisId) &&
            Objects.equals(typeSemisId, that.typeSemisId) &&
            Objects.equals(germinationId, that.germinationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, semisPleineTerreId, semisSousAbrisId, typeSemisId, germinationId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SemisCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (semisPleineTerreId != null ? "semisPleineTerreId=" + semisPleineTerreId + ", " : "") +
            (semisSousAbrisId != null ? "semisSousAbrisId=" + semisSousAbrisId + ", " : "") +
            (typeSemisId != null ? "typeSemisId=" + typeSemisId + ", " : "") +
            (germinationId != null ? "germinationId=" + germinationId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
