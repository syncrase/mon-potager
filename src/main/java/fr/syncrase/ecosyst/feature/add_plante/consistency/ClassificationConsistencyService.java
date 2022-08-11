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
        CronquistClassificationBranch scrapedClassification = new CronquistClassificationBranch(toSaveCronquistClassification);// TODO clone this
        CronquistClassificationBranch existingClassification = cronquistReader.findExistingPartOfThisClassification(scrapedClassification);
        if (existingClassification != null) {
            for (CronquistRank existingRank : existingClassification) {
                CronquistRank scrapedRank;
                scrapedRank = scrapedClassification.getRang(existingRank.getRank());
                updateScrapedRankOrAddConflict(synchronizationResult, existingRank, scrapedRank);
            }
        }
        synchronizationResult.setNewClassification(scrapedClassification);
        return synchronizationResult;
    }

    /**
     * Check if any inconsistency exists<br/>
     * The more complexe use case is in the case of a significant rank in the classification to be saved already exists in another classification segment, this is a conflict which will require a merge management.
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
        boolean connectionScrapedRank = CronquistClassificationBranch.isRangDeLiaison(scrapedRank);
        boolean existingRankIsSignificant = !CronquistClassificationBranch.isRangDeLiaison(existingRank);
        boolean bothSignificativeWithTheSameName = existingRankIsSignificant && !connectionScrapedRank && Objects.equals(scrapedRank.getNom(), existingRank.getNom());

        if (connectionScrapedRank || bothSignificativeWithTheSameName) {
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
    public ClassificationConflict resolveInconsistency(@NotNull ClassificationConflict conflicts) throws InconsistencyResolverException, MoreThanOneResultException {
        ClassificationConflict resolvedConflicts = new ClassificationConflict();
        resolvedConflicts.setNewClassification(conflicts.getNewClassification());


        for (ConflictualRank conflictualRank : conflicts.getConflictedClassifications()) {
            if (!conflictualRank.getScraped().getRank().equals(conflictualRank.getExisting().getRank())) {
                throw new InconsistencyResolverException("Resolve rank inconsistency imply to treat with two ranks of the same taxonomic rank");
            }
            if (Objects.equals(conflictualRank.getScraped().getNom(), conflictualRank.getExisting().getNom())) {
                throw new InconsistencyResolverException("Resolve rank inconsistency imply to treat with two conflicted ranks, with at least not the same name");
            }
            //resolvedConflicts.addConflict(manageConflict(conflictualRank));
            boolean scrapedRankSignificant = !CronquistClassificationBranch.isRangDeLiaison(conflictualRank.getScraped());
            boolean isExistingRankSignificant = !CronquistClassificationBranch.isRangDeLiaison(conflictualRank.getExisting());

            CronquistRank scrapedRank = cronquistReader.findExistingRank(conflictualRank.getScraped());
            CronquistRank existingRank = cronquistReader.findExistingRank(conflictualRank.getExisting());
            scrapedRank = getNullIfConnectionRank(scrapedRank);
            existingRank = getNullIfConnectionRank(existingRank);

            // Si l'un des deux est un rang de liaison (OU EXCLUSIF), les deux rangs doivent être fusionnés. Cas B
            if (!isExistingRankSignificant) {
                // Le rang non null est forcément celui qui possède un nom
                CronquistRank rankWhichReceivingChildren = scrapedRank != null ? scrapedRank : existingRank;
                // L'autre est donc forcément le rang de liaison
                CronquistRank rankWhichWillBeMergedIntoTheOther = scrapedRank == null ? conflictualRank.getScraped() : conflictualRank.getExisting();

                if (!cronquistWriter.mergeTheseRanks(rankWhichReceivingChildren, rankWhichWillBeMergedIntoTheOther)) {
                    //return conflictualRank;
                    resolvedConflicts.addConflict(conflictualRank);
                }
                continue;
            }

            // Cas A1 & A2. Détermination du bon rang dans tous les cas puis merge dans le cas 2 (les deux rangs sont significatifs et existent déjà)
            ScrapedPlant plante = getTheRightOne(conflictualRank);// TODO si les deux plantes existent en Cronquist, gros problème. S'ils ne sont pas du même rang ok gros merge sinon c'est la m**** parce qu'un invariant est brisé
            if (plante != null) {
                boolean scrapedRankIsTheRightOne = conflictualRank.getScraped().getNom().contentEquals(plante.getCronquistClassificationBranch().getLowestRank().getNom());
                if (scrapedRankIsTheRightOne) {
                    // Mettre à jour la base de donnée
                    cronquistWriter.updateRank(conflictualRank.getExisting(), plante.getCronquistClassificationBranch().getLowestRank());
                }
                boolean existingRankIsTheRightOne = conflictualRank.getScraped().getNom().contentEquals(plante.getCronquistClassificationBranch().getLowestRank().getNom());
                if (existingRankIsTheRightOne) {
                    // Mettre à jour la classification à enregistrer
                    resolvedConflicts.getNewClassification().add(conflictualRank.getExisting());
                }
            } else {
                cronquistWriter.removeRank(conflictualRank.getExisting());// TODO cas de test, les rang enregistré se trouve ne pas être du Cronquist
            }

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

    private @Nullable ScrapedPlant getTheRightOne(@NotNull ConflictualRank conflictualRank) {
        boolean isScrapedExistsInCronquist, isExistingExistsInCronquist;
        String scrapedNom = conflictualRank.getScraped().getNom();
        @Nullable ScrapedPlant scrapedScrapedPlant = scrapTheRank(scrapedNom);
        isScrapedExistsInCronquist = scrapedScrapedPlant != null;
        String existingNom = conflictualRank.getExisting().getNom();
        @Nullable ScrapedPlant scrapedExistingPlant = scrapTheRank(existingNom);
        isExistingExistsInCronquist = scrapedExistingPlant != null;

        boolean onlyScrapedInCronquist = isScrapedExistsInCronquist && !isExistingExistsInCronquist;
        if (onlyScrapedInCronquist) {
            return scrapedScrapedPlant;
        }
        boolean onlyExistingInCronquist = !isScrapedExistsInCronquist && isExistingExistsInCronquist;
        if (onlyExistingInCronquist) {
            return scrapedExistingPlant;
        }
        boolean noneIsInCronquist = !isScrapedExistsInCronquist;
        if (noneIsInCronquist) {
            return null;
        }
        log.error("To be implemented : Both ranks exists in Cronquist. {} and {}", scrapedScrapedPlant, scrapedExistingPlant);
        throw new RuntimeException();
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
