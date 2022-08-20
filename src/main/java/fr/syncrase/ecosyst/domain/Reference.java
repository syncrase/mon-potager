package fr.syncrase.ecosyst.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.syncrase.ecosyst.domain.enumeration.ReferenceType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Reference.
 */
@Entity
@Table(name = "reference")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Reference implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ReferenceType type;

    @ManyToOne
    @JsonIgnoreProperties(value = { "references" }, allowSetters = true)
    private Url url;

    @ManyToMany(mappedBy = "references")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "classification", "nomsVernaculaires", "references" }, allowSetters = true)
    private Set<Plante> plantes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Reference id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Reference description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReferenceType getType() {
        return this.type;
    }

    public Reference type(ReferenceType type) {
        this.setType(type);
        return this;
    }

    public void setType(ReferenceType type) {
        this.type = type;
    }

    public Url getUrl() {
        return this.url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public Reference url(Url url) {
        this.setUrl(url);
        return this;
    }

    public Set<Plante> getPlantes() {
        return this.plantes;
    }

    public void setPlantes(Set<Plante> plantes) {
        if (this.plantes != null) {
            this.plantes.forEach(i -> i.removeReferences(this));
        }
        if (plantes != null) {
            plantes.forEach(i -> i.addReferences(this));
        }
        this.plantes = plantes;
    }

    public Reference plantes(Set<Plante> plantes) {
        this.setPlantes(plantes);
        return this;
    }

    public Reference addPlantes(Plante plante) {
        this.plantes.add(plante);
        plante.getReferences().add(this);
        return this;
    }

    public Reference removePlantes(Plante plante) {
        this.plantes.remove(plante);
        plante.getReferences().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reference)) {
            return false;
        }
        return id != null && id.equals(((Reference) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reference{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
