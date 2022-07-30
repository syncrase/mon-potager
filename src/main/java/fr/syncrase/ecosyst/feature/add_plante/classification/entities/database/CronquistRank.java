package fr.syncrase.ecosyst.feature.add_plante.classification.entities.database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.syncrase.ecosyst.feature.add_plante.classification.enumeration.RankName;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A CronquistRank.
 */
@Entity
@Table(name = "cronquist_rank")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CronquistRank implements Serializable {

    /**
     * Deux rangs taxonomiques sont séparés par d'autres rangs dont on ne connait pas forcément le nom.<br>
     * Une valeur par défaut permet de lier ces deux rangs avec des rangs vides.<br>
     * Si ultérieurement ces rangs sont déterminés, les valeurs par défaut sont mises à jour
     */
    public static final String DEFAULT_NAME_FOR_CONNECTOR_RANK = null;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "rank", nullable = false)
    private RankName rank;
    @OneToMany(mappedBy = "parent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = {"children", "urls", "noms", "getRangSuperieur"}, allowSetters = true)
    private Set<CronquistRank> children = new HashSet<>();
    @OneToMany(mappedBy = "cronquistRank")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = {"cronquistRank"}, allowSetters = true)
    private Set<Url> urls = new HashSet<>();
    @OneToMany(mappedBy = "cronquistRank")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = {"cronquistRank"}, allowSetters = true)
    private Set<ClassificationNom> noms = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    @ManyToOne
    @JsonIgnoreProperties(value = {"children", "urls", "noms", "getRangSuperieur"}, allowSetters = true)
    private CronquistRank parent;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CronquistRank id(Long id) {
        this.setId(id);
        return this;
    }

    public RankName getRank() {
        return this.rank;
    }

    public void setRank(RankName rank) {
        this.rank = rank;
    }

    public CronquistRank rank(RankName rank) {
        this.setRank(rank);
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

    public Set<Url> getUrls() {
        return this.urls;
    }

    public void setUrls(Set<Url> urls) {
        if (this.urls != null) {
            this.urls.forEach(i -> i.setCronquistRank(null));
        }
        if (urls != null) {
            urls.forEach(i -> i.setCronquistRank(this));
        }
        this.urls = urls;
    }

    public CronquistRank urls(Set<Url> urls) {
        this.setUrls(urls);
        return this;
    }

    public CronquistRank addUrls(Url url) {
        this.urls.add(url);
        url.setCronquistRank(this);
        return this;
    }

    public CronquistRank removeUrls(Url url) {
        this.urls.remove(url);
        url.setCronquistRank(null);
        return this;
    }

    public Set<ClassificationNom> getNoms() {
        return this.noms;
    }

    public void setNoms(Set<ClassificationNom> classificationNoms) {
        if (this.noms != null) {
            this.noms.forEach(i -> i.setCronquistRank(null));
        }
        if (classificationNoms != null) {
            classificationNoms.forEach(i -> i.setCronquistRank(this));
        }
        this.noms = classificationNoms;
    }

    public CronquistRank noms(Set<ClassificationNom> classificationNoms) {
        this.setNoms(classificationNoms);
        return this;
    }

    public CronquistRank addNoms(ClassificationNom classificationNom) {
        this.noms.add(classificationNom);
        classificationNom.setCronquistRank(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    public CronquistRank removeNoms(ClassificationNom classificationNom) {
        this.noms.remove(classificationNom);
        classificationNom.setCronquistRank(null);
        return this;
    }

    public CronquistRank getParent() {
        return this.parent;
    }

    public void setParent(CronquistRank cronquistRank) {
        this.parent = cronquistRank;
    }

    @Override
    public String toString() {
        return "CronquistRank{" +
            "id=" + id +
            ", rank=" + rank +
            ", noms=" + noms +
            '}';
    }

    /**
     * Relie les attributs de chaque rang au rang
     */
    public void makeConsistentAggregations() {
        this.getNoms().forEach(nom -> {
            nom.setCronquistRank(this);
        });
        this.getUrls().forEach(url -> {
            url.setCronquistRank(this);
        });
    }
}
