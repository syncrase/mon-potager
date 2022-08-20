package fr.syncrase.ecosyst.feature.add_plante.classification.entities.database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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

    @NotNull
    @Column(name = "url", nullable = false)
    private String url;

    @ManyToOne
    @JsonIgnoreProperties(value = {"children", "urls", "noms", "getRangSuperieur"}, allowSetters = true)
    private CronquistRank cronquistRank;

    public Url(String url, Long id) {
        this.url = url;
        this.id = id;
    }

    public Url() {

    }

    public static Url newUrl(String urlWiki) {
        return new Url().url(urlWiki);
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Url id(Long id) {
        this.setId(id);
        return this;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Url url(String url) {
        this.setUrl(url);
        return this;
    }

    public CronquistRank getCronquistRank() {
        return this.cronquistRank;
    }

    public void setCronquistRank(CronquistRank cronquistRank) {
        this.cronquistRank = cronquistRank;
    }

    public Url cronquistRank(CronquistRank cronquistRank) {
        this.setCronquistRank(cronquistRank);
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
