package fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia;

import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.NonExistentWikiPageException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public abstract class WikipediaConnector {
    protected Document getDocumentOf(String urlWiki) throws NonExistentWikiPageException {
        try {
            return Jsoup.connect(urlWiki)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .referrer("https://www.google.com")
                .header("Content-Type", "application/json")
                .header("Cache-control", "no-cache")
                .header("Accept-Encoding", "gzip, deflate")
                .get();
        } catch (IOException e) {
            String message = e.getMessage();
            if (message.contains("Status=404")) {
                throw new NonExistentWikiPageException(message.split("URL=")[1]);
            }
            throw new RuntimeException(e);
        }
    }
}
