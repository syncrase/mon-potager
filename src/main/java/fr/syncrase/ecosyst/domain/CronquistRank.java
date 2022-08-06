package fr.syncrase.ecosyst.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Une classification est représentée par un set de rang ordonné possédant un parent récursivement jusqu'au rang taxonomique le plus haut, le Domaine
 */
@Schema(
    description = "Une classification est représentée par un set de rang ordonné possédant un parent récursivement jusqu'au rang taxonomique le plus haut, le Domaine"
)
@Entity
@Table(name = "cronquist_rank")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CronquistRank implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Hauteur du rang : classe, ordre, espèce, etc.
     */
    @Schema(description = "Hauteur du rang : classe, ordre, espèce, etc.", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "rank", nullable = false)
    private CronquistTaxonomicRank rank;

    /**
     * Nom du rang. Ex. Plantae<br/>\nNom requis, car peut-être un rang de liaison ne possédant pas de nom (null)<br/>\nDoit être unique
     */
    @Schema(
        description = "Nom du rang. Ex. Plantae<br/>\nNom requis, car peut-être un rang de liaison ne possédant pas de nom (null)<br/>\nDoit être unique"
    )
    @Column(name = "nom", unique = true)
    private String nom;

    @JsonIgnoreProperties(
        value = { "cronquist", "apg", "benthamHooker", "wettstein", "thorne", "takhtajan", "engler", "candolle", "dahlgren", "plantes" },
        allowSetters = true
    )
    @OneToOne
    @JoinColumn(unique = true)
    private Classification classification;

    /**
     * Défini une branche de classification de Cronquist
     */
    @Schema(description = "Défini une branche de classification de Cronquist")
    @OneToMany(mappedBy = "parent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = {"classification", "children", "parent"}, allowSetters = true)
    private Set<CronquistRank> children = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = {"classification", "children", "parent"}, allowSetters = true)
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

    public CronquistTaxonomicRank getRank() {
        return this.rank;
    }

    public CronquistRank rank(CronquistTaxonomicRank rank) {
        this.setRank(rank);
        return this;
    }

    public void setRank(CronquistTaxonomicRank rank) {
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

    public Classification getClassification() {
        return this.classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public CronquistRank classification(Classification classification) {
        this.setClassification(classification);
        return this;
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
        return getNom() != null ? "CronquistRank{" +
            "id=" + getId() +
            ", rank='" + getRank() + "'" +
            ", nom='" + getNom() + "'" +
            "}" : String.valueOf(getRank());
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof CronquistRank) {
            CronquistRank cronquistRank = (CronquistRank) o;
            if (this.getRank() == null && cronquistRank.getRank() == null) {
                return 0;
            }
            if (this.getRank() == null) {
                return 1;
            }
            if (cronquistRank.getRank() == null) {
                return -1;
            }
            return this.getRank().isHighestRankOf(cronquistRank.getRank()) ? 1 : this.getRank().isSameRankOf(cronquistRank.getRank()) ? 0 : -1;
        }
        return 0;
    }
}
