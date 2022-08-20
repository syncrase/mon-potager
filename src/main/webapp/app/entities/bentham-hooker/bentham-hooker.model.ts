import {IClassification} from 'app/entities/classification/classification.model';

export interface IBenthamHooker {
  id?: number;
  classification?: IClassification | null;
}

export class BenthamHooker implements IBenthamHooker {
  constructor(public id?: number, public classification?: IClassification | null) {}
}

export function getBenthamHookerIdentifier(benthamHooker: IBenthamHooker): number | undefined {
  return benthamHooker.id;
}
