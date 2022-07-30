import { IPlante } from 'app/entities/plante/plante.model';
import { CronquistTaxonomikRanks } from 'app/entities/enumerations/cronquist-taxonomik-ranks.model';

export interface ICronquistRank {
  id?: number;
  rank?: CronquistTaxonomikRanks;
  children?: ICronquistRank[] | null;
  parent?: ICronquistRank | null;
  plante?: IPlante | null;
}

export class CronquistRank implements ICronquistRank {
  constructor(
    public id?: number,
    public rank?: CronquistTaxonomikRanks,
    public children?: ICronquistRank[] | null,
    public parent?: ICronquistRank | null,
    public plante?: IPlante | null
  ) {}
}

export function getCronquistRankIdentifier(cronquistRank: ICronquistRank): number | undefined {
  return cronquistRank.id;
}
