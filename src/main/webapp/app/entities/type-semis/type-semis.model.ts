export interface ITypeSemis {
  id?: number;
  type?: string;
  description?: string | null;
}

export class TypeSemis implements ITypeSemis {
  constructor(public id?: number, public type?: string, public description?: string | null) {}
}

export function getTypeSemisIdentifier(typeSemis: ITypeSemis): number | undefined {
  return typeSemis.id;
}
