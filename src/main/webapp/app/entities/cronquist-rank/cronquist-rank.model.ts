import { CronquistTaxonomicRank } from 'app/entities/enumerations/cronquist-taxonomic-rank.model';

export interface ICronquistRank {
  id?: number;
  rank?: CronquistTaxonomicRank;
  nom?: string | null;
  children?: ICronquistRank[] | null;
  parent?: ICronquistRank | null;
}

export class CronquistRank implements ICronquistRank {
  constructor(
    public id?: number,
    public rank?: CronquistTaxonomicRank,
    public nom?: string | null,
    public children?: ICronquistRank[] | null,
    public parent?: ICronquistRank | null
  ) {}
}

export function getCronquistRankIdentifier(cronquistRank: ICronquistRank): number | undefined {
  return cronquistRank.id;
}
