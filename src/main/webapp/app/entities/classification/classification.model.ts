import {ICronquistRank} from 'app/entities/cronquist-rank/cronquist-rank.model';
import {IAPG} from 'app/entities/apg/apg.model';
import {IBenthamHooker} from 'app/entities/bentham-hooker/bentham-hooker.model';
import {IWettstein} from 'app/entities/wettstein/wettstein.model';
import {IThorne} from 'app/entities/thorne/thorne.model';
import {ITakhtajan} from 'app/entities/takhtajan/takhtajan.model';
import {IEngler} from 'app/entities/engler/engler.model';
import {ICandolle} from 'app/entities/candolle/candolle.model';
import {IDahlgren} from 'app/entities/dahlgren/dahlgren.model';
import {IPlante} from 'app/entities/plante/plante.model';

export interface IClassification {
  id?: number;
  cronquist?: ICronquistRank | null;
  apg?: IAPG | null;
  benthamHooker?: IBenthamHooker | null;
  wettstein?: IWettstein | null;
  thorne?: IThorne | null;
  takhtajan?: ITakhtajan | null;
  engler?: IEngler | null;
  candolle?: ICandolle | null;
  dahlgren?: IDahlgren | null;
  plantes?: IPlante[] | null;
}

export class Classification implements IClassification {
  constructor(
    public id?: number,
    public cronquist?: ICronquistRank | null,
    public apg?: IAPG | null,
    public benthamHooker?: IBenthamHooker | null,
    public wettstein?: IWettstein | null,
    public thorne?: IThorne | null,
    public takhtajan?: ITakhtajan | null,
    public engler?: IEngler | null,
    public candolle?: ICandolle | null,
    public dahlgren?: IDahlgren | null,
    public plantes?: IPlante[] | null
  ) {}
}

export function getClassificationIdentifier(classification: IClassification): number | undefined {
  return classification.id;
}
