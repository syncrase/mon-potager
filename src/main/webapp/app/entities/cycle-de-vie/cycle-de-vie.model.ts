import { ISemis } from 'app/entities/semis/semis.model';
import { IPeriodeAnnee } from 'app/entities/periode-annee/periode-annee.model';
import { IReproduction } from 'app/entities/reproduction/reproduction.model';

export interface ICycleDeVie {
  id?: number;
  semis?: ISemis | null;
  apparitionFeuilles?: IPeriodeAnnee | null;
  floraison?: IPeriodeAnnee | null;
  recolte?: IPeriodeAnnee | null;
  croissance?: IPeriodeAnnee | null;
  maturite?: IPeriodeAnnee | null;
  plantation?: IPeriodeAnnee | null;
  rempotage?: IPeriodeAnnee | null;
  reproduction?: IReproduction | null;
}

export class CycleDeVie implements ICycleDeVie {
  constructor(
    public id?: number,
    public semis?: ISemis | null,
    public apparitionFeuilles?: IPeriodeAnnee | null,
    public floraison?: IPeriodeAnnee | null,
    public recolte?: IPeriodeAnnee | null,
    public croissance?: IPeriodeAnnee | null,
    public maturite?: IPeriodeAnnee | null,
    public plantation?: IPeriodeAnnee | null,
    public rempotage?: IPeriodeAnnee | null,
    public reproduction?: IReproduction | null
  ) {}
}

export function getCycleDeVieIdentifier(cycleDeVie: ICycleDeVie): number | undefined {
  return cycleDeVie.id;
}
