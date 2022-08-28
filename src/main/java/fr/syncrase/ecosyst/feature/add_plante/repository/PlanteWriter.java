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

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
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

    private static void synchronizePlanteAndReferences(@NotNull Plante plante, Set<Plante> intersection) {
        Plante existingPlanteForOneOfThisNameAndThisClassification = intersection.stream().findFirst().orElse(new Plante());
        plante.setId(existingPlanteForOneOfThisNameAndThisClassification.getId());
        for (Reference reference : plante.getReferences()) {
            for (Reference existingReference : existingPlanteForOneOfThisNameAndThisClassification.getReferences()) {
                // Si je trouve une référence qui correspond à l'une de celles existantes, j'en déduis que c'est la même
                boolean isUrlsMatch = existingReference.getUrl() != null && reference.getUrl() != null && existingReference.getUrl().getUrl().equals(reference.getUrl().getUrl());
                boolean isDescriptionMatch = Objects.equals(existingReference.getDescription(), reference.getDescription());
                boolean isTypeMatch = existingReference.getType().equals(reference.getType());
                if (isUrlsMatch && isDescriptionMatch && isTypeMatch) {
                    reference.setId(existingReference.getId());
                    break;
                }
            }
        }
    }

    @Transactional
    public Plante saveScrapedPlante(@NotNull Plante plante) throws UnableToSaveClassificationException {

        plante.getNomsVernaculaires().forEach(nomVernaculaire -> nomVernaculaire.setPlantes(Set.of(plante)));
        plante.getReferences().forEach(reference -> reference.setPlantes(Set.of(plante)));

        Set<Plante> plantesAlreadyAssociatedWithClassification = saveClassification(plante.getClassification());

        Set<Plante> plantesAlreadyAssociatedWithNames = saveNomsVernaculaires(plante.getNomsVernaculaires());

        inferTheCorrespondingPlante(plante, plantesAlreadyAssociatedWithClassification, plantesAlreadyAssociatedWithNames);

        saveReferences(plante.getReferences());

        return planteRepository.save(plante);
    }

    private void inferTheCorrespondingPlante(@NotNull Plante plante, Set<Plante> plantesAlreadyAssociatedWithClassification, Set<Plante> plantesAlreadyAssociatedWithNames) {
        Set<Plante> intersection = new HashSet<>(plantesAlreadyAssociatedWithNames);
        intersection.retainAll(plantesAlreadyAssociatedWithClassification);
        if (intersection.size() == 0) {
            log.info("No common plante for classification and names. This is a new plante");
        } else if (intersection.size() == 1) {
            log.info("Found a common plante for classification and names. We'll update this one");
            synchronizePlanteAndReferences(plante, intersection);
        } else {
            log.error("Found multiple plante associated with the classification and names. How to determine which one update?\n" +
                "Plante to save : " + plante + "\n" +
                "Associated with classification : " + plantesAlreadyAssociatedWithClassification + "\n" +
                "Associated with noms vernaculaires : " + plantesAlreadyAssociatedWithNames);
        }
    }

    private void saveReferences(Set<Reference> references) {
        if (references != null) {
            references.forEach(reference -> {
                Url existingUrl = urlRepository.findOneByUrl(reference.getUrl().getUrl());
                if (existingUrl != null) {
                    reference.getUrl().setId(existingUrl.getId());
                }
                urlRepository.save(reference.getUrl());
                referenceRepository.save(reference);// TODO comment déterminer si la référence est nouvelle?
            });
        }
    }

    private @NotNull Set<Plante> saveClassification(@NotNull Classification classification) throws UnableToSaveClassificationException {

        CronquistClassificationBranch toSaveCronquistClassification = new CronquistClassificationBranch(classification.getCronquist());
        classification.setCronquist(toSaveCronquistClassification.getNestedLowestRank());
        classification.getCronquist().setClassification(classification);

        CronquistClassificationBranch cronquistClassificationBranch = saveCronquist(toSaveCronquistClassification);
        if (cronquistClassificationBranch != null) {
            CronquistRank lowestRank = cronquistClassificationBranch.getNestedLowestRank();
            boolean isCronquistRankAlreadyAssociatedWithClassification = lowestRank.getClassification() != null && lowestRank.getClassification().getId() != null;
            if (isCronquistRankAlreadyAssociatedWithClassification) {
                classification.setId(lowestRank.getClassification().getId());
                Classification classification1 = classificationRepository.findOneById(lowestRank.getClassification().getId());
                return classification1.getPlantes();
            } else {
                classification.setCronquist(lowestRank);
                classificationRepository.save(classification);
                cronquistRankRepository.save(classification.getCronquist());// Save the classification in the cronquist rank
            }
        } else {
            throw new UnableToSaveClassificationException();
        }

        return Collections.emptySet();
    }

    @Contract(pure = true)
    private @NotNull Set<Plante> saveNomsVernaculaires(Set<NomVernaculaire> nomsVernaculaires) {
        if (nomsVernaculaires == null) {
            return Collections.emptySet();
        }
        Set<Plante> plantesAssociatedWithNames = synchronizeNomsVernaculaires(nomsVernaculaires);
        nomVernaculaireRepository.saveAll(nomsVernaculaires);
        return plantesAssociatedWithNames;
    }

    private @NotNull Set<Plante> synchronizeNomsVernaculaires(@NotNull Set<NomVernaculaire> nomsVernaculaires) {
        Set<Plante> plantesAssociatedWithNames = new HashSet<>();
        for (NomVernaculaire nomVernaculaire : nomsVernaculaires) {
            NomVernaculaire existingNomVernaculaire = nomVernaculaireRepository.findByNom(nomVernaculaire.getNom());
            if (existingNomVernaculaire != null) {
                nomVernaculaire.setId(existingNomVernaculaire.getId());
                nomVernaculaire.setDescription(existingNomVernaculaire.getDescription());
                nomVernaculaire.setNom(existingNomVernaculaire.getNom());
                plantesAssociatedWithNames.addAll(existingNomVernaculaire.getPlantes());
            }
        }
        return plantesAssociatedWithNames;
    }

    @Contract(pure = true)
    private @Nullable CronquistClassificationBranch saveCronquist(@NotNull CronquistClassificationBranch toSaveCronquistClassification) {
        ClassificationConflict conflicts;
        CronquistRank cronquistRank = toSaveCronquistClassification.getLowestRank();
        try {
            conflicts = cronquistConsistencyService.getSynchronizedClassificationAndConflicts(toSaveCronquistClassification);
        } catch (ClassificationReconstructionException e) {
            log.warn("{}: Unable to construct a cronquistRank for {}", e.getClass(), cronquistRank.getNom());
            return null;
        } catch (MoreThanOneResultException e) {
            log.warn("{}: the rank name {} seems to be in an inconsistent state in the database. More than one was found.", e.getClass(), cronquistRank.getNom());
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
                log.warn("{}: Unable to construct a cronquistRank for {}", e.getClass(), cronquistRank.getNom());
                return null;
            } catch (ClassificationReconstructionException e) {
                return null;
            }
        } else {
            resolvedConflicts = conflicts;
        }

        /*
         * Enregistrement de la nouvelle cronquistRank
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
