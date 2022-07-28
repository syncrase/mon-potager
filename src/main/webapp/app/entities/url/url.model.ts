import { ICronquistRank } from 'app/entities/cronquist-rank/cronquist-rank.model';

export interface IUrl {
  id?: number;
  url?: string;
  cronquistRank?: ICronquistRank | null;
}

export class Url implements IUrl {
  constructor(public id?: number, public url?: string, public cronquistRank?: ICronquistRank | null) {}
}

export function getUrlIdentifier(url: IUrl): number | undefined {
  return url.id;
}
