export interface ITemperature {
  id?: number;
  min?: number | null;
  max?: number | null;
  description?: string | null;
  rusticite?: string | null;
}

export class Temperature implements ITemperature {
  constructor(
    public id?: number,
    public min?: number | null,
    public max?: number | null,
    public description?: string | null,
    public rusticite?: string | null
  ) {}
}

export function getTemperatureIdentifier(temperature: ITemperature): number | undefined {
  return temperature.id;
}
