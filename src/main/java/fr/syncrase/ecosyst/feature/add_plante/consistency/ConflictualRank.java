package fr.syncrase.ecosyst.feature.add_plante.consistency;


import fr.syncrase.ecosyst.domain.CronquistRank;

public class ConflictualRank {
    private final CronquistRank rank1;
    private final CronquistRank rank2;

    public ConflictualRank(CronquistRank rank2, CronquistRank rank1) {
        this.rank2 = rank2;
        this.rank1 = rank1;
    }

    public CronquistRank getRank1() {
        return rank1;
    }

    public CronquistRank getRank2() {
        return rank2;
    }
}
