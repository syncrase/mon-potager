package fr.syncrase.ecosyst.feature.add_plante.consistency;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistReader;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for check the consistency of a {@link CronquistClassificationBranch}
 */
@Service
public class ClassificationConsistencyService {

    private final Logger log = LoggerFactory.getLogger(ClassificationConsistencyService.class);

    private final CronquistReader cronquistReader;

    public ClassificationConsistencyService(CronquistReader cronquistReader) {
        this.cronquistReader = cronquistReader;
    }

    /**
     * Read-only database access<br/>
     * Check if any inconsistency exists<br/>
     * Garanti l'invariant : le nom d'un rang est unique quel que soit le rang<br/>
     * Le rank que je cherche à enregistrer existe déjà :<br/>
     * <ul>
     *     <li>par son nom → je récupère l'id du rank enregistré pour le mettre dans le rank à enregistrer.</li>
     *     <li>en tant que rang de liaison → je récupère l'id du rank enregistré pour le mettre dans le rank à enregistrer (le nom sera mis à jour)</li>
     * </ul>
     *
     * @param toSaveCronquistClassification the classification I want to save in the database
     * @return the list of saved classifications which are in conflict with the passed classification
     */
    @Contract(pure = true)
    public ClassificationConflict checkConsistency(CronquistClassificationBranch toSaveCronquistClassification) throws ClassificationReconstructionException, MoreThanOneResultException {
        // Assign IDs of already existing ranks
        ClassificationConflict synchronizedClassification = getInsertionReadyAndConflictedRanks(toSaveCronquistClassification);


        if (synchronizedClassification.getConflictedClassifications().size() != 0) {
            // Trying to resolve conflict
            //Collection<ClassificationConflict> resolvedConflicts = conflicts.stream().peek(classificationConflict -> {
            //    /*
            //     * Les différents cas à gérer lors de la résolution :
            //     * cas complexes :
            //     * - on déduit de la classification à enregistrer que deux classifications divergentes déjà enregistrées ont une partie en commun → merge des branches (changement d'id pour l'une et suppression pour l'autre)
            //     */
            //    // Return null if the conflict remain unresolved OR the resolved classification
            //    Collection<CronquistClassificationBranch> conflict = classificationConsistencyService.resolveConflict(classificationConflict);
            //    if (conflict != null) {
            //        classificationConflict.setConflictedClassifications(conflict);
            //    }
            //});
            // If the conflict cannot be resolved, // TODO return something to the user in order to let him resolve the conflict
        }

        return synchronizedClassification;
    }

    /**
     * From the lowest rank of the passed classification
     * Read the database to find if a name exists
     * If yes, get the ID. This way :
     * <ul>
     *     <li>connection rank in the database will become significant rank on saving</li>
     *     <li>connection rank in classification to be saved become significant rank during the execution of the method</li>
     * </ul>
     * In the case of a significant rank in the classification to be saved already in another classification segment, this is a conflict which will require a merge management.
     *
     * @param toSaveCronquistClassification
     * @return
     */
    @Contract(pure = true)
    private @NotNull ClassificationConflict getInsertionReadyAndConflictedRanks(@NotNull CronquistClassificationBranch toSaveCronquistClassification)
        throws ClassificationReconstructionException, MoreThanOneResultException {

        ClassificationConflict synchronizationResult = new ClassificationConflict();
        CronquistClassificationBranch scrapedClassification = new CronquistClassificationBranch(toSaveCronquistClassification);// TODO clone this
        CronquistClassificationBranch existingClassification = cronquistReader.findExistingPartOfThisClassification(scrapedClassification);
        if (existingClassification != null) {
            for (CronquistRank existingRank : existingClassification) {
                CronquistRank scrapedRank;
                scrapedRank = scrapedClassification.getRang(existingRank.getRank());

                boolean scrapedRankIsSignificant = !CronquistClassificationBranch.isRangDeLiaison(scrapedRank);
                if (scrapedRankIsSignificant) {
                    Boolean isRankExists = cronquistReader.isRankExists(scrapedRank);
                    // Si le nom scrape n'existe pas en base → pas de souci pour l'ajouter
                    // S'il existe en base, il doit être porté par le rang existant. Sinon conflit à gérer
                    if (isRankExists && !scrapedRank.getNom().equals(existingRank.getNom())) {
                        synchronizationResult.addConflict(scrapedRank, existingRank);
                    } else {
                        scrapedRank.setId(existingRank.getId());
                    }
                }
                boolean uniquementLeRangExistantEstSignificatif = CronquistClassificationBranch.isRangDeLiaison(scrapedRank) && !CronquistClassificationBranch.isRangDeLiaison(existingRank);
                if (uniquementLeRangExistantEstSignificatif) {
                    scrapedRank.setId(existingRank.getId());
                }
            }
        }

        synchronizationResult.setNewClassification(scrapedClassification);
        // Retourne la classification contenue tous les IDs trouvés en base de données + tous les rangs taxonomique conflictuels
        return synchronizationResult;
    }
}
