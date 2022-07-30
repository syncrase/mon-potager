package fr.syncrase.ecosyst.feature.add_plante.classification.entities.wrapper;

import fr.syncrase.ecosyst.feature.add_plante.classification.entities.IClassificationNom;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.ClassificationNom;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.CronquistRank;

public class ClassificationNomWrapper implements IClassificationNom {

    private ClassificationNom classificationNom;

    public ClassificationNomWrapper(ClassificationNom classificationNom) {
        this.classificationNom = classificationNom;
    }

    public ClassificationNomWrapper() {

    }

    @Override
    public String getNomFr() {
        return this.classificationNom.getNomFr();
    }

    @Override
    public Long getId() {
        return this.classificationNom.getId();
    }

    @Override
    public void setId(Long id) {
        this.classificationNom.setId(id);
    }

    @Override
    public String getNomLatin() {
        return this.classificationNom.getNomLatin();
    }

    @Override
    public ClassificationNomWrapper clone() {
        try {
            ClassificationNomWrapper clone = (ClassificationNomWrapper) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public ClassificationNom getClassificationNom() {
        return this.classificationNom;
    }

    @Override
    public IClassificationNom nomFr(String nom) {
        this.classificationNom.setNomFr(nom);
        return this;
    }

    public void setCronquistRank(CronquistRank save) {
        this.classificationNom.setCronquistRank(save);
    }
}
