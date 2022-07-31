package fr.syncrase.ecosyst.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A NomVernaculaire.
 */
@Entity
@Table(name = "nom_vernaculaire")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NomVernaculaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "nom", unique = true)
    private String nom;

    @ManyToMany(mappedBy = "nomsVernaculaires")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "cronquistRank", "nomsVernaculaires" }, allowSetters = true)
    private Set<Plante> plantes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public NomVernaculaire id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public NomVernaculaire nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Set<Plante> getPlantes() {
        return this.plantes;
    }

    public void setPlantes(Set<Plante> plantes) {
        if (this.plantes != null) {
            this.plantes.forEach(i -> i.removeNomsVernaculaires(this));
        }
        if (plantes != null) {
            plantes.forEach(i -> i.addNomsVernaculaires(this));
        }
        this.plantes = plantes;
    }

    public NomVernaculaire plantes(Set<Plante> plantes) {
        this.setPlantes(plantes);
        return this;
    }

    public NomVernaculaire addPlantes(Plante plante) {
        this.plantes.add(plante);
        plante.getNomsVernaculaires().add(this);
        return this;
    }

    public NomVernaculaire removePlantes(Plante plante) {
        this.plantes.remove(plante);
        plante.getNomsVernaculaires().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NomVernaculaire)) {
            return false;
        }
        return id != null && id.equals(((NomVernaculaire) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NomVernaculaire{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            "}";
    }
}
