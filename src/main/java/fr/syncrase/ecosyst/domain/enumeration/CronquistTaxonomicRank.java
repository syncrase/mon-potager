package fr.syncrase.ecosyst.domain.enumeration;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * The CronquistTaxonomicRank enumeration.
 */
public enum CronquistTaxonomicRank {
    DOMAINE("Domaine", null),
    SOUSDOMAINE("SuperRegne", null),
    SUPERREGNE("SuperRegne", null),
    REGNE("Regne", null),
    SOUSREGNE("SousRegne", null),
    RAMEAU("Rameau", null),
    INFRAREGNE("InfraRegne", null),
    SUPEREMBRANCHEMENT("SuperEmbranchement", null),
    EMBRANCHEMENT("Embranchement", null),
    SOUSEMBRANCHEMENT("SousEmbranchement", null),
    INFRAEMBRANCHEMENT("InfraEmbranchement", null),
    MICROEMBRANCHEMENT("MicroEmbranchement", null),
    SUPERCLASSE("SuperClasse", null),
    CLASSE("Classe", null),
    SOUSCLASSE("SousClasse", null),
    INFRACLASSE("InfraClasse", null),
    SUPERORDRE("SuperOrdre", null),
    ORDRE("Ordre", "les"),
    SOUSORDRE("SousOrdre", null),
    INFRAORDRE("InfraOrdre", null),
    MICROORDRE("MicroOrdre", null),
    SUPERFAMILLE("SuperFamille", null),
    FAMILLE("Famille", "eae"),
    SOUSFAMILLE("SousFamille", null),
    TRIBU("Tribu", null),
    SOUSTRIBU("SousTribu", null),
    GENRE("Genre", null),
    SOUSGENRE("SousGenre", null),
    SECTION("Section", null),
    SOUSSECTION("SousSection", null),
    ESPECE("Espece", null),
    SOUSESPECE("SousEspece", null),
    VARIETE("Variete", null),
    SOUSVARIETE("SousVariete", null),
    FORME("Forme", null),
    SOUSFORME("SousForme", null);

    private final String value;

    CronquistTaxonomicRank(String value, String suffix) {
        this.value = value;
        this.suffix = suffix;
    }

    public String getValue() {
        return value;
    }

    private final String suffix;

    private static final CronquistTaxonomikRanks[] allRanks = values();

    public String getSuffix() {
        return suffix;
    }

    @Contract(pure = true)
    public @Nullable CronquistTaxonomikRanks getRangSuperieur() {
        int parentOrdinal = this.ordinal() - 1;
        if (parentOrdinal > -1) {
            return allRanks[parentOrdinal];
        } else {
            return null;
        }
    }

    @Contract(pure = true)
    public @Nullable CronquistTaxonomikRanks getRangInferieur() {
        int childOrdinal = this.ordinal() + 1;
        if (childOrdinal < allRanks.length) {
            return allRanks[childOrdinal];
        } else {
            return null;
        }
    }
}
