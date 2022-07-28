export interface IStrate {
  id?: number;
  type?: string | null;
}

export class Strate implements IStrate {
  constructor(public id?: number, public type?: string | null) {}
}

export function getStrateIdentifier(strate: IStrate): number | undefined {
  return strate.id;
}
