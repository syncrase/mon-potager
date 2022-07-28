import { IMois } from 'app/entities/mois/mois.model';

export interface IPeriodeAnnee {
  id?: number;
  debut?: IMois;
  fin?: IMois;
}

export class PeriodeAnnee implements IPeriodeAnnee {
  constructor(public id?: number, public debut?: IMois, public fin?: IMois) {}
}

export function getPeriodeAnneeIdentifier(periodeAnnee: IPeriodeAnnee): number | undefined {
  return periodeAnnee.id;
}
