package fr.syncrase.ecosyst.feature.add_plante.classification.consistency;

import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.ClassificationNom;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.Url;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class CronquistRankUtils {
    public static boolean isRangDeLiaison(CronquistRank rangCible) {
        return hasThisName(rangCible, CronquistRank.DEFAULT_NAME_FOR_CONNECTOR_RANK);
    }

    private static boolean hasThisName(@NotNull CronquistRank rangCible, String defaultNameForConnectorRank) {

        TreeSet<ClassificationNom> classificationNoms = getClassificationNomTreeSet();
        classificationNoms.addAll(rangCible.getNoms());
        return classificationNoms.contains(new ClassificationNom().nomFr(defaultNameForConnectorRank));
    }

    /**
     * Retourne un set vide de ClassificationNom dont la comparaison se fait sur le nomFr
     *
     * @return Tree set vide avec comparaison personnalisée
     */
    @NotNull
    public static TreeSet<ClassificationNom> getClassificationNomTreeSet() {
        return new TreeSet<>(Comparator.comparing(ClassificationNom::getNomFr, (nomFrExistant, nomFrAAjouter) -> {
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

    public static boolean isRangSignificatif(CronquistRank rangSource) {
        return !isRangDeLiaison(rangSource);
    }

    public static void removeAllNames(@NotNull CronquistRank rangCible, Set<Long> ranksIdToDelete) {
        rangCible.getNoms().removeIf(iClassificationNom -> ranksIdToDelete.contains(iClassificationNom.getId()));
    }

    public static Object getNomsIds(@NotNull CronquistRank rangSource) {
        return rangSource.getNoms().stream().map(ClassificationNom::getId).collect(Collectors.toSet());
    }

    public static void addNoms(CronquistRank rangCible, @NotNull Set<ClassificationNom> noms) {

        for (ClassificationNom classificationNom : noms) {
            if (isRangDeLiaison(rangCible)) {
                rangCible.getNoms().removeIf(nom -> nom.getNomFr() == null);
            }
            rangCible.addNoms(classificationNom);
        }
    }

    public static Set<CronquistRank> setParentToAllTaxons(CronquistRank newParent, @NotNull Set<CronquistRank> taxons) {

        return taxons.stream().peek(taxon -> {
            taxon.setParent(newParent);
            newParent.addChildren(taxon);
        }).collect(Collectors.toSet());
    }

    public static void addUrls(CronquistRank rangCible, @NotNull Set<Url> urls) {

        for (Url url : urls) {
            rangCible.addUrls(url);
        }
    }

}
