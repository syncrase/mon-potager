import { IReference } from 'app/entities/reference/reference.model';

export interface IUrl {
  id?: number;
  url?: string | null;
  references?: IReference[] | null;
}

export class Url implements IUrl {
  constructor(public id?: number, public url?: string | null, public references?: IReference[] | null) {}
}

export function getUrlIdentifier(url: IUrl): number | undefined {
  return url.id;
}
