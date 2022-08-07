package fr.syncrase.ecosyst.feature.add_plante.repository;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomikRanks;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Iterator;

@SpringBootTest(classes = MonolithApp.class)
class CronquistWriterTest {

    @Autowired
    CronquistWriter cronquistWriter;

    /**
     * Enregistrement d'une classification dans une base vide. Vérifie que :
     * <ul>
     *     <li>Le nombre d'élément dans classification est le bon</li>
     *     <li>Les rang taxonomiques portent les noms attendu</li>
     *     <li>Chaque élément possède bien un enfant correspondant au rang inférieur</li>
     *     <li>Chaque élément possède bien un parent correspondant au rang supérieur</li>
     *     <li></li>
     *     <li></li>
     * </ul>
     */
    @Test
    void saveClassification() {
        CronquistClassificationBranch cronquistClassificationBranch = cronquistWriter.saveClassification(ClassificationBranchRepository.ALLIUM.getClassification());
        Assertions.assertNotNull(cronquistClassificationBranch, "La classification conflictuel doit exister");
        Assertions.assertEquals(27, cronquistClassificationBranch.size(), "La classification à insérer doit posséder 27 éléments");
        Assertions.assertEquals("Allium", cronquistClassificationBranch.getRang(CronquistTaxonomikRanks.GENRE).getNom(), "Le genre doit être Allium");
        // setConsistantParenthood
        Assertions.assertNotNull(cronquistClassificationBranch.getRang(CronquistTaxonomikRanks.GENRE).getParent(), "Le genre Allium doit posséder un parent");
        Assertions.assertEquals("Liliaceae", cronquistClassificationBranch.getRang(CronquistTaxonomikRanks.FAMILLE).getNom(), "La famille doit être Liliaceae");
        Assertions.assertEquals("Liliales", cronquistClassificationBranch.getRang(CronquistTaxonomikRanks.ORDRE).getNom(), "La famille doit être Liliales");
        Assertions.assertEquals("Liliidae", cronquistClassificationBranch.getRang(CronquistTaxonomikRanks.SOUSCLASSE).getNom(), "La famille doit être Liliidae");
        Assertions.assertEquals("Liliopsida", cronquistClassificationBranch.getRang(CronquistTaxonomikRanks.CLASSE).getNom(), "La famille doit être Liliopsida");
        Assertions.assertEquals("Magnoliophyta", cronquistClassificationBranch.getRang(CronquistTaxonomikRanks.EMBRANCHEMENT).getNom(), "La famille doit être Magnoliophyta");
        Assertions.assertEquals("Tracheobionta", cronquistClassificationBranch.getRang(CronquistTaxonomikRanks.SOUSREGNE).getNom(), "La famille doit être Tracheobionta");
        Assertions.assertEquals("Plantae", cronquistClassificationBranch.getRang(CronquistTaxonomikRanks.REGNE).getNom(), "La famille doit être Plantae");

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
        CronquistClassificationBranch cronquistClassificationBranch2 = cronquistWriter.saveClassification(ClassificationBranchRepository.ALLIUM.getClassification());
        // TODO assert exception
    }
}
