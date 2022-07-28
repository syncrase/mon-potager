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
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.Allelopathie} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.AllelopathieResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /allelopathies?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class AllelopathieCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter type;

    private StringFilter description;

    private IntegerFilter impact;

    private LongFilter cibleId;

    private LongFilter origineId;

    private Boolean distinct;

    public AllelopathieCriteria() {}

    public AllelopathieCriteria(AllelopathieCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.impact = other.impact == null ? null : other.impact.copy();
        this.cibleId = other.cibleId == null ? null : other.cibleId.copy();
        this.origineId = other.origineId == null ? null : other.origineId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public AllelopathieCriteria copy() {
        return new AllelopathieCriteria(this);
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

    public StringFilter getType() {
        return type;
    }

    public StringFilter type() {
        if (type == null) {
            type = new StringFilter();
        }
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public IntegerFilter getImpact() {
        return impact;
    }

    public IntegerFilter impact() {
        if (impact == null) {
            impact = new IntegerFilter();
        }
        return impact;
    }

    public void setImpact(IntegerFilter impact) {
        this.impact = impact;
    }

    public LongFilter getCibleId() {
        return cibleId;
    }

    public LongFilter cibleId() {
        if (cibleId == null) {
            cibleId = new LongFilter();
        }
        return cibleId;
    }

    public void setCibleId(LongFilter cibleId) {
        this.cibleId = cibleId;
    }

    public LongFilter getOrigineId() {
        return origineId;
    }

    public LongFilter origineId() {
        if (origineId == null) {
            origineId = new LongFilter();
        }
        return origineId;
    }

    public void setOrigineId(LongFilter origineId) {
        this.origineId = origineId;
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
        final AllelopathieCriteria that = (AllelopathieCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(description, that.description) &&
            Objects.equals(impact, that.impact) &&
            Objects.equals(cibleId, that.cibleId) &&
            Objects.equals(origineId, that.origineId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, description, impact, cibleId, origineId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AllelopathieCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (impact != null ? "impact=" + impact + ", " : "") +
            (cibleId != null ? "cibleId=" + cibleId + ", " : "") +
            (origineId != null ? "origineId=" + origineId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
