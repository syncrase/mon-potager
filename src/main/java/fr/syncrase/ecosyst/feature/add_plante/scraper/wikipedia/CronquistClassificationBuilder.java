package fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.scraper.ScrapedRank;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.InvalidRankName;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.UnableToScrapClassification;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class CronquistClassificationBuilder {

    private final Logger log = LoggerFactory.getLogger(CronquistClassificationBuilder.class);
    CronquistClassificationBranch cronquistClassification;
    WikipediaClassificationExtractor wikipediaClassificationExtractor;

    /**
     * Extract extractClassification from the wikipedia extractClassification table
     *
     * @param mainTable extractClassification table
     * @return The generated extractClassification object
     */
    public CronquistClassificationBranch getClassificationFromHtml(@NotNull Element mainTable) throws UnableToScrapClassification {

        cronquistClassification = new CronquistClassificationBranch();
        wikipediaClassificationExtractor = new WikipediaClassificationExtractor();
        Elements elementsDeClassification = wikipediaClassificationExtractor.extractClassificationElements(mainTable);

        for (Element classificationItem : elementsDeClassification) {
            setCronquistTaxonomyItemFromElement(classificationItem);// TODO remove side effect
        }

        Map<String, ScrapedRank> rangTaxonMap = wikipediaClassificationExtractor.extractionRangsTaxonomiquesInferieurs(mainTable);
        if (rangTaxonMap.keySet().size() > 0) {
            rangTaxonMap.forEach((classificationLevel, rangValues) -> setCronquistTaxonomyItem(rangValues));
        }
        cronquistClassification.clearTail();
        //cronquistClassification.inferAllRank();
        return cronquistClassification;
    }

    /**
     * Extract rank values from JSOUP Element
     *
     * @param classificationItem row of the taxonomy table which contains taxonomy rank and name
     */
    private void setCronquistTaxonomyItemFromElement(Element classificationItem) {
        String classificationLevel = wikipediaClassificationExtractor.extractClassificationLevel(classificationItem);
        ScrapedRank scrapedRankData;
        try {
            scrapedRankData = new ScrapedRank(
                classificationLevel,
                wikipediaClassificationExtractor.extractRankName(classificationItem),
                wikipediaClassificationExtractor.extractUrl(classificationItem)
            );
            setCronquistTaxonomyItem(scrapedRankData);
        } catch (InvalidRankName e) {
            log.info(e.getLocalizedMessage());
        }
    }

    /**
     * @param rangValues taxon
     */
    private void setCronquistTaxonomyItem(@NotNull ScrapedRank rangValues) {
        switch (rangValues.getClassificationLevel()) {
            case "Super-Règne":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SUPERREGNE), rangValues.getTaxonName());
                break;
            case "Règne":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.REGNE), rangValues.getTaxonName());
                break;
            case "Sous-règne":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SOUSREGNE), rangValues.getTaxonName());
                break;
            case "Rameau":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.RAMEAU), rangValues.getTaxonName());
                break;
            case "Infra-règne":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.INFRAREGNE), rangValues.getTaxonName());
                break;
            case "Super-division":
            case "Super-embranchement":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SUPEREMBRANCHEMENT), rangValues.getTaxonName());
                break;
            case "Division":
            case "Embranchement":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT), rangValues.getTaxonName());
                break;
            case "Sous-division":
            case "Sous-embranchement":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SOUSEMBRANCHEMENT), rangValues.getTaxonName());
                break;
            case "Infra-embranchement":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.INFRAEMBRANCHEMENT), rangValues.getTaxonName());
                break;
            case "Micro-embranchement":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.MICROEMBRANCHEMENT), rangValues.getTaxonName());
                break;
            case "Super-classe":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SUPERCLASSE), rangValues.getTaxonName());
                break;
            case "Classe":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.CLASSE), rangValues.getTaxonName());
                break;
            case "Sous-classe":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SOUSCLASSE), rangValues.getTaxonName());
                break;
            case "Infra-classe":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.INFRACLASSE), rangValues.getTaxonName());
                break;
            case "Super-ordre":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SUPERORDRE), rangValues.getTaxonName());
                break;
            case "Ordre":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.ORDRE), rangValues.getTaxonName());
                break;
            case "Sous-ordre":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SOUSORDRE), rangValues.getTaxonName());
                break;
            case "Infra-ordre":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.INFRAORDRE), rangValues.getTaxonName());
                break;
            case "Micro-ordre":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.MICROORDRE), rangValues.getTaxonName());
                break;
            case "Super-famille":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SUPERFAMILLE), rangValues.getTaxonName());
                break;
            case "Famille":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.FAMILLE), rangValues.getTaxonName());
                break;
            case "Sous-famille":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SOUSFAMILLE), rangValues.getTaxonName());
                break;
            case "Tribu":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.TRIBU), rangValues.getTaxonName());
                break;
            case "Sous-tribu":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SOUSTRIBU), rangValues.getTaxonName());
                break;
            case "Genre":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.GENRE), rangValues.getTaxonName());
                break;
            case "Sous-genre":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SOUSGENRE), rangValues.getTaxonName());
                break;
            case "Section":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SECTION), rangValues.getTaxonName());
                break;
            case "Sous-section":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SOUSSECTION), rangValues.getTaxonName());
                break;
            case "Espèce":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.ESPECE), rangValues.getTaxonName());
                break;
            case "Sous-espèce":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SOUSESPECE), rangValues.getTaxonName());
                break;
            case "Variété":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.VARIETE), rangValues.getTaxonName());
                break;
            case "Sous-variété":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SOUSVARIETE), rangValues.getTaxonName());
                break;
            case "Forme":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.FORME), rangValues.getTaxonName());
                break;
            case "Sous-forme":
                updateRank(cronquistClassification.getRang(CronquistTaxonomicRank.SOUSFORME), rangValues.getTaxonName());
                break;
            case "":
                log.warn("ATTENTION Le niveau du rang extrait du wiki est une chaine vide");
                break;
            default:
                log.warn(rangValues.getClassificationLevel() + " est un rang qui n'est pas pris en charge par du Cronquist");

        }
    }

    private void updateRank(@NotNull CronquistRank rank, String rankNomFr) {
        rank.setNom(rankNomFr);
    }

}
