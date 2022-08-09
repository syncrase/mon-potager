package fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia;

import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.NonExistentWikiPageException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikipediaResolver extends WikipediaConnector {

    public static final String WIKI_SEARCH_URL = "https://fr.wikipedia.org/wiki/";
    private final Logger log = LoggerFactory.getLogger(WikipediaResolver.class);

    /**
     * Cas de test
     * - j'envoie ail, je récupère
     * - j'envoie morelle noire, je récupère
     *
     * @param name
     * @return
     * @throws NonExistentWikiPageException
     */
    public String resolveUrlForThisPlant(String name) throws NonExistentWikiPageException {
        log.info("Search for {} on Wikipedia", name);
        String url = WIKI_SEARCH_URL + name;
        //Elements encadreTaxonomique = wikipediaHtmlExtractor.lookFor(urlWiki);
        Elements encadreTaxonomique = null;
        Document document = getDocumentOf(url);

        // If I found a div or table containing a classification
        for (String xpath : WikipediaClassificationExtractor.CLASSIFICATION_SELECTOR) {
            encadreTaxonomique = document.select(xpath);
            if (encadreTaxonomique.size() != 0) return document.location();
        }

        // TODO If not, trying to find an url containing the plant's name

        return null;
    }
}
