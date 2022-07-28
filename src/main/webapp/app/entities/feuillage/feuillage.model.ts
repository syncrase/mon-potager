export interface IFeuillage {
  id?: number;
  type?: string | null;
}

export class Feuillage implements IFeuillage {
  constructor(public id?: number, public type?: string | null) {}
}

export function getFeuillageIdentifier(feuillage: IFeuillage): number | undefined {
  return feuillage.id;
}
