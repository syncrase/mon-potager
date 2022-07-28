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
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.Sol} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.SolResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sols?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class SolCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private DoubleFilter phMin;

    private DoubleFilter phMax;

    private StringFilter type;

    private StringFilter richesse;

    private Boolean distinct;

    public SolCriteria() {}

    public SolCriteria(SolCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.phMin = other.phMin == null ? null : other.phMin.copy();
        this.phMax = other.phMax == null ? null : other.phMax.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.richesse = other.richesse == null ? null : other.richesse.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SolCriteria copy() {
        return new SolCriteria(this);
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

    public DoubleFilter getPhMin() {
        return phMin;
    }

    public DoubleFilter phMin() {
        if (phMin == null) {
            phMin = new DoubleFilter();
        }
        return phMin;
    }

    public void setPhMin(DoubleFilter phMin) {
        this.phMin = phMin;
    }

    public DoubleFilter getPhMax() {
        return phMax;
    }

    public DoubleFilter phMax() {
        if (phMax == null) {
            phMax = new DoubleFilter();
        }
        return phMax;
    }

    public void setPhMax(DoubleFilter phMax) {
        this.phMax = phMax;
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

    public StringFilter getRichesse() {
        return richesse;
    }

    public StringFilter richesse() {
        if (richesse == null) {
            richesse = new StringFilter();
        }
        return richesse;
    }

    public void setRichesse(StringFilter richesse) {
        this.richesse = richesse;
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
        final SolCriteria that = (SolCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(phMin, that.phMin) &&
            Objects.equals(phMax, that.phMax) &&
            Objects.equals(type, that.type) &&
            Objects.equals(richesse, that.richesse) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phMin, phMax, type, richesse, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SolCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (phMin != null ? "phMin=" + phMin + ", " : "") +
            (phMax != null ? "phMax=" + phMax + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (richesse != null ? "richesse=" + richesse + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
