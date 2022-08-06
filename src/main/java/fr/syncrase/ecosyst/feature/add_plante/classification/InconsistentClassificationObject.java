package fr.syncrase.ecosyst.feature.add_plante.classification;

public class InconsistentClassificationObject extends Exception {

    public InconsistentClassificationObject() {
        super("Un rang doit absolument posséder un rang taxonomique (Plantae => Règne)");
    }
}
