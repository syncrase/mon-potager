package fr.syncrase.ecosyst.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomikRanks;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CronquistRank.
 */
@Entity
@Table(name = "cronquist_rank")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CronquistRank implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Hauteur du rang: classe, ordre, espèce, etc.
     */
    @Schema(description = "Hauteur du rang: classe, ordre, espèce, etc.", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "rank", nullable = false)
    private CronquistTaxonomikRanks rank;

    /**
     * nom du rang. Ex. Plantae\nNom requis car peut être un rang de liaison ne possédant pas de nom
     */
    @Schema(description = "nom du rang. Ex. Plantae\nNom requis car peut être un rang de liaison ne possédant pas de nom")
    @Column(name = "nom")
    private String nom;

    /**
     * Défini une branche de classification de Cronquist
     */
    @Schema(description = "Défini une branche de classification de Cronquist")
    @OneToMany(mappedBy = "parent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "children", "parent" }, allowSetters = true)
    private Set<CronquistRank> children = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "children", "parent" }, allowSetters = true)
    private CronquistRank parent;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CronquistRank id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CronquistTaxonomikRanks getRank() {
        return this.rank;
    }

    public CronquistRank rank(CronquistTaxonomikRanks rank) {
        this.setRank(rank);
        return this;
    }

    public void setRank(CronquistTaxonomikRanks rank) {
        this.rank = rank;
    }

    public String getNom() {
        return this.nom;
    }

    public CronquistRank nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Set<CronquistRank> getChildren() {
        return this.children;
    }

    public void setChildren(Set<CronquistRank> cronquistRanks) {
        if (this.children != null) {
            this.children.forEach(i -> i.setParent(null));
        }
        if (cronquistRanks != null) {
            cronquistRanks.forEach(i -> i.setParent(this));
        }
        this.children = cronquistRanks;
    }

    public CronquistRank children(Set<CronquistRank> cronquistRanks) {
        this.setChildren(cronquistRanks);
        return this;
    }

    public CronquistRank addChildren(CronquistRank cronquistRank) {
        this.children.add(cronquistRank);
        cronquistRank.setParent(this);
        return this;
    }

    public CronquistRank removeChildren(CronquistRank cronquistRank) {
        this.children.remove(cronquistRank);
        cronquistRank.setParent(null);
        return this;
    }

    public CronquistRank getParent() {
        return this.parent;
    }

    public void setParent(CronquistRank cronquistRank) {
        this.parent = cronquistRank;
    }

    public CronquistRank parent(CronquistRank cronquistRank) {
        this.setParent(cronquistRank);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CronquistRank)) {
            return false;
        }
        return id != null && id.equals(((CronquistRank) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CronquistRank{" +
            "id=" + getId() +
            ", rank='" + getRank() + "'" +
            ", nom='" + getNom() + "'" +
            "}";
    }
}
