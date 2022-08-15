import {ICronquistRank} from "../../entities/cronquist-rank/cronquist-rank.model";


export interface ICronquistClassificationBranch {
    classificationCronquist?: ICronquistRank[] | null;
}

export class CronquistClassificationBranch implements ICronquistClassificationBranch {
    constructor(
        public classificationCronquist?: ICronquistRank[] | null,
    ) {
    }
}
