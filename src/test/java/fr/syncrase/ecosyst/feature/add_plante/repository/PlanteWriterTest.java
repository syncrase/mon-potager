package fr.syncrase.ecosyst.feature.add_plante.repository;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ScrapedPlanteMockRepository;
import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedPlant;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.UnableToSaveClassificationException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MonolithApp.class)
class PlanteWriterTest {

    @Autowired
    PlanteWriter planteWriter;

    @Autowired
    PlanteReader planteReader;

    @AfterEach
    void tearDown() {
        planteWriter.removeAll();
    }

    private static void assertClassification(@NotNull Plante plante) {
        Assertions.assertNotNull(plante.getClassification(), "La plante doit être associée à une classification");
        CronquistRank cronquistRank = plante.getClassification().getCronquist();
        Assertions.assertNotNull(cronquistRank, "La classification de Cronquist doit exister");
        Assertions.assertNotNull(cronquistRank.getClassification(), "Cronquist doit être associée à une classification");
        Assertions.assertNotNull(cronquistRank.getClassification().getId(), "La classification associée doit posséder un ID");
        Assertions.assertEquals(cronquistRank.getRank(), CronquistTaxonomicRank.ESPECE, "La classification doit être associé à l'espèce");
        //noinspection MismatchedQueryAndUpdateOfCollection
        final CronquistClassificationBranch porrumClassification = new CronquistClassificationBranch(cronquistRank);
        Assertions.assertEquals("allium ampeloprasum var. porrum", porrumClassification.getLowestRank().getNom());
        Assertions.assertNotNull(porrumClassification.getLowestRank().getId());
        Assertions.assertEquals("alliaceae", porrumClassification.getRang(CronquistTaxonomicRank.FAMILLE).getNom());
        Assertions.assertNotNull(porrumClassification.getRang(CronquistTaxonomicRank.FAMILLE).getId());
        Assertions.assertEquals("liliales", porrumClassification.getRang(CronquistTaxonomicRank.ORDRE).getNom());
        Assertions.assertNotNull(porrumClassification.getRang(CronquistTaxonomicRank.ORDRE).getId());
        Assertions.assertEquals("liliidae", porrumClassification.getRang(CronquistTaxonomicRank.SOUSCLASSE).getNom());
        Assertions.assertNotNull(porrumClassification.getRang(CronquistTaxonomicRank.SOUSCLASSE).getId());
        Assertions.assertEquals("liliopsida", porrumClassification.getRang(CronquistTaxonomicRank.CLASSE).getNom());
        Assertions.assertNotNull(porrumClassification.getRang(CronquistTaxonomicRank.CLASSE).getId());
        Assertions.assertEquals("magnoliophyta", porrumClassification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom());
        Assertions.assertNotNull(porrumClassification.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getId());
        Assertions.assertEquals("tracheobionta", porrumClassification.getRang(CronquistTaxonomicRank.SOUSREGNE).getNom());
        Assertions.assertNotNull(porrumClassification.getRang(CronquistTaxonomicRank.SOUSREGNE).getId());
        Assertions.assertEquals("plantae", porrumClassification.getRang(CronquistTaxonomicRank.REGNE).getNom());
        Assertions.assertNotNull(porrumClassification.getRang(CronquistTaxonomicRank.REGNE).getId());
    }

    private static void assertNomsVernaculaires(@NotNull Plante plante) {
        Assertions.assertNotNull(plante.getNomsVernaculaires(), "La plante doit posséder des noms vernaculaires");
        Assertions.assertFalse(plante.getNomsVernaculaires().stream()
                .anyMatch(nomVernaculaire -> nomVernaculaire.getPlantes().size() == 0),
            "Chaque nom vernaculaire doit être associée à une plante");
        Assertions.assertFalse(plante.getNomsVernaculaires().stream()
                .anyMatch(nomVernaculaire -> nomVernaculaire.getPlantes().stream()
                    .anyMatch(plante1 -> plante1.getId() == null)),
            "Aucun des noms vernaculaires ne doit posséder de plante sans ID");
        Assertions.assertFalse(plante.getNomsVernaculaires().stream()
                .anyMatch(nomVernaculaire -> nomVernaculaire.getDescription() == null),
            "Les noms vernaculaires doivent posséder une description");
    }

    private static void assertReferences(@NotNull Plante plante) {
        Assertions.assertNotNull(plante.getReferences(), "La plante doit posséder des références");
        Assertions.assertFalse(plante.getReferences().stream()
                .anyMatch(reference -> reference.getPlantes().size() == 0),
            "Chaque référence doit être associée à une plante");
        Assertions.assertFalse(plante.getReferences().stream()
                .anyMatch(reference -> reference.getPlantes().stream()
                    .anyMatch(plante1 -> plante1.getId() == null)),
            "Aucune des références ne doit posséder de plante sans ID");
        Assertions.assertFalse(plante.getReferences().stream()
                .anyMatch(reference -> reference.getDescription() == null),
            "Les références doivent posséder une description");
    }

    @Test
    void saveClassification() throws UnableToSaveClassificationException {
        ScrapedPlant porrumScrapedPlante = ScrapedPlanteMockRepository.PORRUM.getPlante();
        Plante porrumPlantePlante = porrumScrapedPlante.getPlante();
        Assertions.assertNotNull(porrumPlantePlante.getReferences(), "La plante doit posséder des références");
        Assertions.assertNotNull(porrumPlantePlante.getNomsVernaculaires(), "La plante doit posséder des noms vernaculaires");
        Assertions.assertNotNull(porrumPlantePlante.getClassification(), "La plante doit posséder une classification");

        Plante plante = planteWriter.saveScrapedPlante(porrumPlantePlante);
        Assertions.assertNotNull(plante);
        assertReferences(plante);
        assertNomsVernaculaires(plante);
        assertClassification(plante);

        Plante eagerLoadedPlante = planteReader.eagerLoad(plante);
        Assertions.assertNotNull(eagerLoadedPlante);
        assertReferences(eagerLoadedPlante);
        assertNomsVernaculaires(eagerLoadedPlante);
        assertClassification(eagerLoadedPlante);

        ScrapedPlant porrumScrapedPlante2 = ScrapedPlanteMockRepository.PORRUM.getPlante();
        Plante porrumPlantePlante2 = porrumScrapedPlante2.getPlante();
        Plante plante2 = planteWriter.saveScrapedPlante(porrumPlantePlante2);
        // Asserts : all IDs must be equals
        Assertions.assertEquals(plante.getId(), plante2.getId(), "Les IDs des plantes doivent être égaux");
        Assertions.assertEquals(plante.getClassification().getId(), plante2.getClassification().getId(), "Les IDs des classifications doivent être égaux");

        Assertions.assertTrue(plante2.getReferences().containsAll(plante.getReferences()), "Les références des plantes doivent être les mêmes");
        Assertions.assertTrue(plante.getNomsVernaculaires().containsAll(plante2.getNomsVernaculaires()), "Les noms vernaculaires doivent être égaux");

    }
}
