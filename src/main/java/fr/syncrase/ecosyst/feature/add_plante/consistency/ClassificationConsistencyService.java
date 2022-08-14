package fr.syncrase.ecosyst.feature.add_plante.consistency;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedPlant;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistReader;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistWriter;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.WebScrapingService;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.NonExistentWikiPageException;
import fr.syncrase.ecosyst.feature.add_plante.scraper.wikipedia.exception.PlantNotFoundException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

/**
 * Service for check the consistency of a {@link CronquistClassificationBranch}
 */
@Service
public class ClassificationConsistencyService {

    private final Logger log = LoggerFactory.getLogger(ClassificationConsistencyService.class);

    private final CronquistReader cronquistReader;
    private final CronquistWriter cronquistWriter;
    private final WebScrapingService webScrapingService;

    private CronquistRankRepository cronquistRankRepository;

    public ClassificationConsistencyService(CronquistReader cronquistReader, CronquistWriter cronquistWriter, WebScrapingService webScrapingService) {
        this.cronquistReader = cronquistReader;
        this.cronquistWriter = cronquistWriter;
        this.webScrapingService = webScrapingService;
    }

    /**
     * Read-only database access<br/>
     * <p>
     * In order to guarantee that the returned classification can be safely inserted without break any invariant. Each rank is synchronized :
     * <ul>
     *     <li>existing ranks get an ID</li>
     *     <li>Already named rank get a name and an ID</li>
     * </ul><br/>
     * Conflicts occur when the passed classification
     * <ul>
     *     <li>name a rank with another name</li>
     *     <li>name a rank with an already existing rank, but in another place</li>
     * </ul>
     *
     * @param toSaveCronquistClassification Classification que l'on souhaite enregistrer en base de données, celle-ci est copiée et synchronisée avec la BDD afin de satisfaire tous les invariants
     * @return La classification dont les rangs ont été comparés avec la base de données. S'il n'y a aucun conflit, la classification peut être enregistrée telle qu'elle.
     */
    @Contract(pure = true)
    public ClassificationConflict getSynchronizedClassificationAndConflicts(CronquistClassificationBranch toSaveCronquistClassification) throws ClassificationReconstructionException, MoreThanOneResultException {
        ClassificationConflict synchronizationResult = new ClassificationConflict();
        CronquistClassificationBranch scrapedClassification = new CronquistClassificationBranch(toSaveCronquistClassification);
        synchronizationResult.setNewClassification(scrapedClassification);
        addConflictIfRankExistsAtAnotherLevel(synchronizationResult, scrapedClassification);
        if (synchronizationResult.getConflictedClassifications().size() != 0) {
            return synchronizationResult;// The same rank name already exists at another place. The conflict must be resolved before continue the synchronization
        }

        CronquistClassificationBranch existingClassification = cronquistReader.findExistingPartOfThisClassification(scrapedClassification);
        if (existingClassification != null) {
            for (CronquistRank existingRank : existingClassification) {
                CronquistRank scrapedRank;
                scrapedRank = scrapedClassification.getRang(existingRank.getRank());
                updateScrapedRankOrAddConflict(synchronizationResult, existingRank, scrapedRank);
            }
        }
        return synchronizationResult;
    }

    /**
     * Go through scraped classification and assert that each rank isn't associated with another taxonomic rank
     *
     * @param synchronizationResult A conflict is added to this object if the scraped rank already exists for another taxonomic rank
     * @param scrapedClassification The scraped classification which will be asserted
     * @throws MoreThanOneResultException if the passed scraped rank doesn't permit to find a unique rank
     */
    private void addConflictIfRankExistsAtAnotherLevel(ClassificationConflict synchronizationResult, @NotNull CronquistClassificationBranch scrapedClassification) throws MoreThanOneResultException {
        for (CronquistRank scrapedRank : scrapedClassification) {
            CronquistRank existingScrapedRank = cronquistReader.findExistingRank(scrapedRank);
            if (existingScrapedRank != null && Objects.equals(existingScrapedRank.getNom(), scrapedRank.getNom()) && !existingScrapedRank.getRank().equals(scrapedRank.getRank())) {
                synchronizationResult.addConflict(scrapedRank, existingScrapedRank);
            }
        }
    }

