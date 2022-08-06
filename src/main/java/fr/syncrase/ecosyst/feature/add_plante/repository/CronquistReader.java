package fr.syncrase.ecosyst.feature.add_plante.repository;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.ClassificationReconstructionException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import fr.syncrase.ecosyst.service.CronquistRankQueryService;
import fr.syncrase.ecosyst.service.criteria.CronquistRankCriteria;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.jhipster.service.filter.LongFilter;

import java.util.Iterator;
import java.util.List;

@Service
public class CronquistReader {

    private final Logger log = LoggerFactory.getLogger(CronquistReader.class);
    private CronquistRankQueryService cronquistRankQueryService;

    @Autowired
    public void setCronquistRankQueryService(CronquistRankQueryService cronquistRankQueryService) {
        this.cronquistRankQueryService = cronquistRankQueryService;
    }

    @Contract(pure = true)
    public CronquistClassificationBranch findExistingPartOfThisClassification(@NotNull CronquistClassificationBranch classification) throws ClassificationReconstructionException, MoreThanOneResultException {
        CronquistClassificationBranch existingClassification = null;
        Iterator<CronquistRank> rankIterator = classification.iterator();// TODO vérifier que je pars du rang le plus bas
        CronquistRank cronquistRank;
        while (rankIterator.hasNext()) {
            cronquistRank = findExistingRank(rankIterator.next());
            if (cronquistRank != null) {
                existingClassification = findExistingClassification(cronquistRank);
                assert existingClassification != null;
                break;// I got it!
            }
        }
        return existingClassification;
    }

    /**
     * Récupère la classification ascendante à partir du rang passé en paramètre
     *
     * @param cronquistRank Rang dont il faut vérifier la présence en base
     * @return Le rang qui correspond. Null si le rang n'existe pas
     */
    @Contract(pure = true)
    @Nullable
    public CronquistClassificationBranch findExistingClassification(@NotNull CronquistRank cronquistRank) throws ClassificationReconstructionException, MoreThanOneResultException {
        CronquistRank existingRank = findExistingRank(cronquistRank);
        if (existingRank != null) {
            return new CronquistClassificationBranch(existingRank);
        }
        return null;
    }

    /**
     * @param cronquistRank Rang dont il faut vérifier la présence en base
     * @return Le rang qui correspond. Null si le rang n'existe pas
     */
    @Contract(pure = true)
    @Nullable
    public CronquistRank findExistingRank(@NotNull CronquistRank cronquistRank) throws MoreThanOneResultException {
        if (CronquistClassificationBranch.isRangDeLiaison(cronquistRank) && cronquistRank.getId() == null) {
            return null;
        }
        return queryForCronquistRank(cronquistRank);
    }

    @Contract(pure = true)
    public @Nullable CronquistRank queryForCronquistRank(@NotNull CronquistRank cronquistRank) throws MoreThanOneResultException {
        CronquistRankCriteria rankCrit = new CronquistRankCriteria();

        if (cronquistRank.getId() != null) {
            LongFilter idFilter = new LongFilter();
            idFilter.setEquals(cronquistRank.getId());
            rankCrit.setId(idFilter);
        }

        if (cronquistRank.getRank() != null) {
            CronquistRankCriteria.CronquistTaxonomikRanksFilter rankFilter = new CronquistRankCriteria.CronquistTaxonomikRanksFilter();
            rankFilter.setEquals(cronquistRank.getRank());
            rankCrit.setRank(rankFilter);
        }

        rankCrit.setDistinct(true);
        List<CronquistRank> rank = cronquistRankQueryService.findByCriteria(rankCrit);
        switch (rank.size()) {
            case 0:
                return null;
            case 1:
                return rank.get(0);
            default:
                throw new MoreThanOneResultException("Provided criteria do not correspond to only one result : " + cronquistRank);
        }
    }

    public Boolean isRankExists(CronquistRank scrapedRank) {
        // TODO to be implemented
        return Boolean.FALSE;
    }
}
