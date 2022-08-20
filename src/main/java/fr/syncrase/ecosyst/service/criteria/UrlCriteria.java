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
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.Url} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.UrlResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /urls?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class UrlCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter url;

    private LongFilter referencesId;

    private Boolean distinct;

    public UrlCriteria() {}

    public UrlCriteria(UrlCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.url = other.url == null ? null : other.url.copy();
        this.referencesId = other.referencesId == null ? null : other.referencesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public UrlCriteria copy() {
        return new UrlCriteria(this);
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

    public StringFilter getUrl() {
        return url;
    }

    public StringFilter url() {
        if (url == null) {
            url = new StringFilter();
        }
        return url;
    }

    public void setUrl(StringFilter url) {
        this.url = url;
    }

    public LongFilter getReferencesId() {
        return referencesId;
    }

    public LongFilter referencesId() {
        if (referencesId == null) {
            referencesId = new LongFilter();
        }
        return referencesId;
    }

    public void setReferencesId(LongFilter referencesId) {
        this.referencesId = referencesId;
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
        final UrlCriteria that = (UrlCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(url, that.url) &&
            Objects.equals(referencesId, that.referencesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, referencesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UrlCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (url != null ? "url=" + url + ", " : "") +
            (referencesId != null ? "referencesId=" + referencesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
