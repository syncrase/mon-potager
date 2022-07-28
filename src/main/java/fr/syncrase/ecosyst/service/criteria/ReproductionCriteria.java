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
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.Reproduction} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.ReproductionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /reproductions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class ReproductionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter vitesse;

    private StringFilter type;

    private LongFilter cycleDeVieId;

    private Boolean distinct;

    public ReproductionCriteria() {}

    public ReproductionCriteria(ReproductionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.vitesse = other.vitesse == null ? null : other.vitesse.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.cycleDeVieId = other.cycleDeVieId == null ? null : other.cycleDeVieId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ReproductionCriteria copy() {
        return new ReproductionCriteria(this);
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

    public StringFilter getVitesse() {
        return vitesse;
    }

    public StringFilter vitesse() {
        if (vitesse == null) {
            vitesse = new StringFilter();
        }
        return vitesse;
    }

    public void setVitesse(StringFilter vitesse) {
        this.vitesse = vitesse;
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

    public LongFilter getCycleDeVieId() {
        return cycleDeVieId;
    }

    public LongFilter cycleDeVieId() {
        if (cycleDeVieId == null) {
            cycleDeVieId = new LongFilter();
        }
        return cycleDeVieId;
    }

    public void setCycleDeVieId(LongFilter cycleDeVieId) {
        this.cycleDeVieId = cycleDeVieId;
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
        final ReproductionCriteria that = (ReproductionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(vitesse, that.vitesse) &&
            Objects.equals(type, that.type) &&
            Objects.equals(cycleDeVieId, that.cycleDeVieId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vitesse, type, cycleDeVieId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReproductionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (vitesse != null ? "vitesse=" + vitesse + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (cycleDeVieId != null ? "cycleDeVieId=" + cycleDeVieId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
