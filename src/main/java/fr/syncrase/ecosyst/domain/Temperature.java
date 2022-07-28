package fr.syncrase.ecosyst.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Temperature.
 */
@Entity
@Table(name = "temperature")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Temperature implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "min")
    private Double min;

    @Column(name = "max")
    private Double max;

    @Column(name = "description")
    private String description;

    @Column(name = "rusticite")
    private String rusticite;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Temperature id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMin() {
        return this.min;
    }

    public Temperature min(Double min) {
        this.setMin(min);
        return this;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return this.max;
    }

    public Temperature max(Double max) {
        this.setMax(max);
        return this;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public String getDescription() {
        return this.description;
    }

    public Temperature description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRusticite() {
        return this.rusticite;
    }

    public Temperature rusticite(String rusticite) {
        this.setRusticite(rusticite);
        return this;
    }

    public void setRusticite(String rusticite) {
        this.rusticite = rusticite;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Temperature)) {
            return false;
        }
        return id != null && id.equals(((Temperature) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Temperature{" +
            "id=" + getId() +
            ", min=" + getMin() +
            ", max=" + getMax() +
            ", description='" + getDescription() + "'" +
            ", rusticite='" + getRusticite() + "'" +
            "}";
    }
}
