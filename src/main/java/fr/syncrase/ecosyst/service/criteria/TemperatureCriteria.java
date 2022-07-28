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
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.Temperature} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.TemperatureResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /temperatures?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class TemperatureCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private DoubleFilter min;

    private DoubleFilter max;

    private StringFilter description;

    private StringFilter rusticite;

    private Boolean distinct;

    public TemperatureCriteria() {}

    public TemperatureCriteria(TemperatureCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.min = other.min == null ? null : other.min.copy();
        this.max = other.max == null ? null : other.max.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.rusticite = other.rusticite == null ? null : other.rusticite.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TemperatureCriteria copy() {
        return new TemperatureCriteria(this);
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

    public DoubleFilter getMin() {
        return min;
    }

    public DoubleFilter min() {
        if (min == null) {
            min = new DoubleFilter();
        }
        return min;
    }

    public void setMin(DoubleFilter min) {
        this.min = min;
    }

    public DoubleFilter getMax() {
        return max;
    }

    public DoubleFilter max() {
        if (max == null) {
            max = new DoubleFilter();
        }
        return max;
    }

    public void setMax(DoubleFilter max) {
        this.max = max;
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

    public StringFilter getRusticite() {
        return rusticite;
    }

    public StringFilter rusticite() {
        if (rusticite == null) {
            rusticite = new StringFilter();
        }
        return rusticite;
    }

    public void setRusticite(StringFilter rusticite) {
        this.rusticite = rusticite;
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
        final TemperatureCriteria that = (TemperatureCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(min, that.min) &&
            Objects.equals(max, that.max) &&
            Objects.equals(description, that.description) &&
            Objects.equals(rusticite, that.rusticite) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, min, max, description, rusticite, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TemperatureCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (min != null ? "min=" + min + ", " : "") +
            (max != null ? "max=" + max + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (rusticite != null ? "rusticite=" + rusticite + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