    /**
     * Check if any inconsistency exists<br/>
     * The more complexe use case is in the case of a significant rank in the classification to be saved already exists in another classification segment, this is a conflict which will require a merge management.<br/>
     * If the scraped rank already exists at another taxonomic rank, declare a conflict.
     * <p>
     * <table border="2">
     *   <thead>
     *     <tr>
     *       <th>scraped rank</th>
     *       <th>existing rank</th>
     *       <th>Action</th>
     *     </tr>
     *    </thead>
     *    <tbody>
     *      <tr>
     *        <td>significatif</td>
     *        <td>significatif</td>
     *        <td>
     *          <u>Gestion de deux rangs significatifs</u><br/>
     *          <ul>
     *            <li>Si les noms sont les mêmes -> copie de l'ID du rang existant dans le rang à insérer</li>
     *            <li>Si les deux noms sont différents. Détermination (plus tard) du bon nom pour ce rang
     *                  <ul>
     *                      <li>Si le rang à ajouter existe déjà -> Détermination du bon nom PUIS merge des deux branches de classification (cas A1)</li>
     *                      <li>Si le rang à ajouter n'existe pas -> Détermination du bon nom (cas A2)</li>
     *                  </ul>
     *            </li>
     *          </ul>
     *        </td>
     *      </tr>
     *      <tr>
     *        <td>significatif</td>
     *        <td>liaison</td>
     *        <td>
     *            <u>Le rang existant devient significatif</u>
     *            <br/>
     *            <ul>
     *              <li>Si le rang à ajouter existe déjà -> cela signifie que deux branches de classification se trouve être la même. La fusion de ces deux branches et nécessaire pour assurer la consistance des données (cas B)</li>
     *              <li>Si le rang à ajouter n'existe pas -> copie de l'ID du rang existant dans le rang à insérer</li>
     *            </ul>
     *      </td>
     *      </tr>
     *      <tr>
     *        <td><hr>liaison</td>
     *        <td><hr>significatif</td>
     *        <td><hr>Le rang de liaison à ajouter se trouve être un rang significatif. Copie de l'ID et du nom</td>
     *      </tr>
     *      <tr>
     *        <td><hr>liaison</td>
     *        <td><hr>liaison</td>
     *        <td><hr>Copie de l'ID du rang existant dans le rang à insérer</td>
     *      </tr>
     *      <tr>
     *      </tr>
     *   </tbody>
     * </table>
     *
     * @param synchronizationResult objet dans le lequel les conflits sont ajoutés
     * @param existingRank          Rang existant en base de données. Doit posséder un ID
     * @param scrapedRank           Rang scrapé dont il faut vérifier qu'il pourra être enregistré en base de données sans briser les invariants
     * @throws MoreThanOneResultException Dans le cas où la base de données est déjà inconsistante, car un nom retourne plusieurs résultats (ne pourra jamais arriver parce qu'une contrainte en base l'interdit).
     */
    private void updateScrapedRankOrAddConflict(@NotNull ClassificationConflict synchronizationResult, @NotNull CronquistRank existingRank, @NotNull CronquistRank scrapedRank) throws MoreThanOneResultException {
        if (existingRank.getId() == null) {
            log.error("Impossible de vérifier la consistance du rang scrapé : le rang existant reçu est null!");
            return;
        }
        boolean scrapedRankIsConnection = CronquistClassificationBranch.isRangDeLiaison(scrapedRank);
        boolean existingRankIsSignificant = !CronquistClassificationBranch.isRangDeLiaison(existingRank);
        boolean bothSignificativeWithTheSameName = existingRankIsSignificant && !scrapedRankIsConnection && Objects.equals(scrapedRank.getNom(), existingRank.getNom());

        if (scrapedRankIsConnection || bothSignificativeWithTheSameName) {
            scrapedRank.setId(existingRank.getId());
            scrapedRank.setNom(existingRank.getNom());
            return;
        }

        if (existingRankIsSignificant) {
            synchronizationResult.addConflict(scrapedRank, existingRank);
        } else {
            CronquistRank existingRank1 = cronquistReader.findExistingRank(scrapedRank);
            if (existingRank1 != null) {
                synchronizationResult.addConflict(scrapedRank, existingRank);
            } else {
                scrapedRank.setId(existingRank.getId());
            }
        }
    }

