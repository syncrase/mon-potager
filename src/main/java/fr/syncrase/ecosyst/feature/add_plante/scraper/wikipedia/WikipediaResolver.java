package fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia;

import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.NonExistentWikiPageException;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WikipediaResolver extends WikipediaConnector {

    private final Logger log = LoggerFactory.getLogger(WikipediaResolver.class);

    /*
     * Cas de test
     * - j'envoie ail, je récupère
     * - j'envoie morelle noire, je récupère
     *
     * @param name
     * @return
     * @throws NonExistentWikiPageException
     */

    public boolean checkIfPageExists(String urlS) {
        URL url;
        try {
            url = new URL(urlS);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("HEAD");
            if (huc.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }


    @Nullable
    public Document getDocumentIfContainingClassification(String url) throws NonExistentWikiPageException {
        Document document = getDocumentOf(url);
        if (!document.childNode(0).toString().equals("<!doctype html>")) {
            // Parfois jsoup retourne un document vide, en exécutant une deuxième fois ça fonctionne...
            document = getDocumentOf(url);
        }
        // If I found a div or table containing a classification
        for (String xpath : WikipediaClassificationExtractor.CLASSIFICATION_SELECTOR) {
            if (document.select(xpath).size() != 0) return document;
        }
        return null;
    }
}
