import {ICronquistRank} from "../../entities/cronquist-rank/cronquist-rank.model";
import {Plante} from "../../entities/plante/plante.model";

export interface IScrapedPlante {
  cronquistClassificationBranch?: ICronquistRank[] | null;
  plante?: Plante | null;
}

export class ScrapedPlante implements IScrapedPlante {
  constructor(
    public plante?: Plante | null,
    public cronquistClassificationBranch?: ICronquistRank[] | null,
  ) {
  }
}
