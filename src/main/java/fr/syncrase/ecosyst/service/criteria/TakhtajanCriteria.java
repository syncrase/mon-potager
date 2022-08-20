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
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.Takhtajan} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.TakhtajanResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /takhtajans?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class TakhtajanCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter classificationId;

    private Boolean distinct;

    public TakhtajanCriteria() {}

    public TakhtajanCriteria(TakhtajanCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.classificationId = other.classificationId == null ? null : other.classificationId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TakhtajanCriteria copy() {
        return new TakhtajanCriteria(this);
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

    public LongFilter getClassificationId() {
        return classificationId;
    }

    public LongFilter classificationId() {
        if (classificationId == null) {
            classificationId = new LongFilter();
        }
        return classificationId;
    }

    public void setClassificationId(LongFilter classificationId) {
        this.classificationId = classificationId;
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
        final TakhtajanCriteria that = (TakhtajanCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(classificationId, that.classificationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, classificationId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TakhtajanCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (classificationId != null ? "classificationId=" + classificationId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
