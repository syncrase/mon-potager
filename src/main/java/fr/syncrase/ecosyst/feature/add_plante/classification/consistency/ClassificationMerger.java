package fr.syncrase.ecosyst.feature.add_plante.classification.consistency;

import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.Url;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.wrapper.CronquistRankWrapper;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.ICronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.ClassificationNom;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.enumeration.RankName;
import fr.syncrase.ecosyst.repository.MoreThanOneResultException;
import fr.syncrase.ecosyst.repository.UnknownRankId;
import fr.syncrase.ecosyst.repository.database.ClassificationNomRepository;
import fr.syncrase.ecosyst.repository.database.CronquistRankRepository;
import fr.syncrase.ecosyst.repository.database.UrlRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClassificationMerger {

    private final Logger log = LoggerFactory.getLogger(ClassificationMerger.class);

    private CronquistRankRepository cronquistRankRepository;
    private ClassificationNomRepository classificationNomRepository;
    private UrlRepository urlRepository;

    @Autowired
    public void setCronquistRankRepository(CronquistRankRepository cronquistRankRepository) {
        this.cronquistRankRepository = cronquistRankRepository;
    }

    @Autowired
    public void setClassificationNomRepository(ClassificationNomRepository classificationNomRepository) {
        this.classificationNomRepository = classificationNomRepository;
    }

    @Autowired
    public void setUrlRepository(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public ICronquistRank merge(
        @NotNull CronquistClassificationBranch brancheSource,
        @NotNull CronquistClassificationBranch brancheCible,
        @NotNull RankName rankWhereTheMergeIsApplied
                               ) throws UnknownRankId, InconsistentRank, MoreThanOneResultException {

        Optional<CronquistRank> rangQuiSeraSupprimeOptional = cronquistRankRepository.findById(brancheSource.getRang(rankWhereTheMergeIsApplied).getId());
        Optional<CronquistRank> rangQuiRecupereLesInfosOptional = cronquistRankRepository.findById(brancheCible.getRang(rankWhereTheMergeIsApplied).getId());
        if (rangQuiSeraSupprimeOptional.isPresent() && rangQuiRecupereLesInfosOptional.isPresent()) {
            CronquistRank rangQuiSeraSupprime = rangQuiSeraSupprimeOptional.get();
            CronquistRank rangQuiRecupereLesInfos = rangQuiRecupereLesInfosOptional.get();

            MergeContainer mergeContainer = new MergeContainer(rangQuiSeraSupprime, rangQuiRecupereLesInfos);

            copyDataFromRankToRank(mergeContainer);
            switchAncestry(mergeContainer);

            supprimeLiaisonAscendante(mergeContainer.getRangQuiSeraSupprime());

            return new CronquistRankWrapper(mergeContainer.getRangQuiRecupereLesInfos());

        } else {
            throw new UnknownRankId();
        }
    }

    private void supprimeLiaisonAscendante(@NotNull CronquistRank rangAPartirDuquelLAscendanceEstSupprimee) {
        do {
            log.debug("Suppression du rang " + rangAPartirDuquelLAscendanceEstSupprimee);
            Set<Long> nomsIds = rangAPartirDuquelLAscendanceEstSupprimee.getNoms().stream().map(ClassificationNom::getId).collect(Collectors.toSet());
            log.debug("Suppression des noms " + nomsIds);
            classificationNomRepository.deleteAllById(nomsIds);
            Set<Long> urlsIds = rangAPartirDuquelLAscendanceEstSupprimee.getUrls().stream().map(Url::getId).collect(Collectors.toSet());
            log.debug("Suppression des urls " + urlsIds);
            urlRepository.deleteAllById(urlsIds);
            log.debug("Suppression du rangAPartirDuquelLAscendanceEstSupprimee " + rangAPartirDuquelLAscendanceEstSupprimee.getId());
            cronquistRankRepository.deleteById(rangAPartirDuquelLAscendanceEstSupprimee.getId());
            rangAPartirDuquelLAscendanceEstSupprimee = rangAPartirDuquelLAscendanceEstSupprimee.getParent();
        } while (CronquistRankUtils.isRangDeLiaison(rangAPartirDuquelLAscendanceEstSupprimee));
    }

    private void copyDataFromRankToRank(
        @NotNull MergeContainer mergeContainer
                                       ) throws InconsistentRank {
        addNamesFromRankToRank(mergeContainer);
        addUrlsFromRankToRank(mergeContainer);
    }

    private void switchAncestry(
        @NotNull MergeContainer mergeContainer
                               ) {
        CronquistRank newParent = mergeContainer.getRangQuiRecupereLesInfos();
        CronquistRank rangSource = mergeContainer.getRangQuiSeraSupprime();
        log.debug(String.format("Change l'ascendance des taxons du rang %s", rangSource.getId()));
        // Tous les taxons changent de parent
        // → enregistrement des taxons avec le nouveau parent
        Set<CronquistRank> taxons = rangSource.getChildren();
        Set<CronquistRank> taxonsWithNewParent = CronquistRankUtils.setParentToAllTaxons(newParent, taxons);
        cronquistRankRepository.saveAll(taxonsWithNewParent);

        // Enregistrement de l'ancien parent sans ses taxons
        rangSource.getChildren().clear();
        cronquistRankRepository.save(rangSource);
        cronquistRankRepository.save(newParent);
    }

    private void addNamesFromRankToRank(
        @NotNull MergeContainer mergeContainer
                                       ) throws InconsistentRank {
        CronquistRank rangCible = mergeContainer.getRangQuiRecupereLesInfos();
        CronquistRank rangSource = mergeContainer.getRangQuiSeraSupprime();

        if (CronquistRankUtils.isRangDeLiaison(rangCible) && CronquistRankUtils.isRangSignificatif(rangSource)) {
            if (rangCible.getNoms().size() != 1) {
                throw new InconsistentRank("Le rang de liaison ne doit pas posséder plus d'un nom " + rangCible);
            }
            Set<Long> ranksIdToDelete = rangCible.getNoms().stream().map(ClassificationNom::getId).collect(Collectors.toSet());
            log.debug(String.format("Suppression du nom %s appartenant au rang %s", ranksIdToDelete, rangCible.getId()));
            classificationNomRepository.deleteAllById(
                ranksIdToDelete
                                                     );
            CronquistRankUtils.removeAllNames(rangCible, ranksIdToDelete);
        }
        log.debug(String.format("Ajout des noms %s au rang %s", CronquistRankUtils.getNomsIds(rangSource), rangCible.getId()));
        CronquistRankUtils.addNoms(rangCible, rangSource.getNoms());
        rangSource.getNoms().clear();
    }

    private void addUrlsFromRankToRank(
        @NotNull MergeContainer mergeContainer
                                      ) {
        CronquistRank rangCible = mergeContainer.getRangQuiRecupereLesInfos();
        CronquistRank rangSource = mergeContainer.getRangQuiSeraSupprime();

        CronquistRankUtils.addUrls(rangCible, rangSource.getUrls());
        rangSource.getUrls().clear();
    }

    /**
     * Utilisé pour faire transiter les deux rangs à merger d'une méthode à l'autre.
     * SIDE-EFFECT
     */
    private static class MergeContainer {
        private final CronquistRank rangQuiSeraSupprime;
        private final CronquistRank rangQuiRecupereLesInfos;

        public MergeContainer(CronquistRank rangQuiSeraSupprime, CronquistRank rangQuiRecupereLesInfos) {
            this.rangQuiSeraSupprime = rangQuiSeraSupprime;
            this.rangQuiRecupereLesInfos = rangQuiRecupereLesInfos;
        }

        public CronquistRank getRangQuiSeraSupprime() {
            return rangQuiSeraSupprime;
        }

        public CronquistRank getRangQuiRecupereLesInfos() {
            return rangQuiRecupereLesInfos;
        }
    }
}
