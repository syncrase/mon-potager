package fr.syncrase.ecosyst.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Wettstein.
 */
@Entity
@Table(name = "wettstein")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Wettstein implements Serializable {

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

    public Wettstein id(Long id) {
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

    public Wettstein classification(Classification classification) {
        this.setClassification(classification);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Wettstein)) {
            return false;
        }
        return id != null && id.equals(((Wettstein) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Wettstein{" +
            "id=" + getId() +
            "}";
    }
}
