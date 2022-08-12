package fr.syncrase.ecosyst.feature.add_plante.repository;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.feature.add_plante.consistency.InconsistencyResolverException;
import fr.syncrase.ecosyst.feature.add_plante.repository.exception.MoreThanOneResultException;
import fr.syncrase.ecosyst.repository.CronquistRankRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;


@Service
public class CronquistWriter {

    private final Logger log = LoggerFactory.getLogger(CronquistWriter.class);

    private CronquistRankRepository cronquistRankRepository;

    private CronquistReader cronquistReader;

    @Autowired
    public void setCronquistReader(CronquistReader cronquistReader) {
        this.cronquistReader = cronquistReader;
    }

    @Autowired
    public void setCronquistRankRepository(CronquistRankRepository cronquistRankRepository) {
        this.cronquistRankRepository = cronquistRankRepository;
    }

    public CronquistClassificationBranch saveClassification(CronquistClassificationBranch newClassification) {
        log.info(String.format("Save the classification %s", newClassification));
        if (newClassification.getClassificationSet() == null || newClassification.getClassificationSet().size() == 0) {
            log.error("No classification to save");
            return null;
        }

        newClassification.setConsistantParenthood();
        Iterator<CronquistRank> iterator = newClassification.getClassificationSet().descendingIterator();
        while (iterator.hasNext()) {
            cronquistRankRepository.save(iterator.next());
        }
        return newClassification;
    }

    public void removeClassification(CronquistClassificationBranch classificationToRemove) {
        log.info(String.format("Delete the classification %s", classificationToRemove));
        if (classificationToRemove.getClassificationSet() == null || classificationToRemove.getClassificationSet().size() == 0) {
            log.error("No classification to delete");
            return;
        }


        cronquistRankRepository.deleteAll(classificationToRemove.getClassificationSet());

        //Iterator<CronquistRank> iterator = classificationToRemove.getClassificationSet().descendingIterator();
        //while(iterator.hasNext()){
        //    cronquistRankRepository.save(iterator.next());
        //}

    }

    /**
     * @param rankWhichReceivingChildren        rank which will receive children
     * @param rankWhichWillBeMergedIntoTheOther rank which will be deleted like the upper connection classification segment
     * @return true if the merge succeeded, false otherwise
     * @throws InconsistencyResolverException when passed ranks cannot be merged because of lack of data
     */
    @Transactional
    public CronquistRank mergeTheseRanks(CronquistRank rankWhichReceivingChildren, CronquistRank rankWhichWillBeMergedIntoTheOther) throws InconsistencyResolverException, MoreThanOneResultException {
        if (rankWhichReceivingChildren == null || rankWhichWillBeMergedIntoTheOther == null) {
            throw new InconsistencyResolverException("Resolve rank inconsistency imply to treat with two not null ranks");
        }
        if (rankWhichReceivingChildren.getId() == null || rankWhichWillBeMergedIntoTheOther.getId() == null) {
            throw new InconsistencyResolverException("Resolve rank inconsistency imply to treat with connection name identified " + rankWhichReceivingChildren + " into" + rankWhichReceivingChildren);
        }

        rankWhichReceivingChildren = cronquistReader.findExistingRank(rankWhichReceivingChildren);
        rankWhichWillBeMergedIntoTheOther = cronquistReader.findExistingRank(rankWhichWillBeMergedIntoTheOther);

        if (rankWhichReceivingChildren == null || rankWhichWillBeMergedIntoTheOther == null) {
            throw new InconsistencyResolverException("Resolve rank inconsistency imply to treat with existing ranks");
        }

        // Ajout du parent à tous les enfants
        for (CronquistRank child : rankWhichWillBeMergedIntoTheOther.getChildren()) {
            child.setParent(rankWhichReceivingChildren);
        }
        // Ajout des enfants au nouveau parent
        rankWhichReceivingChildren.getChildren().addAll(rankWhichWillBeMergedIntoTheOther.getChildren());

        // Suppression de la branch de liaison
        CronquistRank goingToBeDeletedRank;
        do {
            goingToBeDeletedRank = rankWhichWillBeMergedIntoTheOther;
            log.debug("Suppression du rang de liaison obsolète id={} ", goingToBeDeletedRank.getId());
            cronquistRankRepository.deleteById(goingToBeDeletedRank.getId());
            goingToBeDeletedRank = goingToBeDeletedRank.getParent();
        } while (CronquistClassificationBranch.isRangDeLiaison(goingToBeDeletedRank));

        return cronquistRankRepository.save(rankWhichReceivingChildren);
    }

    public void updateRank(@NotNull CronquistRank toBeUpdated, @NotNull CronquistRank updateWith) {
        toBeUpdated.setNom(updateWith.getNom());
        cronquistRankRepository.save(toBeUpdated);
    }

    public void removeRank(@NotNull CronquistRank existing) {
        existing.setNom(null);
        cronquistRankRepository.save(existing);

    }
}
