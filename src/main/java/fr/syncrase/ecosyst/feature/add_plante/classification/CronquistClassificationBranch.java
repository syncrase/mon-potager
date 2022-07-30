package fr.syncrase.ecosyst.feature.add_plante.classification;

import fr.syncrase.ecosyst.feature.add_plante.classification.entities.atomic.AtomicCronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.consistency.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.ICronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.enumeration.RankName;
import org.apache.commons.collections4.map.LinkedMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

// TODO extends AbstractLinkedMap<RankName, ICronquistRank>
public class CronquistClassificationBranch implements Cloneable {

    /**
     * Liste de tous les rangs de la classification<br>
     * L'élément 0 est le rang le plus haut : le super règne
     */
    private LinkedMap<RankName, ICronquistRank> classificationCronquistMap;

    /**
     * Construit une classification vierge
     */
    public CronquistClassificationBranch() {
        initEmptyClassification();
    }

    /**
     * Construit une classification à partir d'un rang
     */
    public CronquistClassificationBranch(@NotNull ICronquistRank cronquistRank) throws ClassificationReconstructionException {
        initEmptyClassification();
        ICronquistRank currentRank = cronquistRank;
        while (currentRank != null) {
            classificationCronquistMap.put(currentRank.getRankName(), new AtomicCronquistRank(currentRank));
            currentRank = currentRank.getParent();
        }
        this.clearTail();
    }

    @Nullable
    private ICronquistRank getParent(@NotNull ICronquistRank currentRank) {
        boolean isSuperRegne = currentRank.getRankName().equals(RankName.SUPERREGNE);
        if (isSuperRegne) {
            return null;
        }
        return classificationCronquistMap.get(currentRank.getRankName().getRangSuperieur());
    }

    private void initEmptyClassification() {
        classificationCronquistMap = new LinkedMap<>(34);
        initDefaultValues();
    }

    private void initDefaultValues() {
        RankName[] rangsDisponibles = RankName.values();
        for (RankName hauteurDeRangEnCours : rangsDisponibles) {
            classificationCronquistMap.put(hauteurDeRangEnCours, AtomicCronquistRank.getDefaultRank(hauteurDeRangEnCours));
        }
    }

    /**
     * La raison d'être des rangs de liaison (sans nom) est de lier deux rangs dont les noms sont connus.<br>
     * Cette méthode nettoie les rangs liaison inférieurs au dernier rang de la classification
     */
    public void clearTail() {
        RankName[] rangsDisponibles = RankName.values();
        for (int positionDansLaClassification = rangsDisponibles.length - 1; positionDansLaClassification >= 0; positionDansLaClassification--) {
            RankName nomDuRangEnCours = rangsDisponibles[positionDansLaClassification];
            boolean cestUnRangConnu = classificationCronquistMap.get(nomDuRangEnCours) != null && !classificationCronquistMap.get(nomDuRangEnCours).isRangDeLiaison();
            if (cestUnRangConnu) {
                break;
            }
            removeTaxon(nomDuRangEnCours);
        }
    }

    private void removeTaxon(@NotNull RankName nomDuRangEnCours) {
        classificationCronquistMap.remove(nomDuRangEnCours);
    }

    public ICronquistRank getRang(RankName rang) {
        if (rang != null) {
            return classificationCronquistMap.get(rang);
        } else {
            return null;
        }
    }

    public ICronquistRank getRangDeBase() {
        return classificationCronquistMap.get(classificationCronquistMap.lastKey());
    }

    public Collection<ICronquistRank> getClassification() {
        return classificationCronquistMap.values();
    }

    public LinkedMap<RankName, ICronquistRank> getClassificationBranch() {
        return classificationCronquistMap;
    }

    public ICronquistRank put(RankName currentRankName, ICronquistRank currentRank) {
        classificationCronquistMap.put(currentRankName, currentRank);
        return currentRank;
    }

    public int ranksCount() {
        return classificationCronquistMap.size();
    }

    @Override
    public String toString() {
        return "CronquistClassificationBranch{" + "classificationCronquistMap=" + classificationCronquistMap + '}';
    }

    public void inferAllRank() {
        classificationCronquistMap.forEach((rankName, rank) -> rank.setRankName(rankName));
    }

    @Override
    public CronquistClassificationBranch clone() {
        try {
            CronquistClassificationBranch clone = (CronquistClassificationBranch) super.clone();
            for (Map.Entry<RankName, ICronquistRank> entry : this.classificationCronquistMap.entrySet()) {
                RankName rankName = entry.getKey();
                ICronquistRank iCronquistRank = entry.getValue().clone();
                clone.put(rankName, iCronquistRank);
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
