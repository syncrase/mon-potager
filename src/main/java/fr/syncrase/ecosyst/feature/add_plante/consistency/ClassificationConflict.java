package fr.syncrase.ecosyst.feature.add_plante.consistency;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Represent a conflict which may occur when trying to save a new classification.<br/>
 * The conflict occur when an invariant is broken<br/>
 */
public class ClassificationConflict {
    /**
     * A set of ranks which have the same name. The determination of which is the correct one must be performed<br/>
     * If this list is empty, there will be no conflict on saving. The consistency is guaranteed
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

    public void addConflict(@NotNull CronquistRank scrapedRank, @NotNull CronquistRank existingRank) {
        ConflictualRank conflict = new ConflictualRank().existing(existingRank).scrapedRank(scrapedRank);
        conflictedClassifications.add(conflict);
    }

    public void addAllConflicts(Set<ConflictualRank> conflict) {
        conflictedClassifications.addAll(conflict);
    }

    public void addConflict(ConflictualRank conflict) {
        if (conflict != null) {
            conflictedClassifications.add(conflict);
        }
    }
}
