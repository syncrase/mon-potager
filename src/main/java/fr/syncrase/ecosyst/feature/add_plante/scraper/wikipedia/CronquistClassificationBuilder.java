package fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia;

import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.classification.consistency.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.ICronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.atomic.AtomicClassificationNom;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.atomic.AtomicUrl;
import fr.syncrase.ecosyst.feature.add_plante.classification.enumeration.RankName;
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
    public CronquistClassificationBranch getClassification(@NotNull Element mainTable) throws ClassificationReconstructionException, UnableToScrapClassification {

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
                updateRank(cronquistClassification.getRang(RankName.SUPERREGNE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Règne":
                updateRank(cronquistClassification.getRang(RankName.REGNE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-règne":
                updateRank(cronquistClassification.getRang(RankName.SOUSREGNE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Rameau":
                updateRank(cronquistClassification.getRang(RankName.RAMEAU), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Infra-règne":
                updateRank(cronquistClassification.getRang(RankName.INFRAREGNE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Super-division":
            case "Super-embranchement":
                updateRank(cronquistClassification.getRang(RankName.SUPEREMBRANCHEMENT), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Division":
            case "Embranchement":
                updateRank(cronquistClassification.getRang(RankName.EMBRANCHEMENT), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-division":
            case "Sous-embranchement":
                updateRank(cronquistClassification.getRang(RankName.SOUSEMBRANCHEMENT), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Infra-embranchement":
                updateRank(cronquistClassification.getRang(RankName.INFRAEMBRANCHEMENT), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Micro-embranchement":
                updateRank(cronquistClassification.getRang(RankName.MICROEMBRANCHEMENT), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Super-classe":
                updateRank(cronquistClassification.getRang(RankName.SUPERCLASSE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Classe":
                updateRank(cronquistClassification.getRang(RankName.CLASSE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-classe":
                updateRank(cronquistClassification.getRang(RankName.SOUSCLASSE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Infra-classe":
                updateRank(cronquistClassification.getRang(RankName.INFRACLASSE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Super-ordre":
                updateRank(cronquistClassification.getRang(RankName.SUPERORDRE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Ordre":
                updateRank(cronquistClassification.getRang(RankName.ORDRE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-ordre":
                updateRank(cronquistClassification.getRang(RankName.SOUSORDRE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Infra-ordre":
                updateRank(cronquistClassification.getRang(RankName.INFRAORDRE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Micro-ordre":
                updateRank(cronquistClassification.getRang(RankName.MICROORDRE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Super-famille":
                updateRank(cronquistClassification.getRang(RankName.SUPERFAMILLE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Famille":
                updateRank(cronquistClassification.getRang(RankName.FAMILLE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-famille":
                updateRank(cronquistClassification.getRang(RankName.SOUSFAMILLE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Tribu":
                updateRank(cronquistClassification.getRang(RankName.TRIBU), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-tribu":
                updateRank(cronquistClassification.getRang(RankName.SOUSTRIBU), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Genre":
                updateRank(cronquistClassification.getRang(RankName.GENRE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-genre":
                updateRank(cronquistClassification.getRang(RankName.SOUSGENRE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Section":
                updateRank(cronquistClassification.getRang(RankName.SECTION), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-section":
                updateRank(cronquistClassification.getRang(RankName.SOUSSECTION), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Espèce":
                updateRank(cronquistClassification.getRang(RankName.ESPECE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-espèce":
                updateRank(cronquistClassification.getRang(RankName.SOUSESPECE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Variété":
                updateRank(cronquistClassification.getRang(RankName.VARIETE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-variété":
                updateRank(cronquistClassification.getRang(RankName.SOUSVARIETE), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Forme":
                updateRank(cronquistClassification.getRang(RankName.FORME), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "Sous-forme":
                updateRank(cronquistClassification.getRang(RankName.SOUSFORME), rangValues.getTaxonName(), rangValues.getUrl());
                break;
            case "":
                log.warn("ATTENTION Le niveau du rang extrait du wiki est une chaine vide");
                break;
            default:
                log.warn(rangValues.getClassificationLevel() + " est un rang qui n'est pas pris en charge par du Cronquist");

        }
    }

    private void updateRank(@NotNull ICronquistRank rank, String rankNomFr, String url) {
        rank.addNameToCronquistRank(new AtomicClassificationNom().nomFr(rankNomFr));
        if ((url != null) && (!url.equals(""))) {
            rank.addUrl(new AtomicUrl().url(Wikipedia.getValidUrl(url)));
        }
    }

}
