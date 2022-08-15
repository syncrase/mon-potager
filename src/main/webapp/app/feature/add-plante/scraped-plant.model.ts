import {ICronquistRank} from "../../entities/cronquist-rank/cronquist-rank.model";
import {INomVernaculaire} from "../../entities/nom-vernaculaire/nom-vernaculaire.model";

export interface IScrapedPlante {
  id?: number;
  /*
  * Ce n'est pas comme ça dans l'objet en Java
   */
  cronquistClassificationBranch?: ICronquistRank[] | null;
  nomsVernaculaires?: INomVernaculaire[] | null;
}

export class ScrapedPlante implements IScrapedPlante {
  constructor(
      public id?: number,
      public cronquistClassificationBranch?: ICronquistRank[] | null,
      public nomsVernaculaires?: INomVernaculaire[] | null
  ) {
  }
}
