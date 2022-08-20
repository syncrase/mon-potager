import {IClassification} from 'app/entities/classification/classification.model';

export interface ICandolle {
  id?: number;
  classification?: IClassification | null;
}

export class Candolle implements ICandolle {
  constructor(public id?: number, public classification?: IClassification | null) {}
}

export function getCandolleIdentifier(candolle: ICandolle): number | undefined {
  return candolle.id;
}
