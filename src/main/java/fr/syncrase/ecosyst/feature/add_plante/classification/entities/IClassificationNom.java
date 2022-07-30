package fr.syncrase.ecosyst.feature.add_plante.classification.entities;

import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.ClassificationNom;

public interface IClassificationNom extends Cloneable {
    String getNomFr();

    Long getId();

    void setId(Long id);

    String getNomLatin();

    public IClassificationNom clone();

    ClassificationNom getClassificationNom();

    IClassificationNom nomFr(String nom);
}
