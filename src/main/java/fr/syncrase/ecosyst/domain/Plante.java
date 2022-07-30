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
 * A Plante.
 */
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

    @OneToMany(mappedBy = "plante")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "children", "parent", "plante" }, allowSetters = true)
    private Set<CronquistRank> lowestClassificationRanks = new HashSet<>();

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
    @JsonIgnoreProperties(value = { "plantes" }, allowSetters = true)
    private Set<NomVernaculaire> nomsVernaculaires = new HashSet<>();

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

    public Set<CronquistRank> getLowestClassificationRanks() {
        return this.lowestClassificationRanks;
    }

    public void setLowestClassificationRanks(Set<CronquistRank> cronquistRanks) {
        if (this.lowestClassificationRanks != null) {
            this.lowestClassificationRanks.forEach(i -> i.setPlante(null));
        }
        if (cronquistRanks != null) {
            cronquistRanks.forEach(i -> i.setPlante(this));
        }
        this.lowestClassificationRanks = cronquistRanks;
    }

    public Plante lowestClassificationRanks(Set<CronquistRank> cronquistRanks) {
        this.setLowestClassificationRanks(cronquistRanks);
        return this;
    }

    public Plante addLowestClassificationRank(CronquistRank cronquistRank) {
        this.lowestClassificationRanks.add(cronquistRank);
        cronquistRank.setPlante(this);
        return this;
    }

    public Plante removeLowestClassificationRank(CronquistRank cronquistRank) {
        this.lowestClassificationRanks.remove(cronquistRank);
        cronquistRank.setPlante(null);
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
            "}";
    }
}
