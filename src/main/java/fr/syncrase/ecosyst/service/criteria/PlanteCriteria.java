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
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.Plante} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.PlanteResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /plantes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class PlanteCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter classificationId;

    private LongFilter nomsVernaculairesId;

    private LongFilter referencesId;

    private Boolean distinct;

    public PlanteCriteria() {}

    public PlanteCriteria(PlanteCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.classificationId = other.classificationId == null ? null : other.classificationId.copy();
        this.nomsVernaculairesId = other.nomsVernaculairesId == null ? null : other.nomsVernaculairesId.copy();
        this.referencesId = other.referencesId == null ? null : other.referencesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PlanteCriteria copy() {
        return new PlanteCriteria(this);
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

    public LongFilter getNomsVernaculairesId() {
        return nomsVernaculairesId;
    }

    public LongFilter nomsVernaculairesId() {
        if (nomsVernaculairesId == null) {
            nomsVernaculairesId = new LongFilter();
        }
        return nomsVernaculairesId;
    }

    public void setNomsVernaculairesId(LongFilter nomsVernaculairesId) {
        this.nomsVernaculairesId = nomsVernaculairesId;
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
        final PlanteCriteria that = (PlanteCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(classificationId, that.classificationId) &&
            Objects.equals(nomsVernaculairesId, that.nomsVernaculairesId) &&
            Objects.equals(referencesId, that.referencesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, classificationId, nomsVernaculairesId, referencesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlanteCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (classificationId != null ? "classificationId=" + classificationId + ", " : "") +
            (nomsVernaculairesId != null ? "nomsVernaculairesId=" + nomsVernaculairesId + ", " : "") +
            (referencesId != null ? "referencesId=" + referencesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