    @Contract(pure = true)
    public ClassificationConflict resolveInconsistencyInDatabase(@NotNull ClassificationConflict conflicts) throws InconsistencyResolverException, MoreThanOneResultException {
        ClassificationConflict resolvedConflicts = new ClassificationConflict();
        resolvedConflicts.setNewClassification(conflicts.getNewClassification());


        for (ConflictualRank conflictualRank : conflicts.getConflictedClassifications()) {

            boolean sameNameForDistinctsTaxonomicRanks = !conflictualRank.getScraped().getRank().equals(conflictualRank.getExisting().getRank()) &&
                Objects.equals(conflictualRank.getScraped().getNom(), conflictualRank.getExisting().getNom());
            if (sameNameForDistinctsTaxonomicRanks) {
                // One of the ranks is misplaced, resolve it
                resolveMisplacedRank(resolvedConflicts, conflictualRank);
                continue;
            }
            throwIfMalformedParameters(conflictualRank);

            boolean scrapedRankSignificant = !CronquistClassificationBranch.isRangDeLiaison(conflictualRank.getScraped());
            boolean isExistingRankSignificant = !CronquistClassificationBranch.isRangDeLiaison(conflictualRank.getExisting());

            CronquistRank scrapedRank = cronquistReader.findExistingRank(conflictualRank.getScraped());
            CronquistRank existingRank = cronquistReader.findExistingRank(conflictualRank.getExisting());
            scrapedRank = getNullIfConnectionRank(scrapedRank);
            existingRank = getNullIfConnectionRank(existingRank);

            // Si l'un des deux est un rang de liaison (OU EXCLUSIF), les deux rangs doivent être fusionnés. Cas B
            if (!isExistingRankSignificant) {
                // Le rang non null est forcément celui qui possède un nom // TODO extract this for more readability
                CronquistRank rankWhichReceivingChildren = scrapedRank != null ? scrapedRank : existingRank;
                // L'autre est donc forcément le rang de liaison
                CronquistRank rankWhichWillBeMergedIntoTheOther = scrapedRank == null ? conflictualRank.getScraped() : conflictualRank.getExisting();

                CronquistRank mergedRank = cronquistWriter.mergeTheseRanks(rankWhichReceivingChildren, rankWhichWillBeMergedIntoTheOther);
                if (mergedRank == null) {
                    //return conflictualRank; TODO test en échec de cette condition? Dans quel cas garantir que le merge échouera ?
                    resolvedConflicts.addConflict(conflictualRank);
                }
                continue;
            }

            // Cas A1 & A2. Détermination du bon rang dans tous les cas puis merge dans le cas 2 (les deux rangs sont significatifs et existent déjà)
            validateEachRankIsCronquistByScrapingAndPersistResult(resolvedConflicts, conflictualRank);

            // TODO merge
            CronquistRank validatedRank = null;
            if (scrapedRank != null && existingRank != null) {
                // Détermination de quel rang utiliser pour le merge
                validatedRank = scrapedRank;// TODO implémenter le scraping pour checker les deux noms. EN ATTENDANT on considère que le scrapedRank porte le bon nom de rang
            }
            // Résolution des conflits
            // Détermination, merge, etc
            //return conflictualRank;// TODO construct new conflict based on the previous revolving process

        }


        return resolvedConflicts;
    }

    private void throwIfMalformedParameters(@NotNull ConflictualRank conflictualRank) throws InconsistencyResolverException {
        if (
            !conflictualRank.getScraped().getRank().equals(conflictualRank.getExisting().getRank()) &&
                !Objects.equals(conflictualRank.getScraped().getNom(), conflictualRank.getExisting().getNom())
        ) {
            throw new InconsistencyResolverException("Resolve rank inconsistency imply to treat with : two ranks of the same taxonomic rank OR two ranks with the same name");
        }
        if (Objects.equals(conflictualRank.getScraped().getNom(), conflictualRank.getExisting().getNom())) {
            throw new InconsistencyResolverException("Resolve rank inconsistency imply to treat with two conflicted ranks, with at least not the same name");
        }
    }

    private void resolveMisplacedRank(ClassificationConflict resolvedConflicts, @NotNull ConflictualRank conflictualRank) {
        String name = conflictualRank.getScraped().getNom();
        @Nullable ScrapedPlant freshlyScrapRank = scrapTheRank(name);

        boolean isTheExistingHasTheRightRank;
        boolean isTheScrapedHasTheRightRank;
        if (freshlyScrapRank == null) {
            log.warn("Impossible de scraper la plante {}", name);
            return;
        }
        if (freshlyScrapRank.getCronquistClassificationBranch() == null) {
            log.warn("Echec de la récupération de la classification de Cronquist de {}", name);
            return;
        }
        CronquistRank lowestRank = freshlyScrapRank.getCronquistClassificationBranch().getLowestRank();
        isTheExistingHasTheRightRank = Objects.equals(conflictualRank.getExisting().getRank(), lowestRank.getRank());
        isTheScrapedHasTheRightRank = Objects.equals(conflictualRank.getScraped().getRank(), lowestRank.getRank());

        // if it's scraped rank which has the good rank, delete existing (and rename another existing?)
        if (!isTheExistingHasTheRightRank) {
            cronquistWriter.removeRank(conflictualRank.getExisting());
        }
        // if it's the existing rank which has the good rank, just update the classification to insert
        if (!isTheScrapedHasTheRightRank) {
            resolvedConflicts.getNewClassification().remove(conflictualRank.getScraped());
        }
    }

