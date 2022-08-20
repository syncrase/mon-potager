import { IClassification } from 'app/entities/classification/classification.model';
import { INomVernaculaire } from 'app/entities/nom-vernaculaire/nom-vernaculaire.model';
import { IReference } from 'app/entities/reference/reference.model';

export interface IPlante {
  id?: number;
  classification?: IClassification | null;
  nomsVernaculaires?: INomVernaculaire[] | null;
  references?: IReference[] | null;
}

export class Plante implements IPlante {
  constructor(
    public id?: number,
    public classification?: IClassification | null,
    public nomsVernaculaires?: INomVernaculaire[] | null,
    public references?: IReference[] | null
  ) {}
}

export function getPlanteIdentifier(plante: IPlante): number | undefined {
  return plante.id;
}
