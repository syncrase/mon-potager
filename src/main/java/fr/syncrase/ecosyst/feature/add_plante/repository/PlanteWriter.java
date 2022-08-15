package fr.syncrase.ecosyst.feature.add_plante.repository;

import fr.syncrase.ecosyst.domain.NomVernaculaire;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConflict;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConsistencyService;
import fr.syncrase.ecosyst.feature.add_plante.consistency.InconsistencyResolverException;
import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedPlant;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.UnableToSaveClassificationException;
import fr.syncrase.ecosyst.repository.NomVernaculaireRepository;
import fr.syncrase.ecosyst.repository.PlanteRepository;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class PlanteWriter {

    private final Logger log = LoggerFactory.getLogger(PlanteWriter.class);

    private final CronquistWriter cronquistWriter;

    private final NomVernaculaireRepository nomVernaculaireRepository;

    private final ClassificationConsistencyService classificationConsistencyService;

    private final PlanteRepository planteRepository;

    public PlanteWriter(CronquistWriter cronquistWriter, NomVernaculaireRepository nomVernaculaireRepository, ClassificationConsistencyService classificationConsistencyService, PlanteRepository planteRepository) {
        this.cronquistWriter = cronquistWriter;
        this.nomVernaculaireRepository = nomVernaculaireRepository;
        this.classificationConsistencyService = classificationConsistencyService;
        this.planteRepository = planteRepository;
    }

    @Transactional
    public Plante saveScrapedPlante(@NotNull ScrapedPlant plante) throws UnableToSaveClassificationException {

        CronquistClassificationBranch savedCronquist = saveClassification(plante.getCronquistClassificationBranch());
        if (savedCronquist == null && plante.getCronquistClassificationBranch() != null) {
            throw new UnableToSaveClassificationException();
        }
        if (savedCronquist != null) {
            plante.setCronquistClassificationBranch(savedCronquist);
        }

        List<NomVernaculaire> nomVernaculaires = saveNomsVernaculaires(plante);
        plante.setNomsVernaculaires(new HashSet<>(nomVernaculaires));

        return savePlante(plante);
    }

    private @NotNull Plante savePlante(@NotNull ScrapedPlant plante) {
        return planteRepository.save(
                new Plante()
                        .id(plante.getId())
                        .nomsVernaculaires(plante.getNomsVernaculaires())
                        .cronquistRank(plante.getCronquistClassificationBranch().getLowestRank())
        );
    }

    @Contract(pure = true)
    private @NotNull List<NomVernaculaire> saveNomsVernaculaires(@NotNull ScrapedPlant plante) {
        if (plante.getNomsVernaculaires() == null) {
            return List.of();
        }
        for (NomVernaculaire nomVernaculaire : plante.getNomsVernaculaires()) {
            NomVernaculaire existingNomVernaculaire = nomVernaculaireRepository.findByNom(nomVernaculaire.getNom());
            if (existingNomVernaculaire != null) {
                nomVernaculaire.setId(existingNomVernaculaire.getId());
            }
        }
        return nomVernaculaireRepository.saveAll(plante.getNomsVernaculaires());
    }

    @Contract(pure = true)
    private @Nullable CronquistClassificationBranch saveClassification(@NotNull CronquistClassificationBranch classification) {
        /*
         * Accès en read only
         */
        CronquistClassificationBranch toSaveCronquistClassification = new CronquistClassificationBranch(classification);
        ClassificationConflict conflicts;
        try {
            conflicts = classificationConsistencyService.getSynchronizedClassificationAndConflicts(toSaveCronquistClassification);
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
                resolvedConflicts = classificationConsistencyService.resolveInconsistencyInDatabase(conflicts);
                resolvedConflicts = classificationConsistencyService.getSynchronizedClassificationAndConflicts(resolvedConflicts.getNewClassification());
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
            return cronquistWriter.saveClassification(conflicts.getNewClassification());
        }
        return null;
    }
}
