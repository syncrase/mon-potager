package fr.syncrase.ecosyst.feature.add_plante.classification;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomikRanks;
import liquibase.repackaged.org.apache.commons.collections4.map.LinkedMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

// TODO extends AbstractLinkedMap<RankName, CronquistRank>
public class CronquistClassificationBranch implements Cloneable {

    /**
     * Liste de tous les rangs de la classification<br>
     * L'élément 0 est le rang le plus haut : le super règne
     */
    private LinkedMap<CronquistTaxonomikRanks, CronquistRank> classificationCronquistMap;

    /**
     * Construit une classification vierge
     */
    public CronquistClassificationBranch() {
        initEmptyClassification();
    }

    /**
     * Construit une classification à partir d'un rang
     */
    public CronquistClassificationBranch(@NotNull CronquistRank cronquistRank) {
        initEmptyClassification();
        CronquistRank currentRank = cronquistRank;
        while (currentRank != null) {
            classificationCronquistMap.put(currentRank.getRank(), currentRank);
            currentRank = currentRank.getParent();
        }
        this.clearTail();
    }

    @Nullable
    private CronquistRank getParent(@NotNull CronquistRank rank) {
        boolean isSuperRegne = rank.getRank().name().equals(CronquistTaxonomikRanks.DOMAINE);
        if (isSuperRegne) {
            return null;
        }
        return classificationCronquistMap.get(rank.getRank().getRangSuperieur());
    }

    private void initEmptyClassification() {
        classificationCronquistMap = new LinkedMap<>(34);
        initDefaultValues();
    }

    private void initDefaultValues() {
        CronquistTaxonomikRanks[] rangsDisponibles = CronquistTaxonomikRanks.values();
        for (CronquistTaxonomikRanks hauteurDeRangEnCours : rangsDisponibles) {
            classificationCronquistMap.put(hauteurDeRangEnCours, getDefaultRank(hauteurDeRangEnCours));
        }
    }

    private CronquistRank getDefaultRank(CronquistTaxonomikRanks hauteurDeRangEnCours) {
        //  TODO add nom
        //   return new CronquistRank().rank(hauteurDeRangEnCours).addNom(new AtomicClassificationNom().nomFr(DEFAULT_NAME_FOR_CONNECTOR_RANK));;
        return null;
    }

    /**
     * La raison d'être des rangs de liaison (sans nom) est de lier deux rangs dont les noms sont connus.<br>
     * Cette méthode nettoie les rangs liaison inférieurs au dernier rang de la classification
     */
    public void clearTail() {
        CronquistTaxonomikRanks[] rangsDisponibles = CronquistTaxonomikRanks.values();
        for (int positionDansLaClassification = rangsDisponibles.length - 1; positionDansLaClassification >= 0; positionDansLaClassification--) {
            CronquistTaxonomikRanks nomDuRangEnCours = rangsDisponibles[positionDansLaClassification];
            // TODO fix this
            //            boolean cestUnRangConnu = classificationCronquistMap.get(nomDuRangEnCours) != null && !classificationCronquistMap.get(nomDuRangEnCours).isRangDeLiaison();
            //            if (cestUnRangConnu) {
            //                break;
            //            }
            removeTaxon(nomDuRangEnCours);
        }
    }

    private void removeTaxon(@NotNull CronquistTaxonomikRanks nomDuRangEnCours) {
        classificationCronquistMap.remove(nomDuRangEnCours);
    }

    public CronquistRank getRang(CronquistTaxonomikRanks rang) {
        if (rang != null) {
            return classificationCronquistMap.get(rang);
        } else {
            return null;
        }
    }

    public CronquistRank getRangDeBase() {
        return classificationCronquistMap.get(classificationCronquistMap.lastKey());
    }

    public Collection<CronquistRank> getClassification() {
        return classificationCronquistMap.values();
    }

    public LinkedMap<CronquistTaxonomikRanks, CronquistRank> getClassificationBranch() {
        return classificationCronquistMap;
    }

    public CronquistRank put(CronquistTaxonomikRanks currentRankName, CronquistRank currentRank) {
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
        classificationCronquistMap.forEach((rankName, rank) -> {
        });
        // TODO fix this
        //            rank.setRankName(rankName)
        //        );
    }

    @Override
    public CronquistClassificationBranch clone() {
        try {
            CronquistClassificationBranch clone = (CronquistClassificationBranch) super.clone();
            for (Map.Entry<CronquistTaxonomikRanks, CronquistRank> entry : this.classificationCronquistMap.entrySet()) {
                CronquistTaxonomikRanks rankName = entry.getKey();
                // TODO fix this
                //                CronquistRank iCronquistRank = entry.getValue().clone();
                //                clone.put(rankName, iCronquistRank);
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
