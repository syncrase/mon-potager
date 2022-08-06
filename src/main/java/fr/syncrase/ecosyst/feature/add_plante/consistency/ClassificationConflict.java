package fr.syncrase.ecosyst.feature.add_plante.consistency;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;

import java.util.HashSet;
import java.util.Set;

/**
 * Represent a conflict which may occur when trying to save a new classification.<br/>
 * The conflict occur when an invariant is broken<br/>
 */
public class ClassificationConflict {
    /**
     * Represent the whole classifications with which a conflict will occur of I'm trying to save the classification.
     * If this list is empty, there will be no conflict on saving
     */
    Set<ConflictualRank> conflictedClassifications;

    /**
     * The classification I want to save in the database.
     */
    CronquistClassificationBranch newClassification;

    public ClassificationConflict() {
        this.conflictedClassifications = new HashSet<>();
    }

    public Set<ConflictualRank> getConflictedClassifications() {
        return conflictedClassifications;
    }

    public void setConflictedClassifications(Set<ConflictualRank> conflictedClassifications) {
        this.conflictedClassifications = conflictedClassifications;
    }

    public CronquistClassificationBranch getNewClassification() {
        return newClassification;
    }

    public void setNewClassification(CronquistClassificationBranch newClassification) {
        this.newClassification = newClassification;
    }

    public void addConflict(CronquistRank scrapedRank, CronquistRank existingRank) {
        ConflictualRank conflict = new ConflictualRank(scrapedRank, existingRank);
    }

    private class ConflictualRank {
        private final CronquistRank existingRank;
        private final CronquistRank scrapedRank;

        public ConflictualRank(CronquistRank scrapedRank, CronquistRank existingRank) {
            this.scrapedRank = scrapedRank;
            this.existingRank = existingRank;
        }

        public CronquistRank getExistingRank() {
            return existingRank;
        }

        public CronquistRank getScrapedRank() {
            return scrapedRank;
        }
    }
}
