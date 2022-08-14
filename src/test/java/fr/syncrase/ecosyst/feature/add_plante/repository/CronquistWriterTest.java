package fr.syncrase.ecosyst.feature.add_plante.repository;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchMockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Iterator;

@SpringBootTest(classes = MonolithApp.class)
class CronquistWriterTest {

    @Autowired
    CronquistWriter cronquistWriter;

    @AfterEach
    void tearDown() {
        cronquistWriter.removeAll();
    }

    /**
     * Enregistrement d'une classification dans une base vide. Vérifie que :
     * <ul>
     *     <li>Le nombre d'élément dans classification est le bon</li>
     *     <li>Les rang taxonomiques portent les noms attendu</li>
     *     <li>Chaque élément possède bien un enfant correspondant au rang inférieur</li>
     *     <li>Chaque élément possède bien un parent correspondant au rang supérieur</li>
     * </ul><br/>
     * <p>
     * L'enregistrement d'une classification sémantiquement identique, mais dont la consistence n'a pas été vérifiée doit lancer une exception
     * </p>
     */
    @Test
    void saveClassification() {
        CronquistClassificationBranch cronquistClassificationBranch = cronquistWriter.saveClassification(ClassificationBranchMockRepository.ALLIUM.getClassification());
        Assertions.assertNotNull(cronquistClassificationBranch, "La classification conflictuel doit exister");
        Assertions.assertEquals(27, cronquistClassificationBranch.size(), "La classification à insérer doit posséder 27 éléments");
        Assertions.assertEquals("Allium", cronquistClassificationBranch.getRang(CronquistTaxonomicRank.GENRE).getNom(), "Le genre doit être Allium");
        // setConsistantParenthood
        Assertions.assertNotNull(cronquistClassificationBranch.getRang(CronquistTaxonomicRank.GENRE).getParent(), "Le genre Allium doit posséder un parent");
        Assertions.assertEquals("Liliaceae", cronquistClassificationBranch.getRang(CronquistTaxonomicRank.FAMILLE).getNom(), "La famille doit être Liliaceae");
        Assertions.assertEquals("Liliales", cronquistClassificationBranch.getRang(CronquistTaxonomicRank.ORDRE).getNom(), "La famille doit être Liliales");
        Assertions.assertEquals("Liliidae", cronquistClassificationBranch.getRang(CronquistTaxonomicRank.SOUSCLASSE).getNom(), "La famille doit être Liliidae");
        Assertions.assertEquals("Liliopsida", cronquistClassificationBranch.getRang(CronquistTaxonomicRank.CLASSE).getNom(), "La famille doit être Liliopsida");
        Assertions.assertEquals("Magnoliophyta", cronquistClassificationBranch.getRang(CronquistTaxonomicRank.EMBRANCHEMENT).getNom(), "La famille doit être Magnoliophyta");
        Assertions.assertEquals("Tracheobionta", cronquistClassificationBranch.getRang(CronquistTaxonomicRank.SOUSREGNE).getNom(), "La famille doit être Tracheobionta");
        Assertions.assertEquals("Plantae", cronquistClassificationBranch.getRang(CronquistTaxonomicRank.REGNE).getNom(), "La famille doit être Plantae");

        Iterator<CronquistRank> iterator = cronquistClassificationBranch.iterator();
        CronquistRank previousRank = iterator.next();
        while (iterator.hasNext()) {
            CronquistRank currentRank = iterator.next();
            Assertions.assertEquals(1, currentRank.getChildren().size(), "Chaque élément doit posséder 1 enfant. " + currentRank + " n'en possède pas et devrait avoir " + previousRank + " pour enfant");
            Assertions.assertTrue(currentRank.getChildren().contains(previousRank), "Chaque enfant doit être l'élément précédent");
            previousRank = currentRank;
        }


        /*
         * Vérifie que l'enregistrement d'une autre classification sémantiquement identique est impossible
         */

        Exception exception = Assertions.assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            cronquistWriter.saveClassification(ClassificationBranchMockRepository.ALLIUM.getClassification());
        });

        String expectedMessagePart1 = "could not execute statement; SQL [n/a]; constraint [\"PUBLIC.UX_CRONQUIST_RANK__NOM_INDEX_2 ON PUBLIC.CRONQUIST_RANK(NOM) VALUES ";
        String expectedMessagePart2 = "SQL statement:\ninsert into cronquist_rank (nom, parent_id, rank, id) values (?, ?, ?, ?) [23505-200]]; " +
            "nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessagePart1) && actualMessage.contains(expectedMessagePart2));


    }
}
