import { IClassification } from 'app/entities/classification/classification.model';

export interface IWettstein {
  id?: number;
  classification?: IClassification | null;
}

export class Wettstein implements IWettstein {
  constructor(public id?: number, public classification?: IClassification | null) {}
}

export function getWettsteinIdentifier(wettstein: IWettstein): number | undefined {
  return wettstein.id;
}
