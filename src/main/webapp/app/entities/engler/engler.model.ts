import {IClassification} from 'app/entities/classification/classification.model';

export interface IEngler {
  id?: number;
  classification?: IClassification | null;
}

export class Engler implements IEngler {
  constructor(public id?: number, public classification?: IClassification | null) {}
}

export function getEnglerIdentifier(engler: IEngler): number | undefined {
  return engler.id;
}
