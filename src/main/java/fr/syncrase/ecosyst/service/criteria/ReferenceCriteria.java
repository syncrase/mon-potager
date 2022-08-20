package fr.syncrase.ecosyst.service.criteria;

import fr.syncrase.ecosyst.domain.enumeration.ReferenceType;
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
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.Reference} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.ReferenceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /references?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class ReferenceCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ReferenceType
     */
    public static class ReferenceTypeFilter extends Filter<ReferenceType> {

        public ReferenceTypeFilter() {}

        public ReferenceTypeFilter(ReferenceTypeFilter filter) {
            super(filter);
        }

        @Override
        public ReferenceTypeFilter copy() {
            return new ReferenceTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter description;

    private ReferenceTypeFilter type;

    private LongFilter urlId;

    private LongFilter plantesId;

    private Boolean distinct;

    public ReferenceCriteria() {}

    public ReferenceCriteria(ReferenceCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.urlId = other.urlId == null ? null : other.urlId.copy();
        this.plantesId = other.plantesId == null ? null : other.plantesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ReferenceCriteria copy() {
        return new ReferenceCriteria(this);
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

    public ReferenceTypeFilter getType() {
        return type;
    }

    public ReferenceTypeFilter type() {
        if (type == null) {
            type = new ReferenceTypeFilter();
        }
        return type;
    }

    public void setType(ReferenceTypeFilter type) {
        this.type = type;
    }

    public LongFilter getUrlId() {
        return urlId;
    }

    public LongFilter urlId() {
        if (urlId == null) {
            urlId = new LongFilter();
        }
        return urlId;
    }

    public void setUrlId(LongFilter urlId) {
        this.urlId = urlId;
    }

    public LongFilter getPlantesId() {
        return plantesId;
    }

    public LongFilter plantesId() {
        if (plantesId == null) {
            plantesId = new LongFilter();
        }
        return plantesId;
    }

    public void setPlantesId(LongFilter plantesId) {
        this.plantesId = plantesId;
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
        final ReferenceCriteria that = (ReferenceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(type, that.type) &&
            Objects.equals(urlId, that.urlId) &&
            Objects.equals(plantesId, that.plantesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, type, urlId, plantesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReferenceCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (urlId != null ? "urlId=" + urlId + ", " : "") +
            (plantesId != null ? "plantesId=" + plantesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
