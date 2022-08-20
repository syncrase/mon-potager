package fr.syncrase.ecosyst.service.criteria;

import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the {@link fr.syncrase.ecosyst.domain.Classification} entity. This class is used
 * in {@link fr.syncrase.ecosyst.web.rest.ClassificationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /classifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class ClassificationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter cronquistId;

    private LongFilter apgId;

    private LongFilter benthamHookerId;

    private LongFilter wettsteinId;

    private LongFilter thorneId;

    private LongFilter takhtajanId;

    private LongFilter englerId;

    private LongFilter candolleId;

    private LongFilter dahlgrenId;

    private LongFilter plantesId;

    private Boolean distinct;

    public ClassificationCriteria() {}

    public ClassificationCriteria(ClassificationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.cronquistId = other.cronquistId == null ? null : other.cronquistId.copy();
        this.apgId = other.apgId == null ? null : other.apgId.copy();
        this.benthamHookerId = other.benthamHookerId == null ? null : other.benthamHookerId.copy();
        this.wettsteinId = other.wettsteinId == null ? null : other.wettsteinId.copy();
        this.thorneId = other.thorneId == null ? null : other.thorneId.copy();
        this.takhtajanId = other.takhtajanId == null ? null : other.takhtajanId.copy();
        this.englerId = other.englerId == null ? null : other.englerId.copy();
        this.candolleId = other.candolleId == null ? null : other.candolleId.copy();
        this.dahlgrenId = other.dahlgrenId == null ? null : other.dahlgrenId.copy();
        this.plantesId = other.plantesId == null ? null : other.plantesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ClassificationCriteria copy() {
        return new ClassificationCriteria(this);
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

    public LongFilter getCronquistId() {
        return cronquistId;
    }

    public LongFilter cronquistId() {
        if (cronquistId == null) {
            cronquistId = new LongFilter();
        }
        return cronquistId;
    }

    public void setCronquistId(LongFilter cronquistId) {
        this.cronquistId = cronquistId;
    }

    public LongFilter getApgId() {
        return apgId;
    }

    public LongFilter apgId() {
        if (apgId == null) {
            apgId = new LongFilter();
        }
        return apgId;
    }

    public void setApgId(LongFilter apgId) {
        this.apgId = apgId;
    }

    public LongFilter getBenthamHookerId() {
        return benthamHookerId;
    }

    public LongFilter benthamHookerId() {
        if (benthamHookerId == null) {
            benthamHookerId = new LongFilter();
        }
        return benthamHookerId;
    }

    public void setBenthamHookerId(LongFilter benthamHookerId) {
        this.benthamHookerId = benthamHookerId;
    }

    public LongFilter getWettsteinId() {
        return wettsteinId;
    }

    public LongFilter wettsteinId() {
        if (wettsteinId == null) {
            wettsteinId = new LongFilter();
        }
        return wettsteinId;
    }

    public void setWettsteinId(LongFilter wettsteinId) {
        this.wettsteinId = wettsteinId;
    }

    public LongFilter getThorneId() {
        return thorneId;
    }

    public LongFilter thorneId() {
        if (thorneId == null) {
            thorneId = new LongFilter();
        }
        return thorneId;
    }

    public void setThorneId(LongFilter thorneId) {
        this.thorneId = thorneId;
    }

    public LongFilter getTakhtajanId() {
        return takhtajanId;
    }

    public LongFilter takhtajanId() {
        if (takhtajanId == null) {
            takhtajanId = new LongFilter();
        }
        return takhtajanId;
    }

    public void setTakhtajanId(LongFilter takhtajanId) {
        this.takhtajanId = takhtajanId;
    }

    public LongFilter getEnglerId() {
        return englerId;
    }

    public LongFilter englerId() {
        if (englerId == null) {
            englerId = new LongFilter();
        }
        return englerId;
    }

    public void setEnglerId(LongFilter englerId) {
        this.englerId = englerId;
    }

    public LongFilter getCandolleId() {
        return candolleId;
    }

    public LongFilter candolleId() {
        if (candolleId == null) {
            candolleId = new LongFilter();
        }
        return candolleId;
    }

    public void setCandolleId(LongFilter candolleId) {
        this.candolleId = candolleId;
    }

    public LongFilter getDahlgrenId() {
        return dahlgrenId;
    }

    public LongFilter dahlgrenId() {
        if (dahlgrenId == null) {
            dahlgrenId = new LongFilter();
        }
        return dahlgrenId;
    }

    public void setDahlgrenId(LongFilter dahlgrenId) {
        this.dahlgrenId = dahlgrenId;
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
        final ClassificationCriteria that = (ClassificationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(cronquistId, that.cronquistId) &&
            Objects.equals(apgId, that.apgId) &&
            Objects.equals(benthamHookerId, that.benthamHookerId) &&
            Objects.equals(wettsteinId, that.wettsteinId) &&
            Objects.equals(thorneId, that.thorneId) &&
            Objects.equals(takhtajanId, that.takhtajanId) &&
            Objects.equals(englerId, that.englerId) &&
            Objects.equals(candolleId, that.candolleId) &&
            Objects.equals(dahlgrenId, that.dahlgrenId) &&
            Objects.equals(plantesId, that.plantesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            cronquistId,
            apgId,
            benthamHookerId,
            wettsteinId,
            thorneId,
            takhtajanId,
            englerId,
            candolleId,
            dahlgrenId,
            plantesId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClassificationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (cronquistId != null ? "cronquistId=" + cronquistId + ", " : "") +
            (apgId != null ? "apgId=" + apgId + ", " : "") +
            (benthamHookerId != null ? "benthamHookerId=" + benthamHookerId + ", " : "") +
            (wettsteinId != null ? "wettsteinId=" + wettsteinId + ", " : "") +
            (thorneId != null ? "thorneId=" + thorneId + ", " : "") +
            (takhtajanId != null ? "takhtajanId=" + takhtajanId + ", " : "") +
            (englerId != null ? "englerId=" + englerId + ", " : "") +
            (candolleId != null ? "candolleId=" + candolleId + ", " : "") +
            (dahlgrenId != null ? "dahlgrenId=" + dahlgrenId + ", " : "") +
            (plantesId != null ? "plantesId=" + plantesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
