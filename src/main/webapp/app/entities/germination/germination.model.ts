export interface IGermination {
  id?: number;
  tempsDeGermination?: string | null;
  conditionDeGermination?: string | null;
}

export class Germination implements IGermination {
  constructor(public id?: number, public tempsDeGermination?: string | null, public conditionDeGermination?: string | null) {}
}

export function getGerminationIdentifier(germination: IGermination): number | undefined {
  return germination.id;
}
