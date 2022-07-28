package fr.syncrase.ecosyst.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Germination.
 */
@Entity
@Table(name = "germination")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Germination implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "temps_de_germination")
    private String tempsDeGermination;

    @Column(name = "condition_de_germination")
    private String conditionDeGermination;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Germination id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTempsDeGermination() {
        return this.tempsDeGermination;
    }

    public Germination tempsDeGermination(String tempsDeGermination) {
        this.setTempsDeGermination(tempsDeGermination);
        return this;
    }

    public void setTempsDeGermination(String tempsDeGermination) {
        this.tempsDeGermination = tempsDeGermination;
    }

    public String getConditionDeGermination() {
        return this.conditionDeGermination;
    }

    public Germination conditionDeGermination(String conditionDeGermination) {
        this.setConditionDeGermination(conditionDeGermination);
        return this;
    }

    public void setConditionDeGermination(String conditionDeGermination) {
        this.conditionDeGermination = conditionDeGermination;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Germination)) {
            return false;
        }
        return id != null && id.equals(((Germination) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Germination{" +
            "id=" + getId() +
            ", tempsDeGermination='" + getTempsDeGermination() + "'" +
            ", conditionDeGermination='" + getConditionDeGermination() + "'" +
            "}";
    }
}
