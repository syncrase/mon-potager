package fr.syncrase.ecosyst.feature.add_plante.consistency.nameconflict;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConflict;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConsistencyService;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ConflictualRank;
import fr.syncrase.ecosyst.feature.add_plante.consistency.InconsistencyResolverException;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchRepository;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistWriter;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = MonolithApp.class)
public class RankNameConflictTest {

    @Autowired
    CronquistWriter cronquistWriter;

    @Autowired
    private ClassificationConsistencyService classificationConsistencyService;

    @AfterEach
    void tearDown() {
        cronquistWriter.removeAll();
    }


    @Test
    public void checkConsistency_conflictOnTheOrder() throws ClassificationReconstructionException, MoreThanOneResultException, InconsistencyResolverException {

        // Règne 	Plantae
        //Sous-règne 	Tracheobionta
        //Division 	Magnoliophyta
        //Classe 	Magnoliopsida
        //Sous-classe 	Hamamelidae
        //Ordre 	Saxifragales
        //Famille 	Hamamelidaceae
        //Genre Corylopsis
        CronquistClassificationBranch corylopsisClassification = cronquistWriter.saveClassification(ClassificationBranchRepository.CORYLOPSIS.getClassification());

        // Règne 	Plantae
        //Sous-règne 	Tracheobionta
        //Division 	Magnoliophyta
        //Classe 	Magnoliopsida
        //Sous-classe 	Hamamelidae
        //Ordre 	Hamamelidales
        //Famille 	Hamamelidaceae
        //Genre Distylium
        ClassificationConflict distyliumConflicts = classificationConsistencyService.getSynchronizedClassificationAndConflicts(ClassificationBranchRepository.DISTYLIUM.getClassification());

        Assertions.assertEquals(1, distyliumConflicts.getConflictedClassifications().size(), "Il doit exister un conflit");
        Optional<ConflictualRank> conflictualRank = distyliumConflicts.getConflictedClassifications().stream().findFirst();
        if (conflictualRank.isPresent()) {
            Assertions.assertEquals(conflictualRank.get().getExisting().getRank(), conflictualRank.get().getScraped().getRank(), "Les rang en conflit doivent être du même rang taxonomique");
            Assertions.assertEquals(CronquistTaxonomicRank.ORDRE, conflictualRank.get().getScraped().getRank(), "Ce sont les ordres qui doivent entrer en conflit");
        } else {
            fail();
        }

        ClassificationConflict classificationConflict = classificationConsistencyService.resolveInconsistency(distyliumConflicts);
        // TODO résolution: vérifier que je trouve le bon nom et que le merge à été fait
        cronquistWriter.removeClassification(corylopsisClassification);
        //cronquistWriter.removeClassification(firstCronquistClassificationBranch);
    }

    @Test
    public void checkConsistency_mergeWithoutNameConflict() throws ClassificationReconstructionException, MoreThanOneResultException, InconsistencyResolverException {

        // Règne 	Plantae
        //Sous-règne 	Tracheobionta
        //Division 	Magnoliophyta
        //Classe 	Magnoliopsida
        //Sous-classe 	Rosidae
        //Ordre 	Santalales
        //Famille 	Santalaceae
        //Genre Arjona
        CronquistClassificationBranch arjonaClassification = cronquistWriter.saveClassification(ClassificationBranchRepository.ARJONA.getClassification());


        // La Atalaya appartient à la sous-classe des Rosidae, mais on ne le sait pas à partir des informations reçues. On le découvre quand on enregistre Cossinia
        // Règne 	Plantae
        //Sous-règne 	(+Tracheobionta déduit du précédent)
        //Division 	Magnoliophyta
        //Classe 	Magnoliopsida
        //Sous-classe 	(+Rosidae déduis du suivant)
        //Ordre 	Sapindales
        //Famille 	Sapindaceae
        //Genre Atalaya
        ClassificationConflict atalayaConflicts = classificationConsistencyService.checkConsistency(ClassificationBranchRepository.ATALAYA.getClassification());
        Assertions.assertEquals(0, atalayaConflicts.getConflictedClassifications().size(), "Il ne doit pas y avoir de conflit");
        CronquistRank atalayaSousRegne = atalayaConflicts.getNewClassification().getRang(CronquistTaxonomicRank.SOUSREGNE);
        Assertions.assertEquals("Tracheobionta", atalayaSousRegne.getNom(), "La classification atalaya doit posséder le sous-règne Tracheobionta mais est égal à " + atalayaSousRegne);
        CronquistClassificationBranch atalayaClassification = cronquistWriter.saveClassification(atalayaConflicts.getNewClassification());


        // Règne 	Plantae
        //Sous-règne 	Tracheobionta
        //Division 	Magnoliophyta
        //Classe 	Magnoliopsida
        //Sous-classe 	Rosidae
        //Ordre 	Sapindales
        //Famille 	Sapindaceae
        //Genre Cossinia
        ClassificationConflict cossiniaPinnataConflicts = classificationConsistencyService.checkConsistency(ClassificationBranchRepository.COSSINIA_PINNATA.getClassification());
        Assertions.assertEquals(1, cossiniaPinnataConflicts.getConflictedClassifications().size(), "Il doit y avoir un conflit");

        ClassificationConflict resolvedConflicts = classificationConsistencyService.resolveInconsistency(cossiniaPinnataConflicts);// La base est mise à jour pour accepter la nouvelle classification
        Assertions.assertEquals(0, atalayaConflicts.getConflictedClassifications().size(), "Il ne doit plus y avoir de conflit");

        // On récupère atalaya pour vérifier qu'elle a bien récupéré la sous-classe rosidae

    }
}
