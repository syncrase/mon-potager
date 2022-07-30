package fr.syncrase.ecosyst.feature.add_plante.classification.consistency;

import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.IClassificationNom;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.ICronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.IUrl;
import fr.syncrase.ecosyst.feature.add_plante.classification.enumeration.RankName;
import fr.syncrase.ecosyst.repository.ClassificationReader;
import fr.syncrase.ecosyst.repository.ClassificationRepository;
import fr.syncrase.ecosyst.repository.MoreThanOneResultException;
import fr.syncrase.ecosyst.repository.UnknownRankId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * À partir de la classification reçue par le constructeur, merge avec la classification existante en base.<br>
 * Gère :
 * <ul>
 *     <li>le merge de deux branches existantes</li>
 *     <li>l'assignation des urls</li>
 *     <li>garanti la synchronisation des IDs</li>
 * </ul>
 */
public class CronquistClassificationConsistency {

    private final Logger log = LoggerFactory.getLogger(CronquistClassificationConsistency.class);

    private final ClassificationRepository classificationRepository;
    private final ClassificationReader classificationReader;

    public CronquistClassificationConsistency(ClassificationRepository classificationRepository, ClassificationReader classificationReader) {
        this.classificationRepository = classificationRepository;
        this.classificationReader = classificationReader;
    }


    /**
     * Parcours la classification pour rendre l'existant cohérent avec le rang à insérer
     *
     * @param existingClassification une branche de classification existante en base de données
     * @param scrappedClassification classification scrappée devant être inséré en base de données
     * @return Une nouvelle ClassificationBranch construite à partir de la classification scrapé puis rendue cohérente avec les données déjà existantes en base
     */
    private @NotNull CronquistClassificationBranch makeItConsistent(
        @NotNull CronquistClassificationBranch existingClassification,
        @NotNull CronquistClassificationBranch scrappedClassification
                                                                   ) throws UnknownRankId, MoreThanOneResultException, InconsistentRank, ClassificationReconstructionException {
        Collection<ICronquistRank> existingClassificationList = existingClassification.getClassification();
        if (existingClassificationList.size() == 0) {
            log.info("No existing entity in the database");
            return scrappedClassification;
        }
        CronquistClassificationBranch consistentClassification = scrappedClassification.clone();
        // Parcours des éléments pour ajouter les IDs des éléments connus
        RankName[] rangsDisponibles = RankName.values();
        int positionDansLaClassification = existingClassification.ranksCount() - 1;
        while (positionDansLaClassification >= 0) {
            RankName currentRankName = rangsDisponibles[positionDansLaClassification];
            ICronquistRank existingClassificationRang = existingClassification.getRang(currentRankName);
            if (existingClassificationRang == null) {
                positionDansLaClassification--;
                continue;
            }
            ICronquistRank consistentRank = manageRankConsistency(currentRankName, existingClassification, scrappedClassification);
            consistentClassification.put(currentRankName, consistentRank);
            positionDansLaClassification--;
        }
        consistentClassification.clearTail();
        return consistentClassification;
    }

    /**
     * <table>
     *   <thead>
     *     <tr>
     *       <th>rankToInsert de liaison</th>
     *       <th>existingRank de liaison</th>
     *       <th>Action</th>
     *     </tr>
     *    </thead>
     *    <tbody>
     *      <tr>
     *        <td>non</td>
     *        <td>non</td>
     *        <td>Gestion de deux rangs significatifs. Si le nom est inconnu, il est ajouté à la liste des noms (synonyme). Sinon le rang est uniquement synchronisé</td>
     *      </tr>
     *      <tr>
     *        <td>non</td>
     *        <td>oui</td>
     *        <td>Le rang de liaison devient rang significatif. Si le rang à ajouter existe déjà, les deux branches de classification sont fusionnées</td>
     *      </tr>
     *      <tr>
     *        <td>oui</td>
     *        <td>non</td>
     *        <td>Le rang de liaison à ajouter se trouve être un rang significatif. Synchronisation du rang</td>
     *      </tr>
     *      <tr>
     *        <td>oui</td>
     *        <td>oui</td>
     *        <td>Synchronisation du rang de liaison avec la base de données</td>
     *      </tr>
     *      <tr>
     *      </tr>
     *   </tbody>
     * </table>
     *
     * @return Le rang qui est cohérent avec les données de classification déjà enregistrées
     */
    private @NotNull ICronquistRank manageRankConsistency(
        RankName rankName,
        @NotNull CronquistClassificationBranch existingClassification,
        @NotNull CronquistClassificationBranch scrappedClassification
                                                         ) throws UnknownRankId, MoreThanOneResultException, InconsistentRank, ClassificationReconstructionException {

        ICronquistRank existingRank;
        ICronquistRank rankToInsert;
        existingRank = existingClassification.getRang(rankName).clone();
        rankToInsert = scrappedClassification.getRang(rankName).clone();

        boolean lesDeuxRangsSontSignificatifs = !rankToInsert.isRangDeLiaison() && !existingRank.isRangDeLiaison();
        boolean uniquementLeRangScrappeEstSignificatif = !rankToInsert.isRangDeLiaison() && existingRank.isRangDeLiaison();
        if (lesDeuxRangsSontSignificatifs || uniquementLeRangScrappeEstSignificatif) {
            return merge(existingClassification, rankToInsert);
        }

        boolean uniquementLeRangExistantEstSignificatif = rankToInsert.isRangDeLiaison() && !existingRank.isRangDeLiaison();
        if (uniquementLeRangExistantEstSignificatif) {
            rankToInsert.setId(existingRank.getId());
            rankToInsert.addAllNamesToCronquistRank(existingRank.getNomsWrappers());
            rankToInsert.addAllUrlsToCronquistRank(existingRank.getIUrls());// TODO add test : vérifier que les adresses ne sont pas supprimées
            return rankToInsert;
        }

        boolean lesDeuxRangsSontDeLiaison = rankToInsert.isRangDeLiaison() && existingRank.isRangDeLiaison();
        if (lesDeuxRangsSontDeLiaison) {
            rankToInsert.setId(existingRank.getId());
            rankToInsert = synchronizeNames(existingRank, rankToInsert);
            rankToInsert = synchronizeUrl(existingRank, rankToInsert);
        }
        return rankToInsert;
    }

