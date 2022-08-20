package fr.syncrase.ecosyst.feature.add_plante.models;

import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import org.jetbrains.annotations.NotNull;

public class ScrapedPlant {

    private CronquistClassificationBranch cronquistClassificationBranch;

    private final Plante plante;

    public ScrapedPlant() {
        this.plante = new Plante();
    }

    public ScrapedPlant(@NotNull Plante plante) {
        this.plante = plante;
        this.cronquistClassificationBranch = new CronquistClassificationBranch(plante.getClassification().getCronquist());
    }

    public CronquistClassificationBranch getCronquistClassificationBranch() {
        return this.cronquistClassificationBranch;
    }

    public void setCronquistClassificationBranch(CronquistClassificationBranch cronquistClassificationBranch) {
        this.cronquistClassificationBranch = cronquistClassificationBranch;
    }

    public ScrapedPlant cronquistClassificationBranch(CronquistClassificationBranch cronquistClassificationBranch) {
        this.cronquistClassificationBranch = cronquistClassificationBranch;
        return this;
    }

    public Plante getPlante() {
        return plante;
    }

    @Override
    public String toString() {
        return "Plante{" +
            "id=" + this.plante.getId() +
            "}";
    }
}
