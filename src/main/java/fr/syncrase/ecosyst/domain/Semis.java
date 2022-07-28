package fr.syncrase.ecosyst.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Semis.
 */
@Entity
@Table(name = "semis")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Semis implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties(value = { "debut", "fin" }, allowSetters = true)
    private PeriodeAnnee semisPleineTerre;

    @ManyToOne
    @JsonIgnoreProperties(value = { "debut", "fin" }, allowSetters = true)
    private PeriodeAnnee semisSousAbris;

    @ManyToOne
    private TypeSemis typeSemis;

    @ManyToOne
    private Germination germination;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Semis id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PeriodeAnnee getSemisPleineTerre() {
        return this.semisPleineTerre;
    }

    public void setSemisPleineTerre(PeriodeAnnee periodeAnnee) {
        this.semisPleineTerre = periodeAnnee;
    }

    public Semis semisPleineTerre(PeriodeAnnee periodeAnnee) {
        this.setSemisPleineTerre(periodeAnnee);
        return this;
    }

    public PeriodeAnnee getSemisSousAbris() {
        return this.semisSousAbris;
    }

    public void setSemisSousAbris(PeriodeAnnee periodeAnnee) {
        this.semisSousAbris = periodeAnnee;
    }

    public Semis semisSousAbris(PeriodeAnnee periodeAnnee) {
        this.setSemisSousAbris(periodeAnnee);
        return this;
    }

    public TypeSemis getTypeSemis() {
        return this.typeSemis;
    }

    public void setTypeSemis(TypeSemis typeSemis) {
        this.typeSemis = typeSemis;
    }

    public Semis typeSemis(TypeSemis typeSemis) {
        this.setTypeSemis(typeSemis);
        return this;
    }

    public Germination getGermination() {
        return this.germination;
    }

    public void setGermination(Germination germination) {
        this.germination = germination;
    }

    public Semis germination(Germination germination) {
        this.setGermination(germination);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Semis)) {
            return false;
        }
        return id != null && id.equals(((Semis) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Semis{" +
            "id=" + getId() +
            "}";
    }
}
