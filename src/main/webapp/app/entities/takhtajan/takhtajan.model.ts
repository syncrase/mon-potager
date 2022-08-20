import { IClassification } from 'app/entities/classification/classification.model';

export interface ITakhtajan {
  id?: number;
  classification?: IClassification | null;
}

export class Takhtajan implements ITakhtajan {
  constructor(public id?: number, public classification?: IClassification | null) {}
}

export function getTakhtajanIdentifier(takhtajan: ITakhtajan): number | undefined {
  return takhtajan.id;
}
