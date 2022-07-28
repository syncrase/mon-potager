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
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.Germination} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.GerminationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /germinations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class GerminationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter tempsDeGermination;

    private StringFilter conditionDeGermination;

    private Boolean distinct;

    public GerminationCriteria() {}

    public GerminationCriteria(GerminationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.tempsDeGermination = other.tempsDeGermination == null ? null : other.tempsDeGermination.copy();
        this.conditionDeGermination = other.conditionDeGermination == null ? null : other.conditionDeGermination.copy();
        this.distinct = other.distinct;
    }

    @Override
    public GerminationCriteria copy() {
        return new GerminationCriteria(this);
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

    public StringFilter getTempsDeGermination() {
        return tempsDeGermination;
    }

    public StringFilter tempsDeGermination() {
        if (tempsDeGermination == null) {
            tempsDeGermination = new StringFilter();
        }
        return tempsDeGermination;
    }

    public void setTempsDeGermination(StringFilter tempsDeGermination) {
        this.tempsDeGermination = tempsDeGermination;
    }

    public StringFilter getConditionDeGermination() {
        return conditionDeGermination;
    }

    public StringFilter conditionDeGermination() {
        if (conditionDeGermination == null) {
            conditionDeGermination = new StringFilter();
        }
        return conditionDeGermination;
    }

    public void setConditionDeGermination(StringFilter conditionDeGermination) {
        this.conditionDeGermination = conditionDeGermination;
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
        final GerminationCriteria that = (GerminationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(tempsDeGermination, that.tempsDeGermination) &&
            Objects.equals(conditionDeGermination, that.conditionDeGermination) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tempsDeGermination, conditionDeGermination, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GerminationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (tempsDeGermination != null ? "tempsDeGermination=" + tempsDeGermination + ", " : "") +
            (conditionDeGermination != null ? "conditionDeGermination=" + conditionDeGermination + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
