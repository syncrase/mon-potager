package fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia;

import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedRank;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exceptions.InvalidRankName;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exceptions.NonExistentWikiPageException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exceptions.UnableToScrapClassification;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class WikipediaClassificationExtractor extends WikipediaConnector {
    public static final String LIST_SELECTOR_2 = "div.mw-category-group ul li a[href]";
    public static final String LIST_SELECTOR_1 = "div.CategoryTreeItem a[href]";
    public static final String HREF = "href";
    public static final String CLASSIFICATION_SELECTOR = "div.infobox_v3.large.taxobox_v3.plante.bordered";
    public static final String MAIN_CLASSIFICATION_SELECTOR = "table.taxobox_classification caption a";
    private final Logger log = LoggerFactory.getLogger(WikipediaClassificationExtractor.class);

    /**
     * @param item       Element from which the contained string is extract
     * @param cssQueries Ensemble de cssQueries qui sont testées. La première qui retourne un résultat est utilisée. La query DOIT retourner un unique élément
     * @return the text value of the node or an empty string
     */
    public String selectText(@NotNull Element item, String @NotNull ... cssQueries) {
        for (String cssQuery : cssQueries) {
            Optional<Element> el = item.select(cssQuery).stream().findFirst();
            if (el.isPresent()) {
                return el.map(Element::text).get();//.orElse("")
            }
        }
        return "";
    }

    public @NotNull Map<String, ScrapedRank> extractionRangsTaxonomiquesInferieurs(@NotNull Element mainTable) throws UnableToScrapClassification {
        // Ajout du rang taxonomique le plus inférieur
        Elements siblings = mainTable.siblingElements();
        // Ne garde que la première section de classification
        keepOnlyTheFirstClassificationSection(siblings);
        // Retire les éléments qui correspondent à autre chose que le rang ou le nom du taxon
        removeAllGarbageHtml(siblings);

        // Je ne souhaite que des couples (rang ; nom)
        if (siblings.size() % 2 != 0) {
            throw new UnableToScrapClassification("Impossible de récupérer le rang de base dans cet HTML :\n" + siblings);
        }

        return buildTheLevelRankAssociation(siblings);
    }

    private @NotNull Map<String, ScrapedRank> buildTheLevelRankAssociation(@NotNull Elements siblings) throws UnableToScrapClassification {
        Map<String, ScrapedRank> rangTaxonMap = new HashMap<>();
        Elements rangs = siblings.select("p.bloc a");
        rangs.removeIf(rang -> rang.text().equals("CITES"));
        Elements taxons = siblings.select("div.taxobox_classification b span");
        taxons.removeIf(taxon ->
            taxon.select("span").hasClass("cite_crochet") ||
                taxon.select("span.indicateur-langue").size() != 0
        );

        if (rangs.size() != taxons.size()) {
            throw new UnableToScrapClassification(String.format("Pas la même quantité de rangs et de noms de taxons récupérés. Traitement impossible. Rangs{%s}. Taxons{%s}", rangs, taxons));
        } else {
            for (int i = 0; i < rangs.size(); i++) {
                String classificationItemKey = rangs.get(i).text();
                try {
                    rangTaxonMap.put(classificationItemKey, new ScrapedRank(classificationItemKey, taxons.get(i).text(), taxons.get(i).attr("href")));
                } catch (InvalidRankName e) {
                    log.info(e.getLocalizedMessage());
                }
            }
        }
        return rangTaxonMap;
    }

    private void removeAllGarbageHtml(@NotNull Elements siblings) {
        siblings.removeIf(el ->
            el.className().equals("entete") ||
                el.className().equals("images") ||
                el.className().equals("legend") ||
                el.tagName().equals("table") ||
                el.select("p a").attr("title").contains("Classification") ||
                el.select("p a").attr("title").contains("Synonyme") ||
                el.tagName().equals("ul") ||
                el.select("p a").attr("title").contains("Statut de conservation") ||
                el.select("p sup").attr("class").equals("reference") ||
                el.select("p img").size() != 0 ||
                el.select("p").text().equals("Taxons de rang inférieur") ||
                el.select("p").text().equals("Répartition géographique") ||
                el.select("p").text().equals("(voir texte) ") ||
                el.select("div ul").size() != 0 ||
                el.select("center").size() != 0 ||
                el.select("div div img").attr("alt").equals("Attention !") ||
                el.select("p b").text().equals("DD Données insuffisantes") || //https://fr.wikipedia.org/wiki/Alisma_gramineum
                el.select("p.mw-empty-elt").size() != 0 ||
                el.select("p i").size() != 0 || //https://fr.wikipedia.org/wiki/Alternanthera_reineckii Le synonyme n'est pas dans le div synonyme
                el.select("p").text().contains("de rang inférieur") ||
                el.select("p br").size() != 0 ||
                el.select("div.left").size() != 0 && el.select("div.left").get(0).childrenSize() == 0 || //https://fr.wikipedia.org/wiki/Cryosophila_williamsii
                el.select("hr").size() != 0 ||
                el.select("dl").size() != 0 ||
                el.select("p.center small b").size() != 0 || // https://fr.wikipedia.org/wiki/Magnoliidae
                el.select("div.left").text().equals("Voir classification APG III") // https://fr.wikipedia.org/wiki/Angiospermae
        );
    }

    private void keepOnlyTheFirstClassificationSection(@NotNull Elements siblings) {
        Iterator<Element> iterator = siblings.iterator();
        boolean thresholdForRemoveNextItems = false;
        while (iterator.hasNext()) {
            Element next = iterator.next();
            boolean contains = next.select("p a").attr("title").contains("Classification");
            if (contains) {
                thresholdForRemoveNextItems = true;
            }
            if (thresholdForRemoveNextItems) {
                iterator.remove();
            }
        }
    }

    public String extractClassificationLevel(Element classificationItem) {
        return selectText(classificationItem, "th a");
    }

    public String extractRankName(Element classificationItem) {
        return selectText(classificationItem, "td span a", "td a");
    }

    public String extractUrl(@NotNull Element classificationItem) {
        return classificationItem.select("td a").attr("href");
    }

    public Elements extractList(@NotNull Document doc) {
        Elements wikiList = doc.select(LIST_SELECTOR_1);
        if (wikiList.size() > 0) {
            log.info(wikiList.size() + " éléments de liste récupérées");
            return wikiList;
        }
        // Si 0 alors c'est peut-être un autre html/css qui est utilisé
        wikiList = doc.select(LIST_SELECTOR_2);
        log.info(wikiList.size() + " éléments de liste récupérées avec l'autre CSS");
        return wikiList;
    }

    public Elements extractClassificationElements(@NotNull Element mainTable) {
        return mainTable.select("tbody tr");
    }

    public String extractUrlAttr(@NotNull Element listItem) {
        return listItem.attr(HREF);
    }

    public Elements extractEncadreDeClassification(String urlWiki) throws IOException, NonExistentWikiPageException {
        return getDocumentOf(urlWiki).select(CLASSIFICATION_SELECTOR);
    }

    public @NotNull String extractTypeOfMainClassification(@NotNull Elements encadreTaxonomique) {

        Elements taxoTitles = encadreTaxonomique.select(MAIN_CLASSIFICATION_SELECTOR);
        if (taxoTitles.size() == 0) {
            return "No extractClassification";
        }
        String taxoTitle = taxoTitles.get(0)// ne contient qu'un seul titre
            .childNode(0)// TextNode
            .toString();

        // Récupère le nom de l'encadré : ['Classification', 'Classification APG III (2009)']
        if (taxoTitle.contains("APG III")) {
            return "APG III";
        }
        if (taxoTitle.contains("Cronquist")) {
            Elements small = encadreTaxonomique.select("small");
            if (small.stream().anyMatch(el -> el.toString().contains("Taxon inexistant"))) {
                return "No Cronquist";
            }
            return "Cronquist";
        }

        // Récupère les clés de classification de la première table
        Elements tables = encadreTaxonomique.select("table.taxobox_classification");
        Element mainTable = tables.get(0);
        Elements taxoKeys = mainTable.select("tbody tr th a");
        for (Element classificationKeys : taxoKeys) {
            if (classificationKeys.text().contains("Clade")) {
                return "APG III";
            }
        }
        return "Cronquist";
    }

    public Element extractMainTableOfClassificationFrame(@NotNull Elements encadreTaxonomique) {
        return encadreTaxonomique.select("table.taxobox_classification").get(0);
    }
}
