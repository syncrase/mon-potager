package fr.syncrase.ecosyst.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Sol.
 */
@Entity
@Table(name = "sol")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Sol implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "ph_min")
    private Double phMin;

    @Column(name = "ph_max")
    private Double phMax;

    @Column(name = "type")
    private String type;

    @Column(name = "richesse")
    private String richesse;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Sol id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPhMin() {
        return this.phMin;
    }

    public Sol phMin(Double phMin) {
        this.setPhMin(phMin);
        return this;
    }

    public void setPhMin(Double phMin) {
        this.phMin = phMin;
    }

    public Double getPhMax() {
        return this.phMax;
    }

    public Sol phMax(Double phMax) {
        this.setPhMax(phMax);
        return this;
    }

    public void setPhMax(Double phMax) {
        this.phMax = phMax;
    }

    public String getType() {
        return this.type;
    }

    public Sol type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRichesse() {
        return this.richesse;
    }

    public Sol richesse(String richesse) {
        this.setRichesse(richesse);
        return this;
    }

    public void setRichesse(String richesse) {
        this.richesse = richesse;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sol)) {
            return false;
        }
        return id != null && id.equals(((Sol) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sol{" +
            "id=" + getId() +
            ", phMin=" + getPhMin() +
            ", phMax=" + getPhMax() +
            ", type='" + getType() + "'" +
            ", richesse='" + getRichesse() + "'" +
            "}";
    }
}
