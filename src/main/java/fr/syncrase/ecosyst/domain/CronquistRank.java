package fr.syncrase.ecosyst.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomikRanks;
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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "rank", nullable = false)
    private CronquistTaxonomikRanks rank;

    @OneToMany(mappedBy = "parent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "children", "parent", "plante" }, allowSetters = true)
    private Set<CronquistRank> children = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "children", "parent", "plante" }, allowSetters = true)
    private CronquistRank parent;

    @ManyToOne
    @JsonIgnoreProperties(value = { "lowestClassificationRanks", "nomsVernaculaires" }, allowSetters = true)
    private Plante plante;

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

    public Plante getPlante() {
        return this.plante;
    }

    public void setPlante(Plante plante) {
        this.plante = plante;
    }

    public CronquistRank plante(Plante plante) {
        this.setPlante(plante);
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
            "}";
    }
}
