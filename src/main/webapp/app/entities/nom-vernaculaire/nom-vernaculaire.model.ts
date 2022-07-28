import { ICronquistRank } from 'app/entities/cronquist-rank/cronquist-rank.model';
import { IPlante } from 'app/entities/plante/plante.model';

export interface INomVernaculaire {
  id?: number;
  nom?: string | null;
  cronquistRank?: ICronquistRank;
  plantes?: IPlante[] | null;
}

export class NomVernaculaire implements INomVernaculaire {
  constructor(public id?: number, public nom?: string | null, public cronquistRank?: ICronquistRank, public plantes?: IPlante[] | null) {}
}

export function getNomVernaculaireIdentifier(nomVernaculaire: INomVernaculaire): number | undefined {
  return nomVernaculaire.id;
}
