import { ICronquistRank } from 'app/entities/cronquist-rank/cronquist-rank.model';
import { INomVernaculaire } from 'app/entities/nom-vernaculaire/nom-vernaculaire.model';

export interface IPlante {
  id?: number;
  lowestClassificationRanks?: ICronquistRank[] | null;
  nomsVernaculaires?: INomVernaculaire[] | null;
}

export class Plante implements IPlante {
  constructor(
    public id?: number,
    public lowestClassificationRanks?: ICronquistRank[] | null,
    public nomsVernaculaires?: INomVernaculaire[] | null
  ) {}
}

export function getPlanteIdentifier(plante: IPlante): number | undefined {
  return plante.id;
}
