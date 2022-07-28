package fr.syncrase.ecosyst.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PeriodeAnnee.
 */
@Entity
@Table(name = "periode_annee")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PeriodeAnnee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @NotNull
    private Mois debut;

    @ManyToOne(optional = false)
    @NotNull
    private Mois fin;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PeriodeAnnee id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mois getDebut() {
        return this.debut;
    }

    public void setDebut(Mois mois) {
        this.debut = mois;
    }

    public PeriodeAnnee debut(Mois mois) {
        this.setDebut(mois);
        return this;
    }

    public Mois getFin() {
        return this.fin;
    }

    public void setFin(Mois mois) {
        this.fin = mois;
    }

    public PeriodeAnnee fin(Mois mois) {
        this.setFin(mois);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PeriodeAnnee)) {
            return false;
        }
        return id != null && id.equals(((PeriodeAnnee) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PeriodeAnnee{" +
            "id=" + getId() +
            "}";
    }
}
