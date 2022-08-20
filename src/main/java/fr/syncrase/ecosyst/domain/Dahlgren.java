package fr.syncrase.ecosyst.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A Dahlgren.
 */
@Entity
@Table(name = "dahlgren")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Dahlgren implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @JsonIgnoreProperties(
        value = { "cronquist", "apg", "benthamHooker", "wettstein", "thorne", "takhtajan", "engler", "candolle", "dahlgren", "plantes" },
        allowSetters = true
    )
    @OneToOne
    @JoinColumn(unique = true)
    private Classification classification;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Dahlgren id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Classification getClassification() {
        return this.classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public Dahlgren classification(Classification classification) {
        this.setClassification(classification);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dahlgren)) {
            return false;
        }
        return id != null && id.equals(((Dahlgren) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Dahlgren{" +
            "id=" + getId() +
            "}";
    }
}
