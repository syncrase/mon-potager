package fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomikRanks;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exceptions.InvalidRankName;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exceptions.UnableToScrapClassification;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class CronquistClassificationBuilder {

    private final Logger log = LoggerFactory.getLogger(CronquistClassificationBuilder.class);
    CronquistClassificationBranch cronquistClassification;
    WikipediaHtmlExtractor wikipediaHtmlExtractor;

    /**
     * Extract extractClassification from the wikipedia extractClassification table
     *
     * @param mainTable extractClassification table
     * @return The generated extractClassification object
     */
    public CronquistClassificationBranch getClassification(@NotNull Element mainTable) throws UnableToScrapClassification {

        cronquistClassification = new CronquistClassificationBranch();
        wikipediaHtmlExtractor = new WikipediaHtmlExtractor();
        Elements elementsDeClassification = wikipediaHtmlExtractor.extractClassificationElements(mainTable);

        for (Element classificationItem : elementsDeClassification) {
            setCronquistTaxonomyItemFromElement(classificationItem);// TODO remove side effect
        }

        Map<String, ScrapedRank> rangTaxonMap = wikipediaHtmlExtractor.extractionRangsTaxonomiquesInferieurs(mainTable);
        if (rangTaxonMap.keySet().size() > 0) {
            rangTaxonMap.forEach((classificationLevel, rangValues) -> setCronquistTaxonomyItem(rangValues));
        }
        cronquistClassification.clearTail();
        cronquistClassification.inferAllRank();
        return cronquistClassification;
    }

    /**
     * Extract rank values from JSOUP Element
     *
     * @param classificationItem row of the taxonomy table which contains taxonomy rank and name
     */
    private void setCronquistTaxonomyItemFromElement(Element classificationItem) {
        String classificationLevel = wikipediaHtmlExtractor.extractClassificationLevel(classificationItem);
        ScrapedRank scrapedRankData;
        try {
            scrapedRankData = new ScrapedRank(
                classificationLevel,
                wikipediaHtmlExtractor.extractRankName(classificationItem),
                wikipediaHtmlExtractor.extractUrl(classificationItem)
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
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SUPERREGNE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Règne":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.REGNE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-règne":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SOUSREGNE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Rameau":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.RAMEAU), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Infra-règne":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.INFRAREGNE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Super-division":
            case "Super-embranchement":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SUPEREMBRANCHEMENT), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Division":
            case "Embranchement":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.EMBRANCHEMENT), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-division":
            case "Sous-embranchement":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SOUSEMBRANCHEMENT), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Infra-embranchement":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.INFRAEMBRANCHEMENT), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Micro-embranchement":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.MICROEMBRANCHEMENT), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Super-classe":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SUPERCLASSE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Classe":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.CLASSE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-classe":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SOUSCLASSE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Infra-classe":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.INFRACLASSE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Super-ordre":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SUPERORDRE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Ordre":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.ORDRE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-ordre":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SOUSORDRE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Infra-ordre":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.INFRAORDRE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Micro-ordre":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.MICROORDRE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Super-famille":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SUPERFAMILLE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Famille":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.FAMILLE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-famille":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SOUSFAMILLE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Tribu":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.TRIBU), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-tribu":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SOUSTRIBU), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Genre":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.GENRE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-genre":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SOUSGENRE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Section":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SECTION), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-section":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SOUSSECTION), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Espèce":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.ESPECE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-espèce":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SOUSESPECE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Variété":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.VARIETE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-variété":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SOUSVARIETE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Forme":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.FORME), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-forme":
                updateRank(cronquistClassification.getRang(CronquistTaxonomikRanks.SOUSFORME), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "":
                log.warn("ATTENTION Le niveau du rang extrait du wiki est une chaine vide");
                break;
            default:
                log.warn(rangValues.getClassificationLevel() + " est un rang qui n'est pas pris en charge par du Cronquist");

        }
    }

    private void updateRank(@NotNull CronquistRank rank, String rankNomFr, String url) {
        // TODO fix this
        //        rank.addNameToCronquistRank(new AtomicClassificationNom().nomFr(rankNomFr));
        //        if ((url != null) && (!url.equals(""))) {
        //            rank.addUrl(new AtomicUrl().url(Wikipedia.getValidUrl(url)));
        //        }
    }

}
