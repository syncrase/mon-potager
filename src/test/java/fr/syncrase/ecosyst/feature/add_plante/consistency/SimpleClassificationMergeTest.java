package fr.syncrase.ecosyst.feature.add_plante.consistency;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchRepository;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistWriter;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = MonolithApp.class)
public class SimpleClassificationMergeTest {

    @Autowired
    CronquistWriter cronquistWriter;

    @Autowired
    private ClassificationConsistencyService classificationConsistencyService;


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
        // Je vérifie que la classification pre-checkconsistency manque de plusieurs IDs
        // TODO assert
        fail("To be implemented");
        ClassificationConflict checkedThenResolvedThenCheckedClassification = classificationConsistencyService.checkConsistency(resolvedConflicts.getNewClassification());
        Assertions.assertEquals(0, cossiniaPinnataConflicts.getConflictedClassifications().size(),
            "Il doit pas y avoir de conflit quand on règle les conflits d'une classification qui n'a pas de conflit");
        // Je vérifie que tout ce qui causais problème a été corrigé


        // On récupère atalaya pour vérifier que sa branche de classification à bien intégré la sous-classe rosidae

        // TODO documenter l'erreur: could not initialize proxy - no Session
        // https://stackoverflow.com/questions/21574236/how-to-fix-org-hibernate-lazyinitializationexception-could-not-initialize-prox
        // https://www.baeldung.com/hibernate-initialize-proxy-exception


    }

}
