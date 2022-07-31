import { CronquistTaxonomikRanks } from 'app/entities/enumerations/cronquist-taxonomik-ranks.model';

export interface ICronquistRank {
  id?: number;
  rank?: CronquistTaxonomikRanks;
  nom?: string;
  children?: ICronquistRank[] | null;
  parent?: ICronquistRank | null;
}

export class CronquistRank implements ICronquistRank {
  constructor(
    public id?: number,
    public rank?: CronquistTaxonomikRanks,
    public nom?: string,
    public children?: ICronquistRank[] | null,
    public parent?: ICronquistRank | null
  ) {}
}

export function getCronquistRankIdentifier(cronquistRank: ICronquistRank): number | undefined {
  return cronquistRank.id;
}
