import { IClassification } from 'app/entities/classification/classification.model';

export interface IThorne {
  id?: number;
  classification?: IClassification | null;
}

export class Thorne implements IThorne {
  constructor(public id?: number, public classification?: IClassification | null) {}
}

export function getThorneIdentifier(thorne: IThorne): number | undefined {
  return thorne.id;
}