    /**
     * Met à jour la classification existante pour qu'elle corresponde puisse accueillir la classification scrappée
     *
     * @param existingClassification une branche de classification existante en base de données
     * @param rankToInsert           rang à insérer devant être intégrée à la classification existante
     * @return Le rang qui résulte du merge
     */
    private @NotNull ICronquistRank merge(
        @NotNull CronquistClassificationBranch existingClassification,
        @NotNull ICronquistRank rankToInsert
                                         ) throws UnknownRankId, MoreThanOneResultException, InconsistentRank, ClassificationReconstructionException {
        // TODO quand je merge le rang significatif dans le rang de liaison je dois supprimer le nom de liaison du rang de liaison
        ICronquistRank existingRank = existingClassification.getRang(rankToInsert.getRankName());
        boolean leRangAInsererNEstPasDansLaClassificationExistante = existingRank.isRangSignificatif() && !rankToInsert.doTheRankHasOneOfTheseNames(existingRank.getNomsWrappers());
        @Nullable ICronquistRank synonym = classificationReader.findExistingRank(rankToInsert);

        ICronquistRank consistentRank = existingRank;
        if ((leRangAInsererNEstPasDansLaClassificationExistante || existingRank.isRangDeLiaison()) && synonym != null) {
            consistentRank = classificationRepository.merge(existingClassification, synonym);
        }
        rankToInsert.setId(consistentRank.getId());
        rankToInsert = synchronizeNames(consistentRank, rankToInsert);
        rankToInsert = synchronizeUrl(consistentRank, rankToInsert);
        return rankToInsert;
    }

    /**
     * Synchronise le nouveau rang avec le rang existant. Ajoute-les ids aux noms connus que je souhaite enregistrer, ajoute l'ensemble des noms connu absent du rang à enregistrer
     *
     * @param existingRank rang existant
     * @param rankToInsert nouveau rang à insérer
     * @return La classification pouvant être insérée en données sans aucune incohérence de données. La base à été mise à jour pour accueillir la classification
     */
    private ICronquistRank synchronizeNames(
        @NotNull ICronquistRank existingRank,
        @NotNull ICronquistRank rankToInsert
                                           ) {
        assert rankToInsert.getNomsWrappers().size() == 1;
        ICronquistRank rankWithConsistentNames = rankToInsert.clone();

        for (IClassificationNom existingNom : existingRank.getNomsWrappers()) {
            boolean leNomSignificatifAInsererExisteDeja = rankWithConsistentNames.getNomsWrappers().stream()
                .map(IClassificationNom::getNomFr)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet())
                .contains(existingNom.getNomFr());

            if (leNomSignificatifAInsererExisteDeja) {
                rankWithConsistentNames.getNomsWrappers().forEach(toInsertNom -> {
                    if (toInsertNom.getNomFr().contentEquals(existingNom.getNomFr())) {
                        toInsertNom.setId(existingNom.getId());
                    }
                });
                continue;
            }

            if (existingRank.isRangDeLiaison()) {
                rankWithConsistentNames.getNomsWrappers().forEach(toInsertNom -> toInsertNom.setId(existingNom.getId()));
                continue;
            }

            // Le rang à insérer ne contient pas le nom significatif existant
            rankWithConsistentNames.addNameToCronquistRank(existingNom);
        }
        return rankWithConsistentNames;
    }

    public @NotNull CronquistClassificationBranch getConsistentClassification(
        @NotNull CronquistClassificationBranch scrappedClassification
                                                                             ) throws ClassificationReconstructionException, UnknownRankId, MoreThanOneResultException, InconsistentRank {
        scrappedClassification.inferAllRank();
        CronquistClassificationBranch existingClassification = classificationReader.findExistingPartOfThisClassification(scrappedClassification);
        if (existingClassification != null) {
            return makeItConsistent(existingClassification, scrappedClassification);
        }
        return scrappedClassification;
    }

    private ICronquistRank synchronizeUrl(
        @NotNull ICronquistRank existingRank,
        @NotNull ICronquistRank rankToInsert
                                         ) {

        ICronquistRank rankWithConsistentUrls = rankToInsert.clone();
        for (IUrl existingRankUrl : existingRank.getIUrls()) {
            boolean isTheRankToInsertContainsTheExistingUrl = rankWithConsistentUrls.getIUrls().stream().map(IUrl::getUrl).collect(Collectors.toSet()).contains(existingRankUrl.getUrl());
            if (isTheRankToInsertContainsTheExistingUrl) {
                rankWithConsistentUrls.getIUrls().forEach(iUrl -> {
                    if (iUrl.getUrl().contentEquals(existingRankUrl.getUrl())) {
                        iUrl.setId(existingRankUrl.getId());
                    }
                });
            } else {
                rankWithConsistentUrls.addUrl(existingRankUrl);
            }
        }
        return rankWithConsistentUrls;
    }


}
