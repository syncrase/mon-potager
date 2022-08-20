package fr.syncrase.ecosyst.feature.add_plante.repository;

import fr.syncrase.ecosyst.domain.Classification;
import fr.syncrase.ecosyst.domain.NomVernaculaire;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConflict;
import fr.syncrase.ecosyst.feature.add_plante.consistency.CronquistConsistencyService;
import fr.syncrase.ecosyst.feature.add_plante.consistency.InconsistencyResolverException;
import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedPlant;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.UnableToSaveClassificationException;
import fr.syncrase.ecosyst.repository.ClassificationRepository;
import fr.syncrase.ecosyst.repository.NomVernaculaireRepository;
import fr.syncrase.ecosyst.repository.PlanteRepository;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class PlanteWriter {

    private final Logger log = LoggerFactory.getLogger(PlanteWriter.class);

    private final CronquistWriter cronquistWriter;

    private final NomVernaculaireRepository nomVernaculaireRepository;

    private final CronquistConsistencyService cronquistConsistencyService;

    private final PlanteRepository planteRepository;

    private final ClassificationRepository classificationRepository;

    public PlanteWriter(CronquistWriter cronquistWriter, NomVernaculaireRepository nomVernaculaireRepository, CronquistConsistencyService cronquistConsistencyService, PlanteRepository planteRepository, ClassificationRepository classificationRepository) {
        this.cronquistWriter = cronquistWriter;
        this.nomVernaculaireRepository = nomVernaculaireRepository;
        this.cronquistConsistencyService = cronquistConsistencyService;
        this.planteRepository = planteRepository;
        this.classificationRepository = classificationRepository;
    }

    @Transactional
    public Plante saveScrapedPlante(@NotNull ScrapedPlant plante) throws UnableToSaveClassificationException {

        // For each classification type
        CronquistClassificationBranch savedCronquist = saveCronquist(plante.getCronquistClassificationBranch());
        if (savedCronquist == null && plante.getCronquistClassificationBranch() != null) {
            throw new UnableToSaveClassificationException();
        }
        //if (savedCronquist != null) {
        plante.setCronquistClassificationBranch(savedCronquist);
        //}

        Classification classification = plante.getPlante().getClassification();
        //Classification classification = new Classification().cronquist(plante.getCronquistClassificationBranch().getLowestRank());
        classificationRepository.save(classification);


        //List<NomVernaculaire> nomVernaculaires =
        saveNomsVernaculaires(plante.getPlante().getNomsVernaculaires());
        //plante.setNomsVernaculaires(new HashSet<>(nomVernaculaires));

        Plante planteEntity = new Plante()
            .id(plante.getPlante().getId())
            .nomsVernaculaires(plante.getPlante().getNomsVernaculaires())
            .classification(classification);

        return planteRepository.save(planteEntity);
    }

    @Contract(pure = true)
    private void saveNomsVernaculaires(Set<NomVernaculaire> nomsVernaculaires) {
        //private @NotNull List<NomVernaculaire> saveNomsVernaculaires(@NotNull ScrapedPlant plante) {
        // TODO est-ce que les noms possède les nouveau id à la sortie de la méthode
        if (nomsVernaculaires == null) {
            return;
            //return List.of();
        }
        synchronize(nomsVernaculaires);
        //return
        nomVernaculaireRepository.saveAll(nomsVernaculaires);
    }

    private void synchronize(@NotNull Set<NomVernaculaire> nomsVernaculaires) {
        for (NomVernaculaire nomVernaculaire : nomsVernaculaires) {
            NomVernaculaire existingNomVernaculaire = nomVernaculaireRepository.findByNom(nomVernaculaire.getNom());
            if (existingNomVernaculaire != null) {
                nomVernaculaire.setId(existingNomVernaculaire.getId());
            }
        }
    }

    @Contract(pure = true)
    private @Nullable CronquistClassificationBranch saveCronquist(@NotNull CronquistClassificationBranch classification) {
        /*
         * Accès en read only
         */
        CronquistClassificationBranch toSaveCronquistClassification = new CronquistClassificationBranch(classification);
        ClassificationConflict conflicts;
        try {
            conflicts = cronquistConsistencyService.getSynchronizedClassificationAndConflicts(toSaveCronquistClassification);
        } catch (ClassificationReconstructionException e) {
            log.warn("{}: Unable to construct a classification for {}", e.getClass(), classification.last().getNom());
            return null;
        } catch (MoreThanOneResultException e) {
            log.warn("{}: the rank name {} seems to be in an inconsistent state in the database. More than one was found.", e.getClass(), classification.last().getNom());
            return null;
        }

        /*
         * Modification de la base pour qu'il n'y ait plus de conflit possible
         */
        ClassificationConflict resolvedConflicts;
        if (conflicts.getConflictedClassifications().size() != 0) {
            try {
                resolvedConflicts = cronquistConsistencyService.resolveInconsistencyInDatabase(conflicts);
                resolvedConflicts = cronquistConsistencyService.getSynchronizedClassificationAndConflicts(resolvedConflicts.getNewClassification());
            } catch (InconsistencyResolverException | MoreThanOneResultException e) {
                log.warn("{}: Unable to construct a classification for {}", e.getClass(), classification.last().getNom());
                return null;
            } catch (ClassificationReconstructionException e) {
                return null;
            }
        } else {
            resolvedConflicts = conflicts;
        }

        /*
         * Enregistrement de la nouvelle classification
         */
        if (resolvedConflicts.getConflictedClassifications().size() == 0) {
            return cronquistWriter.save(conflicts.getNewClassification());
        }
        return null;
    }
}
