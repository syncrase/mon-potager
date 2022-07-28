import { IPlante } from 'app/entities/plante/plante.model';

export interface IRessemblance {
  id?: number;
  description?: string | null;
  planteRessemblant?: IPlante | null;
}

export class Ressemblance implements IRessemblance {
  constructor(public id?: number, public description?: string | null, public planteRessemblant?: IPlante | null) {}
}

export function getRessemblanceIdentifier(ressemblance: IRessemblance): number | undefined {
  return ressemblance.id;
}
