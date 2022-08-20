import { IUrl } from 'app/entities/url/url.model';
import { IPlante } from 'app/entities/plante/plante.model';
import { ReferenceType } from 'app/entities/enumerations/reference-type.model';

export interface IReference {
  id?: number;
  description?: string | null;
  type?: ReferenceType;
  url?: IUrl | null;
  plantes?: IPlante[] | null;
}

export class Reference implements IReference {
  constructor(
    public id?: number,
    public description?: string | null,
    public type?: ReferenceType,
    public url?: IUrl | null,
    public plantes?: IPlante[] | null
  ) {}
}

export function getReferenceIdentifier(reference: IReference): number | undefined {
  return reference.id;
}
