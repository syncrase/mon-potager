package fr.syncrase.ecosyst.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Reproduction.
 */
@Entity
@Table(name = "reproduction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Reproduction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "vitesse")
    private String vitesse;

    @Column(name = "type")
    private String type;

    @OneToMany(mappedBy = "reproduction")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = {
            "semis", "apparitionFeuilles", "floraison", "recolte", "croissance", "maturite", "plantation", "rempotage", "reproduction",
        },
        allowSetters = true
    )
    private Set<CycleDeVie> cycleDeVies = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Reproduction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVitesse() {
        return this.vitesse;
    }

    public Reproduction vitesse(String vitesse) {
        this.setVitesse(vitesse);
        return this;
    }

    public void setVitesse(String vitesse) {
        this.vitesse = vitesse;
    }

    public String getType() {
        return this.type;
    }

    public Reproduction type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<CycleDeVie> getCycleDeVies() {
        return this.cycleDeVies;
    }

    public void setCycleDeVies(Set<CycleDeVie> cycleDeVies) {
        if (this.cycleDeVies != null) {
            this.cycleDeVies.forEach(i -> i.setReproduction(null));
        }
        if (cycleDeVies != null) {
            cycleDeVies.forEach(i -> i.setReproduction(this));
        }
        this.cycleDeVies = cycleDeVies;
    }

    public Reproduction cycleDeVies(Set<CycleDeVie> cycleDeVies) {
        this.setCycleDeVies(cycleDeVies);
        return this;
    }

    public Reproduction addCycleDeVie(CycleDeVie cycleDeVie) {
        this.cycleDeVies.add(cycleDeVie);
        cycleDeVie.setReproduction(this);
        return this;
    }

    public Reproduction removeCycleDeVie(CycleDeVie cycleDeVie) {
        this.cycleDeVies.remove(cycleDeVie);
        cycleDeVie.setReproduction(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reproduction)) {
            return false;
        }
        return id != null && id.equals(((Reproduction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reproduction{" +
            "id=" + getId() +
            ", vitesse='" + getVitesse() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
