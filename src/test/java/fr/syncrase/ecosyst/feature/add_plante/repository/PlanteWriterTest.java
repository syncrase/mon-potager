package fr.syncrase.ecosyst.feature.add_plante.repository;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ScrapedPlanteMockRepository;
import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedPlant;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.UnableToSaveClassificationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MonolithApp.class)
class PlanteWriterTest {

    @Autowired
    PlanteWriter planteWriter;

    @AfterEach
    void tearDown() {
        planteWriter.removeAll();
    }

    @Test
    void saveClassification() throws UnableToSaveClassificationException {
        ScrapedPlant porrumScrapedPlante = ScrapedPlanteMockRepository.PORRUM.getPlante();
        Plante porrumPlantePlante = porrumScrapedPlante.getPlante();
        Assertions.assertNotNull(porrumPlantePlante.getReferences(), "La plante doit posséder des références");
        Assertions.assertNotNull(porrumPlantePlante.getNomsVernaculaires(), "La plante doit posséder des noms vernaculaires");

        Plante plante = planteWriter.saveScrapedPlante(porrumPlantePlante);
        Assertions.assertNotNull(plante);

        Assertions.assertNotNull(plante.getReferences(), "La plante doit posséder des références");
        Assertions.assertFalse(plante.getReferences().stream()
                .anyMatch(reference -> reference.getPlantes().size() == 0),
            "Chaque référence doit être associée à une plante");
        Assertions.assertFalse(plante.getReferences().stream()
                .anyMatch(reference -> reference.getPlantes().stream()
                    .anyMatch(plante1 -> plante1.getId() == null)),
            "Aucune des références ne doit posséder de plante sans ID");

        Assertions.assertNotNull(plante.getNomsVernaculaires(), "La plante doit posséder des noms vernaculaires");
        Assertions.assertFalse(plante.getNomsVernaculaires().stream()
                .anyMatch(nomVernaculaire -> nomVernaculaire.getPlantes().size() == 0),
            "Chaque nom vernaculaire doit être associée à une plante");
        Assertions.assertFalse(plante.getNomsVernaculaires().stream()
                .anyMatch(nomVernaculaire -> nomVernaculaire.getPlantes().stream()
                    .anyMatch(plante1 -> plante1.getId() == null)),
            "Aucun des noms vernaculaires ne doit posséder de plante sans ID");


    }
}
