package fr.syncrase.ecosyst.feature.add_plante.models;

import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomikRanks;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.InvalidRankName;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ScrapedRank {
    private final String taxonName;
    private final String classificationLevel;
    private final String url;


    public ScrapedRank(String classificationLevel, String rankName, String url) throws InvalidRankName {
        if (!checkNameValidity(classificationLevel, rankName)) {
            throw new InvalidRankName("Le rang " + rankName + " ne peut être un " + classificationLevel);
        }
        this.classificationLevel = classificationLevel;
        this.taxonName = rankName;
        this.url = url;
    }

    public String getClassificationLevel() {
        return classificationLevel;
    }

    public String getTaxonName() {
        return taxonName;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Vérification basique de la concordance entre le rang et le nom du rankName
     *
     * @param rankName            Nom du rankName dont le suffixe doit correspondre au rang
     * @param classificationLevel rang du rankName
     * @return S'il y a une incohérence, retourne null
     */
    @Contract(pure = true)
    private boolean checkNameValidity(String classificationLevel, @NotNull String rankName) {
        return (!rankName.endsWith(CronquistTaxonomikRanks.ORDRE.getSuffix()) || Arrays.asList(new String[]{"Ordre"}).contains(classificationLevel)) &&
            (!rankName.endsWith(CronquistTaxonomikRanks.FAMILLE.getSuffix()) || Arrays.asList(new String[]{"Famille"}).contains(classificationLevel));
    }

}
