import {IClassification} from 'app/entities/classification/classification.model';

export interface IAPG {
  id?: number;
  classification?: IClassification | null;
}

export class APG implements IAPG {
  constructor(public id?: number, public classification?: IClassification | null) {}
}

export function getAPGIdentifier(aPG: IAPG): number | undefined {
  return aPG.id;
}
