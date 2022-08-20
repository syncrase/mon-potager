package fr.syncrase.ecosyst.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Fait le lien entre les différentes classifications
 */
@Schema(description = "Fait le lien entre les différentes classifications")
@Entity
@Table(name = "classification")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Classification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @JsonIgnoreProperties(value = { "classification", "children", "parent" }, allowSetters = true)
    @OneToOne(mappedBy = "classification")
    private CronquistRank cronquist;

    @JsonIgnoreProperties(value = { "classification" }, allowSetters = true)
    @OneToOne(mappedBy = "classification")
    private APG apg;

    @JsonIgnoreProperties(value = { "classification" }, allowSetters = true)
    @OneToOne(mappedBy = "classification")
    private BenthamHooker benthamHooker;

    @JsonIgnoreProperties(value = { "classification" }, allowSetters = true)
    @OneToOne(mappedBy = "classification")
    private Wettstein wettstein;

    @JsonIgnoreProperties(value = { "classification" }, allowSetters = true)
    @OneToOne(mappedBy = "classification")
    private Thorne thorne;

    @JsonIgnoreProperties(value = { "classification" }, allowSetters = true)
    @OneToOne(mappedBy = "classification")
    private Takhtajan takhtajan;

    @JsonIgnoreProperties(value = { "classification" }, allowSetters = true)
    @OneToOne(mappedBy = "classification")
    private Engler engler;

    @JsonIgnoreProperties(value = { "classification" }, allowSetters = true)
    @OneToOne(mappedBy = "classification")
    private Candolle candolle;

    @JsonIgnoreProperties(value = { "classification" }, allowSetters = true)
    @OneToOne(mappedBy = "classification")
    private Dahlgren dahlgren;

    @OneToMany(mappedBy = "classification")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "classification", "nomsVernaculaires", "references" }, allowSetters = true)
    private Set<Plante> plantes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Classification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CronquistRank getCronquist() {
        return this.cronquist;
    }

    public void setCronquist(CronquistRank cronquistRank) {
        if (this.cronquist != null) {
            this.cronquist.setClassification(null);
        }
        if (cronquistRank != null) {
            cronquistRank.setClassification(this);
        }
        this.cronquist = cronquistRank;
    }

    public Classification cronquist(CronquistRank cronquistRank) {
        this.setCronquist(cronquistRank);
        return this;
    }

    public APG getApg() {
        return this.apg;
    }

    public void setApg(APG aPG) {
        if (this.apg != null) {
            this.apg.setClassification(null);
        }
        if (aPG != null) {
            aPG.setClassification(this);
        }
        this.apg = aPG;
    }

    public Classification apg(APG aPG) {
        this.setApg(aPG);
        return this;
    }

    public BenthamHooker getBenthamHooker() {
        return this.benthamHooker;
    }

    public void setBenthamHooker(BenthamHooker benthamHooker) {
        if (this.benthamHooker != null) {
            this.benthamHooker.setClassification(null);
        }
        if (benthamHooker != null) {
            benthamHooker.setClassification(this);
        }
        this.benthamHooker = benthamHooker;
    }

    public Classification benthamHooker(BenthamHooker benthamHooker) {
        this.setBenthamHooker(benthamHooker);
        return this;
    }

    public Wettstein getWettstein() {
        return this.wettstein;
    }

    public void setWettstein(Wettstein wettstein) {
        if (this.wettstein != null) {
            this.wettstein.setClassification(null);
        }
        if (wettstein != null) {
            wettstein.setClassification(this);
        }
        this.wettstein = wettstein;
    }

    public Classification wettstein(Wettstein wettstein) {
        this.setWettstein(wettstein);
        return this;
    }

    public Thorne getThorne() {
        return this.thorne;
    }

    public void setThorne(Thorne thorne) {
        if (this.thorne != null) {
            this.thorne.setClassification(null);
        }
        if (thorne != null) {
            thorne.setClassification(this);
        }
        this.thorne = thorne;
    }

    public Classification thorne(Thorne thorne) {
        this.setThorne(thorne);
        return this;
    }

    public Takhtajan getTakhtajan() {
        return this.takhtajan;
    }

    public void setTakhtajan(Takhtajan takhtajan) {
        if (this.takhtajan != null) {
            this.takhtajan.setClassification(null);
        }
        if (takhtajan != null) {
            takhtajan.setClassification(this);
        }
        this.takhtajan = takhtajan;
    }

    public Classification takhtajan(Takhtajan takhtajan) {
        this.setTakhtajan(takhtajan);
        return this;
    }

    public Engler getEngler() {
        return this.engler;
    }

    public void setEngler(Engler engler) {
        if (this.engler != null) {
            this.engler.setClassification(null);
        }
        if (engler != null) {
            engler.setClassification(this);
        }
        this.engler = engler;
    }

    public Classification engler(Engler engler) {
        this.setEngler(engler);
        return this;
    }

    public Candolle getCandolle() {
        return this.candolle;
    }

    public void setCandolle(Candolle candolle) {
        if (this.candolle != null) {
            this.candolle.setClassification(null);
        }
        if (candolle != null) {
            candolle.setClassification(this);
        }
        this.candolle = candolle;
    }

    public Classification candolle(Candolle candolle) {
        this.setCandolle(candolle);
        return this;
    }

    public Dahlgren getDahlgren() {
        return this.dahlgren;
    }

    public void setDahlgren(Dahlgren dahlgren) {
        if (this.dahlgren != null) {
            this.dahlgren.setClassification(null);
        }
        if (dahlgren != null) {
            dahlgren.setClassification(this);
        }
        this.dahlgren = dahlgren;
    }

    public Classification dahlgren(Dahlgren dahlgren) {
        this.setDahlgren(dahlgren);
        return this;
    }

    public Set<Plante> getPlantes() {
        return this.plantes;
    }

    public void setPlantes(Set<Plante> plantes) {
        if (this.plantes != null) {
            this.plantes.forEach(i -> i.setClassification(null));
        }
        if (plantes != null) {
            plantes.forEach(i -> i.setClassification(this));
        }
        this.plantes = plantes;
    }

    public Classification plantes(Set<Plante> plantes) {
        this.setPlantes(plantes);
        return this;
    }

    public Classification addPlantes(Plante plante) {
        this.plantes.add(plante);
        plante.setClassification(this);
        return this;
    }

    public Classification removePlantes(Plante plante) {
        this.plantes.remove(plante);
        plante.setClassification(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Classification)) {
            return false;
        }
        return id != null && id.equals(((Classification) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Classification{" +
            "id=" + getId() +
            "}";
    }
}
