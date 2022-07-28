package fr.syncrase.ecosyst.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Ensoleillement.
 */
@Entity
@Table(name = "ensoleillement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Ensoleillement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "orientation")
    private String orientation;

    @Column(name = "ensoleilement")
    private Double ensoleilement;

    @ManyToOne
    @JsonIgnoreProperties(
        value = {
            "confusions",
            "ensoleillements",
            "plantesPotageres",
            "cycleDeVie",
            "sol",
            "temperature",
            "racine",
            "strate",
            "feuillage",
            "nomsVernaculaires",
            "planteBotanique",
        },
        allowSetters = true
    )
    private Plante plante;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ensoleillement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrientation() {
        return this.orientation;
    }

    public Ensoleillement orientation(String orientation) {
        this.setOrientation(orientation);
        return this;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public Double getEnsoleilement() {
        return this.ensoleilement;
    }

    public Ensoleillement ensoleilement(Double ensoleilement) {
        this.setEnsoleilement(ensoleilement);
        return this;
    }

    public void setEnsoleilement(Double ensoleilement) {
        this.ensoleilement = ensoleilement;
    }

    public Plante getPlante() {
        return this.plante;
    }

    public void setPlante(Plante plante) {
        this.plante = plante;
    }

    public Ensoleillement plante(Plante plante) {
        this.setPlante(plante);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ensoleillement)) {
            return false;
        }
        return id != null && id.equals(((Ensoleillement) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ensoleillement{" +
            "id=" + getId() +
            ", orientation='" + getOrientation() + "'" +
            ", ensoleilement=" + getEnsoleilement() +
            "}";
    }
}
