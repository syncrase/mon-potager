export interface IMois {
  id?: number;
  numero?: number;
  nom?: string;
}

export class Mois implements IMois {
  constructor(public id?: number, public numero?: number, public nom?: string) {}
}

export function getMoisIdentifier(mois: IMois): number | undefined {
  return mois.id;
}
