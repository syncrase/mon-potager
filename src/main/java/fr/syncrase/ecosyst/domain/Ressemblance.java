package fr.syncrase.ecosyst.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Pour que la ressemblance soit réflexive il faut l'enregistrer 2 fois. Car si la ressemblance A ressemble à B est enregistrée, alors B ne ressemble pas à A
 */
@Schema(
    description = "Pour que la ressemblance soit réflexive il faut l'enregistrer 2 fois. Car si la ressemblance A ressemble à B est enregistrée, alors B ne ressemble pas à A"
)
@Entity
@Table(name = "ressemblance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Ressemblance implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

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
    private Plante planteRessemblant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ressemblance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Ressemblance description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Plante getPlanteRessemblant() {
        return this.planteRessemblant;
    }

    public void setPlanteRessemblant(Plante plante) {
        this.planteRessemblant = plante;
    }

    public Ressemblance planteRessemblant(Plante plante) {
        this.setPlanteRessemblant(plante);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ressemblance)) {
            return false;
        }
        return id != null && id.equals(((Ressemblance) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ressemblance{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
