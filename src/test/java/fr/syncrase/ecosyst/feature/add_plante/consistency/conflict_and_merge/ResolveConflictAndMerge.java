package fr.syncrase.ecosyst.feature.add_plante.consistency.conflict_and_merge;

import fr.syncrase.ecosyst.MonolithApp;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConflict;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConsistencyService;
import fr.syncrase.ecosyst.feature.add_plante.consistency.InconsistencyResolverException;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchRepository;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistReader;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistWriter;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MonolithApp.class)
public class ResolveConflictAndMerge {

    @Autowired
    CronquistWriter cronquistWriter;

    @Autowired
    CronquistReader cronquistReader;

    @Autowired
    private ClassificationConsistencyService classificationConsistencyService;

    @AfterEach
    void tearDown() {
        cronquistWriter.removeAll();
    }

    @Test
    public void checkConsistency_mergeWithNameConflict() throws ClassificationReconstructionException, MoreThanOneResultException, InconsistencyResolverException {

        // Règne 	Plantae
        //Division 	Angiospermae
        //Classe 	Lilianae        ←- Lilianae est un superordre
        //Ordre 	Alismatales
        //Famille 	Alismataceae
        //Genre 	Helanthium
        //Espèce    Helanthium bolivianum
        //wiki = "https://fr.wikipedia.org/wiki/Helanthium_bolivianum";
        CronquistClassificationBranch helanthiumBolivariumClassification = cronquistWriter.saveClassification(ClassificationBranchRepository.HELANTHIUM_BOLIVIANUM.getClassification());

        // Règne 	    Plantae
        //Classe 	    Equisetopsida   <- rentre en conflit avec l'autre
        //Sous-classe 	Magnoliidae
        //Super-ordre 	Lilianae
        //Ordre 	    Asparagales
        //Famille 	    Asparagaceae
        //Genre 	    Agave
        //Espèce        Agave lechuguilla
        //String wiki = "https://fr.wikipedia.org/wiki/Agave_lechuguilla";
        ClassificationConflict agaveLechuguillaConflicts = classificationConsistencyService.getSynchronizedClassificationAndConflicts(ClassificationBranchRepository.AGAVE_LECHUGUILLA.getClassification());
        Assertions.assertEquals(1, agaveLechuguillaConflicts.getConflictedClassifications().size(), "Il doit y avoir un conflit");

        // TODO to be implemented
        //CronquistClassificationBranch agaveLechuguillaClassification = cronquistWriter.saveClassification(agaveLechuguillaConflicts.getNewClassification());

    }

    @Test
    public void checkConsistency_rankSavedInTheWrongPlace() throws ClassificationReconstructionException, MoreThanOneResultException, InconsistencyResolverException {

        // Règne 	Plantae
        //Division 	Angiospermae
        //Classe 	Lilianae        ←- Lilianae est un superordre
        //Ordre 	Alismatales
        //Famille 	Alismataceae
        //Genre 	Helanthium
        //Espèce    Helanthium bolivianum
        //wiki = "https://fr.wikipedia.org/wiki/Helanthium_bolivianum";
        CronquistClassificationBranch classification = ClassificationBranchRepository.HELANTHIUM_BOLIVIANUM.getClassification();
        CronquistClassificationBranch helanthiumBolivariumClassification = cronquistWriter.saveClassification(classification);

        // Règne 	    Plantae
        //Classe 	    Equisetopsida   <- rentre en conflit avec l'autre, je le supprime car je ne test pas ça
        //Sous-classe 	Magnoliidae
        //Super-ordre 	Lilianae
        //Ordre 	    Asparagales
        //Famille 	    Asparagaceae
        //Genre 	    Agave
        //Espèce        Agave lechuguilla
        //String wiki = "https://fr.wikipedia.org/wiki/Agave_lechuguilla";
        CronquistClassificationBranch agave_lechuguillaClassification = ClassificationBranchRepository.AGAVE_LECHUGUILLA.getClassification();
        agave_lechuguillaClassification.clearRank(CronquistTaxonomicRank.CLASSE);
        ClassificationConflict agaveLechuguillaConflicts = classificationConsistencyService.getSynchronizedClassificationAndConflicts(agave_lechuguillaClassification);

        // TODO to be implemented
        //Assertions.assertEquals(1, agaveLechuguillaConflicts.getConflictedClassifications().size(), "Il doit y avoir un conflit");
        //
        //CronquistClassificationBranch agaveLechuguillaClassification = cronquistWriter.saveClassification(agaveLechuguillaConflicts.getNewClassification());

    }
}
