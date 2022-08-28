package fr.syncrase.ecosyst.feature.add_plante.repository;

import fr.syncrase.ecosyst.domain.*;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.consistency.ClassificationConflict;
import fr.syncrase.ecosyst.feature.add_plante.consistency.CronquistConsistencyService;
import fr.syncrase.ecosyst.feature.add_plante.consistency.InconsistencyResolverException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.UnableToSaveClassificationException;
import fr.syncrase.ecosyst.repository.*;
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

    private final UrlRepository urlRepository;

    private final ReferenceRepository referenceRepository;

    private final CronquistRankRepository cronquistRankRepository;

    public PlanteWriter(
        CronquistWriter cronquistWriter,
        NomVernaculaireRepository nomVernaculaireRepository,
        CronquistConsistencyService cronquistConsistencyService,
        PlanteRepository planteRepository,
        ClassificationRepository classificationRepository,
        UrlRepository urlRepository,
        ReferenceRepository referenceRepository,
        CronquistRankRepository cronquistRankRepository) {
        this.cronquistWriter = cronquistWriter;
        this.nomVernaculaireRepository = nomVernaculaireRepository;
        this.cronquistConsistencyService = cronquistConsistencyService;
        this.planteRepository = planteRepository;
        this.classificationRepository = classificationRepository;
        this.urlRepository = urlRepository;
        this.referenceRepository = referenceRepository;
        this.cronquistRankRepository = cronquistRankRepository;
    }

    @Transactional
    public Plante saveScrapedPlante(@NotNull Plante plante) throws UnableToSaveClassificationException {

        plante.getClassification().setPlantes(Set.of(plante));
        saveClassification(plante.getClassification());

        plante.getReferences().forEach(reference -> reference.setPlantes(Set.of(plante)));
        saveReferences(plante.getReferences());

        plante.getNomsVernaculaires().forEach(nomVernaculaire -> nomVernaculaire.setPlantes(Set.of(plante)));
        saveNomsVernaculaires(plante.getNomsVernaculaires());

        return planteRepository.save(plante);
    }

    private void saveReferences(Set<Reference> references) {
        if (references != null) {
            references.forEach(reference -> {
                urlRepository.save(reference.getUrl());
                referenceRepository.save(reference);
            });
        }
    }

    private void saveClassification(@NotNull Classification classification) throws UnableToSaveClassificationException {
        // For each classification type
        CronquistClassificationBranch cronquistClassificationBranch = saveCronquist(classification.getCronquist());
        if (cronquistClassificationBranch != null) {
            classification.setCronquist(cronquistClassificationBranch.getNestedLowestRank());
            classificationRepository.save(classification);
        } else {
            throw new UnableToSaveClassificationException();
        }

    }

    @Contract(pure = true)
    private void saveNomsVernaculaires(Set<NomVernaculaire> nomsVernaculaires) {
        if (nomsVernaculaires == null) {
            return;
        }
        synchronizeNomsVernaculaires(nomsVernaculaires);
        nomVernaculaireRepository.saveAll(nomsVernaculaires);
    }

    private void synchronizeNomsVernaculaires(@NotNull Set<NomVernaculaire> nomsVernaculaires) {
        for (NomVernaculaire nomVernaculaire : nomsVernaculaires) {
            NomVernaculaire existingNomVernaculaire = nomVernaculaireRepository.findByNom(nomVernaculaire.getNom());
            if (existingNomVernaculaire != null) {
                nomVernaculaire.setId(existingNomVernaculaire.getId());
            }
        }
    }

    @Contract(pure = true)
    private @Nullable CronquistClassificationBranch saveCronquist(CronquistRank classification) {
        /*
         * Accès en read only
         */
        CronquistClassificationBranch toSaveCronquistClassification = new CronquistClassificationBranch(classification);
        ClassificationConflict conflicts;
        try {
            conflicts = cronquistConsistencyService.getSynchronizedClassificationAndConflicts(toSaveCronquistClassification);
        } catch (ClassificationReconstructionException e) {
            log.warn("{}: Unable to construct a classification for {}", e.getClass(), classification.getNom());
            return null;
        } catch (MoreThanOneResultException e) {
            log.warn("{}: the rank name {} seems to be in an inconsistent state in the database. More than one was found.", e.getClass(), classification.getNom());
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
                log.warn("{}: Unable to construct a classification for {}", e.getClass(), classification.getNom());
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

    public void removeAll() {
        planteRepository.findAll().forEach(plante -> {
            plante.setReferences(null);
            plante.setNomsVernaculaires(null);
            plante.setClassification(null);
            planteRepository.save(plante);
        });
        nomVernaculaireRepository.deleteAll();

        referenceRepository.findAll().forEach(reference -> {
            reference.setUrl(null);
            referenceRepository.save(reference);
        });
        urlRepository.deleteAll();
        referenceRepository.deleteAll();

        classificationRepository.findAll().forEach(classification -> {
            classification.setCronquist(null);
            classificationRepository.save(classification);
        });
        cronquistRankRepository.deleteAll();
        classificationRepository.deleteAll();
        planteRepository.deleteAll();
    }
}
