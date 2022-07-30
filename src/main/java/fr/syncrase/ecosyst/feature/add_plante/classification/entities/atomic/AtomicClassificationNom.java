package fr.syncrase.ecosyst.feature.add_plante.classification.entities.atomic;


import fr.syncrase.ecosyst.feature.add_plante.classification.entities.IClassificationNom;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.ClassificationNom;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.TreeSet;

public class AtomicClassificationNom implements IClassificationNom {
    private Long id;
    private String nomFr;
    private String nomLatin;

    public AtomicClassificationNom(@NotNull IClassificationNom classificationNom) {
        this.id = classificationNom.getId();
        this.nomFr = classificationNom.getNomFr();
        this.nomLatin = classificationNom.getNomLatin();
    }

    public AtomicClassificationNom(@NotNull ClassificationNom classificationNom) {
        this.id = classificationNom.getId();
        this.nomFr = classificationNom.getNomFr();
        this.nomLatin = classificationNom.getNomLatin();
    }

    public AtomicClassificationNom() {
    }

    /**
     * Retourne un set vide de ClassificationNom dont la comparaison se fait sur le nomFr
     *
     * @return Tree set vide avec comparaison personnalisée
     */
    @NotNull
    public static TreeSet<IClassificationNom> getAtomicClassificationNomTreeSet() {
        // TODO déplacer cette méthode
        return new TreeSet<>(Comparator.comparing(IClassificationNom::getNomFr, (nomFrExistant, nomFrAAjouter) -> {
            if (nomFrExistant == null && nomFrAAjouter == null) {
                return 0;
            }
            if (nomFrExistant == null) {
                return 1;
            }
            if (nomFrAAjouter == null) {
                return -1;
            }
            return nomFrExistant.compareTo(nomFrAAjouter);
        }));
    }

    public AtomicClassificationNom nomFr(String nomFr) {
        this.setNomFr(nomFr);
        return this;
    }

    public String getNomFr() {
        return this.nomFr;
    }

    public void setNomFr(String nomFr) {
        this.nomFr = nomFr;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtomicClassificationNom id(Long id) {
        this.setId(id);
        return this;
    }

    public AtomicClassificationNom nomLatin(String nomLatin) {
        this.setNomLatin(nomLatin);
        return this;
    }

    public String getNomLatin() {
        return this.nomLatin;
    }

    public void setNomLatin(String nomLatin) {
        this.nomLatin = nomLatin;
    }

    @Override
    public AtomicClassificationNom clone() {
        try {
            AtomicClassificationNom clone = (AtomicClassificationNom) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public ClassificationNom getClassificationNom() {
        return new ClassificationNom()
            .id(this.id)
            .nomFr(this.nomFr)
            .nomLatin(this.nomLatin);
    }
}
