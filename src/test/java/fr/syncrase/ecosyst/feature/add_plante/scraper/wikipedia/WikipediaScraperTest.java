package fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WikipediaScraperTest {

    WikipediaScraper wikipediaScraper;

    public WikipediaScraperTest() {
        this.wikipediaScraper = new WikipediaScraper();
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void extractClassificationFromWiki() {
    }

    @Test
    public void extractClassificationFromWiki_ErreurDansLeNomDuRangTaxonomique() {
        // Le genre contient Gentianaceae mais c'est une famille, le genre est ignoré (à voir avec le scraper)
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Monodiella";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        String nomsDuGenreMonodiella = classification.getRang(CronquistTaxonomicRank.GENRE).getNom();
        Assertions.assertTrue(nomsDuGenreMonodiella.contains("Monodiella"), "Le genre monodiella doit être 'Monodiella' pas 'Gentianaceae'");
        CronquistRank lowestRank = classification.getLowestRank();
        Assertions.assertSame(lowestRank.getRank(), CronquistTaxonomicRank.GENRE, "Le premier rang doit être un genre");
        Assertions.assertNotNull(lowestRank.getNom(), "Le premier rang doit être un rang significatif");
    }

    @Test
    public void extractClassificationFromWiki_Allium() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Allium";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Allium", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Liliaceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Liliales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Liliidae", classification.getRang(CronquistTaxonomicRank.SOUSCLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Liliopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliophyta", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Tracheobionta", classification.getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_Atalaya() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Atalaya_(genre)";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Atalaya", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Sapindaceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Sapindales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliophyta", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_Arjona() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Arjona";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Arjona", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Santalaceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Santalales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Rosidae", classification.getRang(CronquistTaxonomicRank.SOUSCLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliophyta", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Tracheobionta", classification.getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_Cossinia() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Cossinia";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Cossinia", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Sapindaceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Sapindales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Rosidae", classification.getRang(CronquistTaxonomicRank.SOUSCLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliophyta", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Tracheobionta", classification.getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_ErableDeCrete() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/%C3%89rable_de_Cr%C3%A8te";// TODO Encoder le nom dans l'url resolver
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Acer sempervirens", classification.getRang(CronquistTaxonomicRank.ESPECE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Acer", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Aceraceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Sapindales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Rosanae", classification.getRang(CronquistTaxonomicRank.SUPERORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliidae", classification.getRang(CronquistTaxonomicRank.SOUSCLASSE  ).getNom(), "Mauvais rang");
        Assertions.assertEquals("Equisetopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_ErableDeMiyabe() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/%C3%89rable_de_Miyabe";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Acer Miyabei", classification.getRang(CronquistTaxonomicRank.ESPECE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Acer", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Aceraceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Sapindales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Rosidae", classification.getRang(CronquistTaxonomicRank.SOUSCLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliophyta", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Tracheobionta", classification.getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_Hamamelidales() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Hamamelidales";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        // TODO fix scraper
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Hamamelidales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Hamamelidae", classification.getRang(CronquistTaxonomicRank.SOUSCLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliophyta", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Tracheobionta", classification.getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_Corylopsis() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Corylopsis";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Corylopsis", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Hamamelidaceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Saxifragales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Hamamelidae", classification.getRang(CronquistTaxonomicRank.SOUSCLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliophyta", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Tracheobionta", classification.getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_Distylium() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Distylium";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Distylium", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Hamamelidaceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Hamamelidales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Hamamelidae", classification.getRang(CronquistTaxonomicRank.SOUSCLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliophyta", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Tracheobionta", classification.getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_Loropetalum() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Loropetalum";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Loropetalum", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Hamamelidaceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Hamamelidales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Hamamelidae", classification.getRang(CronquistTaxonomicRank.SOUSCLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliophyta", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Tracheobionta", classification.getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_Oxera_neriifolia() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Oxera_neriifolia";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Oxera neriifolia", classification.getRang(CronquistTaxonomicRank.ESPECE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Oxera", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Verbenaceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Lamiales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliophyta", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Tracheobionta", classification.getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_Selaginaceae() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Selaginaceae";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Selaginaceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Lamiales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Equisetopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliophyta", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Tracheobionta", classification.getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_Lepisanthes_senegalensis() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Lepisanthes_senegalensis";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Lepisanthes senegalensis", classification.getRang(CronquistTaxonomicRank.ESPECE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Lepisanthes", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Sapindaceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Sapindales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Rosidae", classification.getRang(CronquistTaxonomicRank.SOUSCLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliophyta", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Tracheobionta", classification.getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_ErableDeMontpellier() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/%C3%89rable_de_Montpellier";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Acer monspessulanum", classification.getRang(CronquistTaxonomicRank.ESPECE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Acer", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Sapindaceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Sapindales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Rosidae", classification.getRang(CronquistTaxonomicRank.SOUSCLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliophyta", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Tracheobionta", classification.getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_Chironia() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Chironia";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Chironia", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Gentianaceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Gentianales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliophyta", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_Asparagaceae() {
        // Inexistant en Cronquist (APGIII)
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Asparagaceae";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNull(classification, "La classification doit avoir été créée");
    }

    @Test
    public void extractClassificationFromWiki_Agave_lechuguilla() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Agave_lechuguilla";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Agave lechuguilla", classification.getRang(CronquistTaxonomicRank.ESPECE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Agave", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Asparagaceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Asparagales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Lilianae", classification.getRang(CronquistTaxonomicRank.SUPERORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliidae", classification.getRang(CronquistTaxonomicRank.SOUSCLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Equisetopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_Helanthium_bolivianum() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Helanthium_bolivianum";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Helanthium bolivianum", classification.getRang(CronquistTaxonomicRank.ESPECE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Helanthium", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Alismataceae", classification.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Alismatales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Lilianae", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Angiospermae", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_Monodiella() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Monodiella";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNotNull(classification, "La classification doit avoir été créée");

        Assertions.assertEquals("Monodiella", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        //Assertions.assertEquals("Gentianaceae", classification.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Gentianales", classification.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliopsida", classification.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Magnoliophyta", classification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "Mauvais rang");
        Assertions.assertEquals("Tracheobionta", classification.getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "Mauvais rang");
        Assertions.assertEquals("Plantae", classification.getRang(CronquistTaxonomicRank.REGNE).getNom(), "Mauvais rang");
    }

    @Test
    public void extractClassificationFromWiki_Angiosperme() {
        CronquistClassificationBranch classification;
        String wiki = "https://fr.wikipedia.org/wiki/Angiosperme";
        classification = wikipediaScraper.extractClassificationFromWiki(wiki);
        Assertions.assertNull(classification, "La classification ne doit pas avoir été créée");

    }
}
