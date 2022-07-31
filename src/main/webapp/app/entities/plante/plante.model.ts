import { ICronquistRank } from 'app/entities/cronquist-rank/cronquist-rank.model';
import { INomVernaculaire } from 'app/entities/nom-vernaculaire/nom-vernaculaire.model';

export interface IPlante {
  id?: number;
  cronquistRank?: ICronquistRank | null;
  nomsVernaculaires?: INomVernaculaire[] | null;
}

export class Plante implements IPlante {
  constructor(public id?: number, public cronquistRank?: ICronquistRank | null, public nomsVernaculaires?: INomVernaculaire[] | null) {}
}

export function getPlanteIdentifier(plante: IPlante): number | undefined {
  return plante.id;
}
