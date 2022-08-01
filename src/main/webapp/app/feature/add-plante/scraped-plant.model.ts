import {ICronquistRank} from "../../entities/cronquist-rank/cronquist-rank.model";
import {INomVernaculaire} from "../../entities/nom-vernaculaire/nom-vernaculaire.model";

export interface IScrapedPlante {
  id?: number;
  lowestClassificationRanks?: ICronquistRank[] | null;
  nomsVernaculaires?: INomVernaculaire[] | null;
}

export class ScrapedPlante implements IScrapedPlante {
  constructor(
    public id?: number,
    public lowestClassificationRanks?: ICronquistRank[] | null,
    public nomsVernaculaires?: INomVernaculaire[] | null
  ) {
  }
}
