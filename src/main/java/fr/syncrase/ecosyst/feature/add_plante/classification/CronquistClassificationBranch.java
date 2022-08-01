package fr.syncrase.ecosyst.feature.add_plante.classification;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomikRanks;
import liquibase.repackaged.org.apache.commons.collections4.map.LinkedMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CronquistClassificationBranch {

    /**
     * Deux rangs taxonomiques sont séparés par d'autres rangs dont on ne connait pas forcément le nom.<br>
     * Une valeur par défaut permet de lier ces deux rangs avec des rangs vides.<br>
     * Si ultérieurement ces rangs sont déterminés, les valeurs par défaut sont mises à jour
     */
    public static final String DEFAULT_NAME_FOR_CONNECTOR_RANK = null;

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
        return new CronquistRank().rank(hauteurDeRangEnCours).nom(DEFAULT_NAME_FOR_CONNECTOR_RANK);
    }

    /**
     * La raison d'être des rangs de liaison (sans nom) est de lier deux rangs dont les noms sont connus.<br>
     * Cette méthode nettoie les rangs liaison inférieurs au dernier rang de la classification
     */
    public void clearTail() {
        CronquistTaxonomikRanks[] rangsDisponibles = CronquistTaxonomikRanks.values();
        for (int positionDansLaClassification = rangsDisponibles.length - 1; positionDansLaClassification >= 0; positionDansLaClassification--) {
            CronquistTaxonomikRanks nomDuRangEnCours = rangsDisponibles[positionDansLaClassification];

            boolean cestUnRangConnu = classificationCronquistMap.get(nomDuRangEnCours) != null && !isRangDeLiaison(classificationCronquistMap.get(nomDuRangEnCours));
            if (cestUnRangConnu) {
                break;
            }
            removeTaxon(nomDuRangEnCours);
        }
    }

    public static boolean isRangDeLiaison(CronquistRank rangCible) {
        return Objects.equals(rangCible.getNom(), DEFAULT_NAME_FOR_CONNECTOR_RANK);
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

    public CronquistRank getNestedClassification() {
        CronquistRank rank, tmpRank;
        rank = classificationCronquistMap.get(classificationCronquistMap.lastKey());
        tmpRank = rank;
        while (tmpRank.getRank().getRangSuperieur() != null) {
            classificationCronquistMap.get(tmpRank.getRank().getRangSuperieur()).addChildren(tmpRank);
            tmpRank.setParent(classificationCronquistMap.get(tmpRank.getRank().getRangSuperieur()));
            tmpRank = tmpRank.getParent();
        }
        return rank;
    }

    public Collection<CronquistRank> getClassification() {
        return classificationCronquistMap.values();
    }

    public TreeSet<CronquistRank> getClassificationSet() {
        TreeSet<CronquistRank> cronquistRanks = new TreeSet<>(Comparator.comparing(CronquistRank::getRank, (rang1, rang2) -> {
            if (rang1 == null && rang2 == null) {
                return 0;
            }
            if (rang1 == null) {
                return 1;
            }
            if (rang2 == null) {
                return -1;
            }
            int i = rang1.isHighestRankOf(rang2) ? 1 : rang1.isSameRankOf(rang2) ? 0 : -1;
            return i;
        }));
        Collection<CronquistRank> values = classificationCronquistMap.values();
        cronquistRanks.addAll(values);
        return cronquistRanks;
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
        classificationCronquistMap.forEach((rankName, rank) -> rank.setNom(rankName.getValue()));
    }
}
