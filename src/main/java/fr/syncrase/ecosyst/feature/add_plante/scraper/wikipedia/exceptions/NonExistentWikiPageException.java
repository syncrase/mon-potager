package fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exceptions;

public class NonExistentWikiPageException extends Throwable {
    public NonExistentWikiPageException(String s) {
        super(s);
    }
}
