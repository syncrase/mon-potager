package fr.syncrase.ecosyst.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Une plante est soit une plante botanique qui correspond à une unique classification (et n'est associée à aucune autre plante, elle est unique) soit une plante potagère qui correspond à une unique plante botanique
 */
@Schema(
    description = "Une plante est soit une plante botanique qui correspond à une unique classification (et n'est associée à aucune autre plante, elle est unique) soit une plante potagère qui correspond à une unique plante botanique"
)
@Entity
@Table(name = "plante")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Plante implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "entretien")
    private String entretien;

    @Column(name = "histoire")
    private String histoire;

    @Column(name = "vitesse_croissance")
    private String vitesseCroissance;

    @Column(name = "exposition")
    private String exposition;

    @OneToMany(mappedBy = "planteRessemblant")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "planteRessemblant" }, allowSetters = true)
    private Set<Ressemblance> confusions = new HashSet<>();

    @OneToMany(mappedBy = "plante")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "plante" }, allowSetters = true)
    private Set<Ensoleillement> ensoleillements = new HashSet<>();

    /**
     * Une plante peut avoir beaucoup de variantes potagère\nUne plante potagère ne correspond qu'à une seule plante botanique et n'est associé à aucune classification (contenu dans le plante botanique)
     */
    @Schema(
        description = "Une plante peut avoir beaucoup de variantes potagère\nUne plante potagère ne correspond qu'à une seule plante botanique et n'est associé à aucune classification (contenu dans le plante botanique)"
    )
    @OneToMany(mappedBy = "planteBotanique")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    private Set<Plante> plantesPotageres = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(
        value = {
            "semis", "apparitionFeuilles", "floraison", "recolte", "croissance", "maturite", "plantation", "rempotage", "reproduction",
        },
        allowSetters = true
    )
    private CycleDeVie cycleDeVie;

    @ManyToOne
    private Sol sol;

    @ManyToOne
    private Temperature temperature;

    @ManyToOne
    private Racine racine;

    @ManyToOne
    private Strate strate;

    @ManyToOne
    private Feuillage feuillage;

    /**
     * Un même nom vernaculaire peut qualifier plusieurs plantes distinctes et très différentes
     */
    @Schema(description = "Un même nom vernaculaire peut qualifier plusieurs plantes distinctes et très différentes")
    @ManyToMany
    @JoinTable(
        name = "rel_plante__noms_vernaculaires",
        joinColumns = @JoinColumn(name = "plante_id"),
        inverseJoinColumns = @JoinColumn(name = "noms_vernaculaires_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "cronquistRank", "plantes" }, allowSetters = true)
    private Set<NomVernaculaire> nomsVernaculaires = new HashSet<>();

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
    private Plante planteBotanique;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Plante id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntretien() {
        return this.entretien;
    }

    public Plante entretien(String entretien) {
        this.setEntretien(entretien);
        return this;
    }

    public void setEntretien(String entretien) {
        this.entretien = entretien;
    }

    public String getHistoire() {
        return this.histoire;
    }

    public Plante histoire(String histoire) {
        this.setHistoire(histoire);
        return this;
    }

    public void setHistoire(String histoire) {
        this.histoire = histoire;
    }

    public String getVitesseCroissance() {
        return this.vitesseCroissance;
    }

    public Plante vitesseCroissance(String vitesseCroissance) {
        this.setVitesseCroissance(vitesseCroissance);
        return this;
    }

    public void setVitesseCroissance(String vitesseCroissance) {
        this.vitesseCroissance = vitesseCroissance;
    }

    public String getExposition() {
        return this.exposition;
    }

    public Plante exposition(String exposition) {
        this.setExposition(exposition);
        return this;
    }

    public void setExposition(String exposition) {
        this.exposition = exposition;
    }

    public Set<Ressemblance> getConfusions() {
        return this.confusions;
    }

    public void setConfusions(Set<Ressemblance> ressemblances) {
        if (this.confusions != null) {
            this.confusions.forEach(i -> i.setPlanteRessemblant(null));
        }
        if (ressemblances != null) {
            ressemblances.forEach(i -> i.setPlanteRessemblant(this));
        }
        this.confusions = ressemblances;
    }

    public Plante confusions(Set<Ressemblance> ressemblances) {
        this.setConfusions(ressemblances);
        return this;
    }

    public Plante addConfusions(Ressemblance ressemblance) {
        this.confusions.add(ressemblance);
        ressemblance.setPlanteRessemblant(this);
        return this;
    }

    public Plante removeConfusions(Ressemblance ressemblance) {
        this.confusions.remove(ressemblance);
        ressemblance.setPlanteRessemblant(null);
        return this;
    }

    public Set<Ensoleillement> getEnsoleillements() {
        return this.ensoleillements;
    }

    public void setEnsoleillements(Set<Ensoleillement> ensoleillements) {
        if (this.ensoleillements != null) {
            this.ensoleillements.forEach(i -> i.setPlante(null));
        }
        if (ensoleillements != null) {
            ensoleillements.forEach(i -> i.setPlante(this));
        }
        this.ensoleillements = ensoleillements;
    }

    public Plante ensoleillements(Set<Ensoleillement> ensoleillements) {
        this.setEnsoleillements(ensoleillements);
        return this;
    }

    public Plante addEnsoleillements(Ensoleillement ensoleillement) {
        this.ensoleillements.add(ensoleillement);
        ensoleillement.setPlante(this);
        return this;
    }

    public Plante removeEnsoleillements(Ensoleillement ensoleillement) {
        this.ensoleillements.remove(ensoleillement);
        ensoleillement.setPlante(null);
        return this;
    }

    public Set<Plante> getPlantesPotageres() {
        return this.plantesPotageres;
    }

    public void setPlantesPotageres(Set<Plante> plantes) {
        if (this.plantesPotageres != null) {
            this.plantesPotageres.forEach(i -> i.setPlanteBotanique(null));
        }
        if (plantes != null) {
            plantes.forEach(i -> i.setPlanteBotanique(this));
        }
        this.plantesPotageres = plantes;
    }

    public Plante plantesPotageres(Set<Plante> plantes) {
        this.setPlantesPotageres(plantes);
        return this;
    }

    public Plante addPlantesPotageres(Plante plante) {
        this.plantesPotageres.add(plante);
        plante.setPlanteBotanique(this);
        return this;
    }

    public Plante removePlantesPotageres(Plante plante) {
        this.plantesPotageres.remove(plante);
        plante.setPlanteBotanique(null);
        return this;
    }

    public CycleDeVie getCycleDeVie() {
        return this.cycleDeVie;
    }

    public void setCycleDeVie(CycleDeVie cycleDeVie) {
        this.cycleDeVie = cycleDeVie;
    }

    public Plante cycleDeVie(CycleDeVie cycleDeVie) {
        this.setCycleDeVie(cycleDeVie);
        return this;
    }

    public Sol getSol() {
        return this.sol;
    }

    public void setSol(Sol sol) {
        this.sol = sol;
    }

    public Plante sol(Sol sol) {
        this.setSol(sol);
        return this;
    }

    public Temperature getTemperature() {
        return this.temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public Plante temperature(Temperature temperature) {
        this.setTemperature(temperature);
        return this;
    }

    public Racine getRacine() {
        return this.racine;
    }

    public void setRacine(Racine racine) {
        this.racine = racine;
    }

    public Plante racine(Racine racine) {
        this.setRacine(racine);
        return this;
    }

    public Strate getStrate() {
        return this.strate;
    }

    public void setStrate(Strate strate) {
        this.strate = strate;
    }

    public Plante strate(Strate strate) {
        this.setStrate(strate);
        return this;
    }

    public Feuillage getFeuillage() {
        return this.feuillage;
    }

    public void setFeuillage(Feuillage feuillage) {
        this.feuillage = feuillage;
    }

    public Plante feuillage(Feuillage feuillage) {
        this.setFeuillage(feuillage);
        return this;
    }

    public Set<NomVernaculaire> getNomsVernaculaires() {
        return this.nomsVernaculaires;
    }

    public void setNomsVernaculaires(Set<NomVernaculaire> nomVernaculaires) {
        this.nomsVernaculaires = nomVernaculaires;
    }

    public Plante nomsVernaculaires(Set<NomVernaculaire> nomVernaculaires) {
        this.setNomsVernaculaires(nomVernaculaires);
        return this;
    }

    public Plante addNomsVernaculaires(NomVernaculaire nomVernaculaire) {
        this.nomsVernaculaires.add(nomVernaculaire);
        nomVernaculaire.getPlantes().add(this);
        return this;
    }

    public Plante removeNomsVernaculaires(NomVernaculaire nomVernaculaire) {
        this.nomsVernaculaires.remove(nomVernaculaire);
        nomVernaculaire.getPlantes().remove(this);
        return this;
    }

    public Plante getPlanteBotanique() {
        return this.planteBotanique;
    }

    public void setPlanteBotanique(Plante plante) {
        this.planteBotanique = plante;
    }

    public Plante planteBotanique(Plante plante) {
        this.setPlanteBotanique(plante);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Plante)) {
            return false;
        }
        return id != null && id.equals(((Plante) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Plante{" +
            "id=" + getId() +
            ", entretien='" + getEntretien() + "'" +
            ", histoire='" + getHistoire() + "'" +
            ", vitesseCroissance='" + getVitesseCroissance() + "'" +
            ", exposition='" + getExposition() + "'" +
            "}";
    }
}
