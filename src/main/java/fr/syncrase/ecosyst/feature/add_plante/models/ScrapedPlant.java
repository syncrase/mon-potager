package fr.syncrase.ecosyst.feature.add_plante.models;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.NomVernaculaire;

import java.util.HashSet;
import java.util.Set;

public class ScrapedPlant {

    private Long id;
    private Set<CronquistRank> lowestClassificationRanks = new HashSet<>();
    private Set<NomVernaculaire> nomsVernaculaires = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public ScrapedPlant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<CronquistRank> getLowestClassificationRanks() {
        return this.lowestClassificationRanks;
    }

    public void setLowestClassificationRanks(Set<CronquistRank> lowestClassificationRanks) {
        this.lowestClassificationRanks = lowestClassificationRanks;
    }

    public ScrapedPlant lowestClassificationRanks(Set<CronquistRank> lowestClassificationRanks) {
        this.lowestClassificationRanks = lowestClassificationRanks;
        return this;
    }

    public Set<NomVernaculaire> getNomsVernaculaires() {
        return this.nomsVernaculaires;
    }

    public void setNomsVernaculaires(Set<NomVernaculaire> nomVernaculaires) {
        this.nomsVernaculaires = nomVernaculaires;
    }

    public ScrapedPlant nomsVernaculaires(Set<NomVernaculaire> nomVernaculaires) {
        this.setNomsVernaculaires(nomVernaculaires);
        return this;
    }

    public ScrapedPlant addNomsVernaculaires(NomVernaculaire nom) {
        this.nomsVernaculaires.add(nom);
        return this;
    }

    @Override
    public String toString() {
        return "Plante{" +
            "id=" + getId() +
            "}";
    }
}
