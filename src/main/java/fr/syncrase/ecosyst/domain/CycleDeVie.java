package fr.syncrase.ecosyst.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CycleDeVie.
 */
@Entity
@Table(name = "cycle_de_vie")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CycleDeVie implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties(value = { "semisPleineTerre", "semisSousAbris", "typeSemis", "germination" }, allowSetters = true)
    private Semis semis;

    @ManyToOne
    @JsonIgnoreProperties(value = { "debut", "fin" }, allowSetters = true)
    private PeriodeAnnee apparitionFeuilles;

    @ManyToOne
    @JsonIgnoreProperties(value = { "debut", "fin" }, allowSetters = true)
    private PeriodeAnnee floraison;

    @ManyToOne
    @JsonIgnoreProperties(value = { "debut", "fin" }, allowSetters = true)
    private PeriodeAnnee recolte;

    @ManyToOne
    @JsonIgnoreProperties(value = { "debut", "fin" }, allowSetters = true)
    private PeriodeAnnee croissance;

    @ManyToOne
    @JsonIgnoreProperties(value = { "debut", "fin" }, allowSetters = true)
    private PeriodeAnnee maturite;

    @ManyToOne
    @JsonIgnoreProperties(value = { "debut", "fin" }, allowSetters = true)
    private PeriodeAnnee plantation;

    @ManyToOne
    @JsonIgnoreProperties(value = { "debut", "fin" }, allowSetters = true)
    private PeriodeAnnee rempotage;

    @ManyToOne
    @JsonIgnoreProperties(value = { "cycleDeVies" }, allowSetters = true)
    private Reproduction reproduction;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CycleDeVie id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Semis getSemis() {
        return this.semis;
    }

    public void setSemis(Semis semis) {
        this.semis = semis;
    }

    public CycleDeVie semis(Semis semis) {
        this.setSemis(semis);
        return this;
    }

    public PeriodeAnnee getApparitionFeuilles() {
        return this.apparitionFeuilles;
    }

    public void setApparitionFeuilles(PeriodeAnnee periodeAnnee) {
        this.apparitionFeuilles = periodeAnnee;
    }

    public CycleDeVie apparitionFeuilles(PeriodeAnnee periodeAnnee) {
        this.setApparitionFeuilles(periodeAnnee);
        return this;
    }

    public PeriodeAnnee getFloraison() {
        return this.floraison;
    }

    public void setFloraison(PeriodeAnnee periodeAnnee) {
        this.floraison = periodeAnnee;
    }

    public CycleDeVie floraison(PeriodeAnnee periodeAnnee) {
        this.setFloraison(periodeAnnee);
        return this;
    }

    public PeriodeAnnee getRecolte() {
        return this.recolte;
    }

    public void setRecolte(PeriodeAnnee periodeAnnee) {
        this.recolte = periodeAnnee;
    }

    public CycleDeVie recolte(PeriodeAnnee periodeAnnee) {
        this.setRecolte(periodeAnnee);
        return this;
    }

    public PeriodeAnnee getCroissance() {
        return this.croissance;
    }

    public void setCroissance(PeriodeAnnee periodeAnnee) {
        this.croissance = periodeAnnee;
    }

    public CycleDeVie croissance(PeriodeAnnee periodeAnnee) {
        this.setCroissance(periodeAnnee);
        return this;
    }

    public PeriodeAnnee getMaturite() {
        return this.maturite;
    }

    public void setMaturite(PeriodeAnnee periodeAnnee) {
        this.maturite = periodeAnnee;
    }

    public CycleDeVie maturite(PeriodeAnnee periodeAnnee) {
        this.setMaturite(periodeAnnee);
        return this;
    }

    public PeriodeAnnee getPlantation() {
        return this.plantation;
    }

    public void setPlantation(PeriodeAnnee periodeAnnee) {
        this.plantation = periodeAnnee;
    }

    public CycleDeVie plantation(PeriodeAnnee periodeAnnee) {
        this.setPlantation(periodeAnnee);
        return this;
    }

    public PeriodeAnnee getRempotage() {
        return this.rempotage;
    }

    public void setRempotage(PeriodeAnnee periodeAnnee) {
        this.rempotage = periodeAnnee;
    }

    public CycleDeVie rempotage(PeriodeAnnee periodeAnnee) {
        this.setRempotage(periodeAnnee);
        return this;
    }

    public Reproduction getReproduction() {
        return this.reproduction;
    }

    public void setReproduction(Reproduction reproduction) {
        this.reproduction = reproduction;
    }

    public CycleDeVie reproduction(Reproduction reproduction) {
        this.setReproduction(reproduction);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CycleDeVie)) {
            return false;
        }
        return id != null && id.equals(((CycleDeVie) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CycleDeVie{" +
            "id=" + getId() +
            "}";
    }
}
