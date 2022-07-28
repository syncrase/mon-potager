import { IPlante } from 'app/entities/plante/plante.model';

export interface IEnsoleillement {
  id?: number;
  orientation?: string | null;
  ensoleilement?: number | null;
  plante?: IPlante | null;
}

export class Ensoleillement implements IEnsoleillement {
  constructor(
    public id?: number,
    public orientation?: string | null,
    public ensoleilement?: number | null,
    public plante?: IPlante | null
  ) {}
}

export function getEnsoleillementIdentifier(ensoleillement: IEnsoleillement): number | undefined {
  return ensoleillement.id;
}
