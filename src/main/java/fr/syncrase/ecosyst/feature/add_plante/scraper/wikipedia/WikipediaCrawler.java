package fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia;

import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.classification.consistency.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exceptions.UnableToScrapClassification;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class WikipediaCrawler {

    private final Logger log = LoggerFactory.getLogger(WikipediaCrawler.class);

    WikipediaHtmlExtractor wikipediaHtmlExtractor;

    public WikipediaCrawler() {
        wikipediaHtmlExtractor = new WikipediaHtmlExtractor();
    }

    public @Nullable CronquistClassificationBranch extractClassificationFromWiki(String urlWiki) {
        try {
            log.info("Get classification from : " + urlWiki);
            Elements encadreTaxonomique = wikipediaHtmlExtractor.extractEncadreDeClassification(urlWiki);

            CronquistClassificationBranch cronquistClassificationBranch = extractPremiereClassification(encadreTaxonomique);
            return cronquistClassificationBranch;
        } catch (SocketTimeoutException e) {
            log.error("La page Wikipedia ne répond pas {}\n. Vérifier la connexion internet!", urlWiki);
        } catch (IOException e) {
            log.error("Problème d'accès lors de l'extraction des données de la page Wikipedia {}", urlWiki);
        }
        return null;
    }

    private @Nullable CronquistClassificationBranch extractPremiereClassification(@NotNull Elements encadreTaxonomique) {
        // TODO Contient plusieurs classifications, en général Cronquist et APGN
        // TODO dans l'état actuel des choses, je ne garde que la PREMIERE section !

        switch (wikipediaHtmlExtractor.extractTypeOfMainClassification(encadreTaxonomique)) {
            case "APG III":
                log.info("APG III to be implemented");
                break;
            case "Cronquist":
                CronquistClassificationBranch cronquistClassificationBranch = extractionCronquist(encadreTaxonomique);
                return cronquistClassificationBranch;
            case "No extractClassification":
                log.info("No classification table found in this page");
                break;
            case "No Cronquist":
                log.info("Taxon inexistant en Cronquist");
                break;
            default:
                log.warn("Classification's type cannot be determined");
        }
        return null;
    }

    /**
     * Récupère la extractClassification contenue dans l'encadré de classification.
     * Vérifications préliminaires :
     * <ul>
     *     <li>Les noms dont le suffixe ne correspond pas au niveau de extractClassification ne sont pas intégrés à la extractClassification.</li>
     *     <li>Chacun des rangs subi une vérification pour s'assurer que le niveau de extractClassification est le bon</li>
     * </ul>
     *
     * @param encadreTaxonomique Elements HTML qui contiennent l'encadré de classification
     * @return La classification extraite ayant subi les vérifications préliminaires
     */
    private CronquistClassificationBranch extractionCronquist(@NotNull Elements encadreTaxonomique) {

        CronquistClassificationBuilder cronquistClassificationBuilder = new CronquistClassificationBuilder();
        CronquistClassificationBranch cronquistClassification = null;
        try {
            Element mainTable = wikipediaHtmlExtractor.extractMainTableOfClassificationFrame(encadreTaxonomique);
            cronquistClassification = cronquistClassificationBuilder.getClassification(mainTable);

        } catch (ClassificationReconstructionException e) {
            log.error("Impossible d'extraire la classification de la page");
            e.printStackTrace();
        } catch (UnableToScrapClassification e) {
            log.error(e.getMessage());
        }

        log.info("Created Cronquist classification : " + cronquistClassification);
        return cronquistClassification;
    }

}
