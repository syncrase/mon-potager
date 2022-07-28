export interface IRacine {
  id?: number;
  type?: string | null;
}

export class Racine implements IRacine {
  constructor(public id?: number, public type?: string | null) {}
}

export function getRacineIdentifier(racine: IRacine): number | undefined {
  return racine.id;
}
