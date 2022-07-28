import { IUrl } from 'app/entities/url/url.model';
import { INomVernaculaire } from 'app/entities/nom-vernaculaire/nom-vernaculaire.model';
import { CronquistTaxonomikRanks } from 'app/entities/enumerations/cronquist-taxonomik-ranks.model';

export interface ICronquistRank {
  id?: number;
  rank?: CronquistTaxonomikRanks;
  children?: ICronquistRank[] | null;
  urls?: IUrl[] | null;
  noms?: INomVernaculaire[];
  parent?: ICronquistRank | null;
}

export class CronquistRank implements ICronquistRank {
  constructor(
    public id?: number,
    public rank?: CronquistTaxonomikRanks,
    public children?: ICronquistRank[] | null,
    public urls?: IUrl[] | null,
    public noms?: INomVernaculaire[],
    public parent?: ICronquistRank | null
  ) {}
}

export function getCronquistRankIdentifier(cronquistRank: ICronquistRank): number | undefined {
  return cronquistRank.id;
}