    /**
     * Update database if necessary
     *
     * @param resolvedConflicts the inner classification is modified if a rank must be deleted or updated
     * @param conflictualRank   the object which contain the two problematic ranks
     */
    private void validateEachRankIsCronquistByScrapingAndPersistResult(ClassificationConflict resolvedConflicts, ConflictualRank conflictualRank) {
        ConflictualRank freshlyScrapRanks = scrapClassificationForEach(conflictualRank);
        boolean isTheScrapedRankACronquistRank = freshlyScrapRanks.getScraped() != null;
        boolean isTheExistingRankACronquistRank = freshlyScrapRanks.getExisting() != null;

        boolean theExistingRankMustBeOverrideWithTheScraped = isTheScrapedRankACronquistRank && !isTheExistingRankACronquistRank;
        if (theExistingRankMustBeOverrideWithTheScraped) {
            // Mettre à jour la base de donnée
            cronquistWriter.updateRank(conflictualRank.getExisting(), freshlyScrapRanks.getScraped());
        }

        boolean theScrapedRankMustBeOverrideWithTheExisting = !isTheScrapedRankACronquistRank && isTheExistingRankACronquistRank;
        if (theScrapedRankMustBeOverrideWithTheExisting) {
            // Mettre à jour la classification à enregistrer
            resolvedConflicts.getNewClassification().add(conflictualRank.getExisting());
        }

        boolean neitherExistingNorScrapedRanksBelongsToCronquist = !isTheScrapedRankACronquistRank && !isTheExistingRankACronquistRank;
        if (neitherExistingNorScrapedRanksBelongsToCronquist) {
            resolvedConflicts.getNewClassification().remove(conflictualRank.getScraped());
            cronquistWriter.removeRank(conflictualRank.getExisting());// TODO cas de test, les rangs enregistrés se trouvent ne pas être du Cronquist
        }

        if (isTheScrapedRankACronquistRank && isTheExistingRankACronquistRank) {
            // TODO si les deux plantes existent en Cronquist, gros problème. S'ils ne sont pas du même rang ok gros merge sinon c'est la m**** parce qu'un invariant est brisé
            resolvedConflicts.addConflict(conflictualRank);
        }
    }

    /**
     * Scrap each of conflictual ranks and return the one who is a cronquist classification
     *
     * @param conflictualRank Contains the two rank for whose I want to know if it belongs to Cronquist classification
     * @return The scrapedPlant corresponding to one of the passed rank. null if none of them is a cronquist rank
     */
    private @NotNull ConflictualRank scrapClassificationForEach(@NotNull ConflictualRank conflictualRank) {
        ConflictualRank conflictualRank1 = new ConflictualRank();

        String scrapedNom = conflictualRank.getScraped().getNom();
        @Nullable ScrapedPlant scrapedScrapedPlant = scrapTheRank(scrapedNom);
        if (scrapedScrapedPlant != null && scrapedScrapedPlant.getCronquistClassificationBranch() != null) {
            conflictualRank1.scrapedRank(scrapedScrapedPlant.getCronquistClassificationBranch().getLowestRank());
        }

        String existingNom = conflictualRank.getExisting().getNom();
        @Nullable ScrapedPlant scrapedExistingPlant = scrapTheRank(existingNom);
        if (scrapedScrapedPlant != null && scrapedExistingPlant.getCronquistClassificationBranch() != null) {
            conflictualRank1.existing(scrapedExistingPlant.getCronquistClassificationBranch().getLowestRank());
        }

        return conflictualRank1;
    }

    private @Nullable ScrapedPlant scrapTheRank(String nom) {
        try {
            return webScrapingService.scrapPlant(nom);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NonExistentWikiPageException | PlantNotFoundException e) {
            log.warn("{}: Unable to find a wiki page for {}", e.getClass(), nom);
            return null;
        }
    }

    @Nullable
    private static CronquistRank getNullIfConnectionRank(CronquistRank rank1) {
        return rank1 != null && rank1.getNom() != null ? rank1 : null;
    }

}
