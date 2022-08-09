package fr.syncrase.ecosyst.feature.add_plante.consistency;


import fr.syncrase.ecosyst.domain.CronquistRank;

public class ConflictualRank {
    private CronquistRank scraped;
    private CronquistRank existing;

    public ConflictualRank() {
    }

    public CronquistRank getScraped() {
        return scraped;
    }

    public CronquistRank getExisting() {
        return existing;
    }

    public ConflictualRank scrapedRank(CronquistRank scraped) {
        this.scraped = scraped;
        return this;
    }

    public ConflictualRank existing(CronquistRank existing) {
        this.existing = existing;
        return this;
    }
}
