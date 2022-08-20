import {IClassification} from 'app/entities/classification/classification.model';

export interface IDahlgren {
  id?: number;
  classification?: IClassification | null;
}

export class Dahlgren implements IDahlgren {
  constructor(public id?: number, public classification?: IClassification | null) {}
}

export function getDahlgrenIdentifier(dahlgren: IDahlgren): number | undefined {
  return dahlgren.id;
}
