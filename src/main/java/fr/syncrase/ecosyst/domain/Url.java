package fr.syncrase.ecosyst.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Url.
 */
@Entity
@Table(name = "url")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Url implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "url", unique = true)
    private String url;

    @OneToMany(mappedBy = "url")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "url", "plantes" }, allowSetters = true)
    private Set<Reference> references = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Url id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public Url url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<Reference> getReferences() {
        return this.references;
    }

    public void setReferences(Set<Reference> references) {
        if (this.references != null) {
            this.references.forEach(i -> i.setUrl(null));
        }
        if (references != null) {
            references.forEach(i -> i.setUrl(this));
        }
        this.references = references;
    }

    public Url references(Set<Reference> references) {
        this.setReferences(references);
        return this;
    }

    public Url addReferences(Reference reference) {
        this.references.add(reference);
        reference.setUrl(this);
        return this;
    }

    public Url removeReferences(Reference reference) {
        this.references.remove(reference);
        reference.setUrl(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Url)) {
            return false;
        }
        return id != null && id.equals(((Url) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Url{" +
            "id=" + getId() +
            ", url='" + getUrl() + "'" +
            "}";
    }
}
