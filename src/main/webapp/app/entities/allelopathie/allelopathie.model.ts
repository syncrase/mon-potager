import { IPlante } from 'app/entities/plante/plante.model';

export interface IAllelopathie {
  id?: number;
  type?: string;
  description?: string | null;
  impact?: number | null;
  cible?: IPlante;
  origine?: IPlante;
}

export class Allelopathie implements IAllelopathie {
  constructor(
    public id?: number,
    public type?: string,
    public description?: string | null,
    public impact?: number | null,
    public cible?: IPlante,
    public origine?: IPlante
  ) {}
}

export function getAllelopathieIdentifier(allelopathie: IAllelopathie): number | undefined {
  return allelopathie.id;
}
