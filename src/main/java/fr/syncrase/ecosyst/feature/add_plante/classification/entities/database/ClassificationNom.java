package fr.syncrase.ecosyst.feature.add_plante.classification.entities.database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A ClassificationNom.
 */
@Entity
@Table(name = "classification_nom")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ClassificationNom implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "nom_fr", unique = true)
    private String nomFr;

    @Column(name = "nom_latin", unique = true)
    private String nomLatin;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = {"children", "urls", "noms", "getRangSuperieur"}, allowSetters = true)
    private CronquistRank cronquistRank;

    public ClassificationNom(Long id, String nomFr, String nomLatin) {
        this.id = id;
        this.nomFr = nomFr;
        this.nomLatin = nomLatin;
    }

    public ClassificationNom() {

    }

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClassificationNom id(Long id) {
        this.setId(id);
        return this;
    }

    public String getNomFr() {
        return this.nomFr;
    }

    public void setNomFr(String nomFr) {
        this.nomFr = nomFr;
    }

    public ClassificationNom nomFr(String nomFr) {
        this.setNomFr(nomFr);
        return this;
    }

    public String getNomLatin() {
        return this.nomLatin;
    }

    public void setNomLatin(String nomLatin) {
        this.nomLatin = nomLatin;
    }

    public ClassificationNom nomLatin(String nomLatin) {
        this.setNomLatin(nomLatin);
        return this;
    }

    public CronquistRank getCronquistRank() {
        return this.cronquistRank;
    }

    public void setCronquistRank(CronquistRank cronquistRank) {
        this.cronquistRank = cronquistRank;
    }

    public ClassificationNom cronquistRank(CronquistRank cronquistRank) {
        this.setCronquistRank(cronquistRank);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassificationNom)) {
            return false;
        }
        return id != null && id.equals(((ClassificationNom) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClassificationNom{" +
            "id=" + getId() +
            ", nomFr='" + getNomFr() + "'" +
            ", nomLatin='" + getNomLatin() + "'" +
            "}";
    }
}
