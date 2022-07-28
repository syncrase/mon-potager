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
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.CycleDeVie} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.CycleDeVieResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cycle-de-vies?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class CycleDeVieCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter semisId;

    private LongFilter apparitionFeuillesId;

    private LongFilter floraisonId;

    private LongFilter recolteId;

    private LongFilter croissanceId;

    private LongFilter maturiteId;

    private LongFilter plantationId;

    private LongFilter rempotageId;

    private LongFilter reproductionId;

    private Boolean distinct;

    public CycleDeVieCriteria() {}

    public CycleDeVieCriteria(CycleDeVieCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.semisId = other.semisId == null ? null : other.semisId.copy();
        this.apparitionFeuillesId = other.apparitionFeuillesId == null ? null : other.apparitionFeuillesId.copy();
        this.floraisonId = other.floraisonId == null ? null : other.floraisonId.copy();
        this.recolteId = other.recolteId == null ? null : other.recolteId.copy();
        this.croissanceId = other.croissanceId == null ? null : other.croissanceId.copy();
        this.maturiteId = other.maturiteId == null ? null : other.maturiteId.copy();
        this.plantationId = other.plantationId == null ? null : other.plantationId.copy();
        this.rempotageId = other.rempotageId == null ? null : other.rempotageId.copy();
        this.reproductionId = other.reproductionId == null ? null : other.reproductionId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public CycleDeVieCriteria copy() {
        return new CycleDeVieCriteria(this);
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

    public LongFilter getSemisId() {
        return semisId;
    }

    public LongFilter semisId() {
        if (semisId == null) {
            semisId = new LongFilter();
        }
        return semisId;
    }

    public void setSemisId(LongFilter semisId) {
        this.semisId = semisId;
    }

    public LongFilter getApparitionFeuillesId() {
        return apparitionFeuillesId;
    }

    public LongFilter apparitionFeuillesId() {
        if (apparitionFeuillesId == null) {
            apparitionFeuillesId = new LongFilter();
        }
        return apparitionFeuillesId;
    }

    public void setApparitionFeuillesId(LongFilter apparitionFeuillesId) {
        this.apparitionFeuillesId = apparitionFeuillesId;
    }

    public LongFilter getFloraisonId() {
        return floraisonId;
    }

    public LongFilter floraisonId() {
        if (floraisonId == null) {
            floraisonId = new LongFilter();
        }
        return floraisonId;
    }

    public void setFloraisonId(LongFilter floraisonId) {
        this.floraisonId = floraisonId;
    }

    public LongFilter getRecolteId() {
        return recolteId;
    }

    public LongFilter recolteId() {
        if (recolteId == null) {
            recolteId = new LongFilter();
        }
        return recolteId;
    }

    public void setRecolteId(LongFilter recolteId) {
        this.recolteId = recolteId;
    }

    public LongFilter getCroissanceId() {
        return croissanceId;
    }

    public LongFilter croissanceId() {
        if (croissanceId == null) {
            croissanceId = new LongFilter();
        }
        return croissanceId;
    }

    public void setCroissanceId(LongFilter croissanceId) {
        this.croissanceId = croissanceId;
    }

    public LongFilter getMaturiteId() {
        return maturiteId;
    }

    public LongFilter maturiteId() {
        if (maturiteId == null) {
            maturiteId = new LongFilter();
        }
        return maturiteId;
    }

    public void setMaturiteId(LongFilter maturiteId) {
        this.maturiteId = maturiteId;
    }

    public LongFilter getPlantationId() {
        return plantationId;
    }

    public LongFilter plantationId() {
        if (plantationId == null) {
            plantationId = new LongFilter();
        }
        return plantationId;
    }

    public void setPlantationId(LongFilter plantationId) {
        this.plantationId = plantationId;
    }

    public LongFilter getRempotageId() {
        return rempotageId;
    }

    public LongFilter rempotageId() {
        if (rempotageId == null) {
            rempotageId = new LongFilter();
        }
        return rempotageId;
    }

    public void setRempotageId(LongFilter rempotageId) {
        this.rempotageId = rempotageId;
    }

    public LongFilter getReproductionId() {
        return reproductionId;
    }

    public LongFilter reproductionId() {
        if (reproductionId == null) {
            reproductionId = new LongFilter();
        }
        return reproductionId;
    }

    public void setReproductionId(LongFilter reproductionId) {
        this.reproductionId = reproductionId;
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
        final CycleDeVieCriteria that = (CycleDeVieCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(semisId, that.semisId) &&
            Objects.equals(apparitionFeuillesId, that.apparitionFeuillesId) &&
            Objects.equals(floraisonId, that.floraisonId) &&
            Objects.equals(recolteId, that.recolteId) &&
            Objects.equals(croissanceId, that.croissanceId) &&
            Objects.equals(maturiteId, that.maturiteId) &&
            Objects.equals(plantationId, that.plantationId) &&
            Objects.equals(rempotageId, that.rempotageId) &&
            Objects.equals(reproductionId, that.reproductionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            semisId,
            apparitionFeuillesId,
            floraisonId,
            recolteId,
            croissanceId,
            maturiteId,
            plantationId,
            rempotageId,
            reproductionId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CycleDeVieCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (semisId != null ? "semisId=" + semisId + ", " : "") +
            (apparitionFeuillesId != null ? "apparitionFeuillesId=" + apparitionFeuillesId + ", " : "") +
            (floraisonId != null ? "floraisonId=" + floraisonId + ", " : "") +
            (recolteId != null ? "recolteId=" + recolteId + ", " : "") +
            (croissanceId != null ? "croissanceId=" + croissanceId + ", " : "") +
            (maturiteId != null ? "maturiteId=" + maturiteId + ", " : "") +
            (plantationId != null ? "plantationId=" + plantationId + ", " : "") +
            (rempotageId != null ? "rempotageId=" + rempotageId + ", " : "") +
            (reproductionId != null ? "reproductionId=" + reproductionId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
