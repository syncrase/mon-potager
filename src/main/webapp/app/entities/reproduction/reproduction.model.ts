import { ICycleDeVie } from 'app/entities/cycle-de-vie/cycle-de-vie.model';

export interface IReproduction {
  id?: number;
  vitesse?: string | null;
  type?: string | null;
  cycleDeVies?: ICycleDeVie[] | null;
}

export class Reproduction implements IReproduction {
  constructor(public id?: number, public vitesse?: string | null, public type?: string | null, public cycleDeVies?: ICycleDeVie[] | null) {}
}

export function getReproductionIdentifier(reproduction: IReproduction): number | undefined {
  return reproduction.id;
}
