import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITemperature, getTemperatureIdentifier } from '../temperature.model';

export type EntityResponseType = HttpResponse<ITemperature>;
export type EntityArrayResponseType = HttpResponse<ITemperature[]>;

@Injectable({ providedIn: 'root' })
export class TemperatureService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/temperatures');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(temperature: ITemperature): Observable<EntityResponseType> {
    return this.http.post<ITemperature>(this.resourceUrl, temperature, { observe: 'response' });
  }

  update(temperature: ITemperature): Observable<EntityResponseType> {
    return this.http.put<ITemperature>(`${this.resourceUrl}/${getTemperatureIdentifier(temperature) as number}`, temperature, {
      observe: 'response',
    });
  }

  partialUpdate(temperature: ITemperature): Observable<EntityResponseType> {
    return this.http.patch<ITemperature>(`${this.resourceUrl}/${getTemperatureIdentifier(temperature) as number}`, temperature, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITemperature>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITemperature[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTemperatureToCollectionIfMissing(
    temperatureCollection: ITemperature[],
    ...temperaturesToCheck: (ITemperature | null | undefined)[]
  ): ITemperature[] {
    const temperatures: ITemperature[] = temperaturesToCheck.filter(isPresent);
    if (temperatures.length > 0) {
      const temperatureCollectionIdentifiers = temperatureCollection.map(temperatureItem => getTemperatureIdentifier(temperatureItem)!);
      const temperaturesToAdd = temperatures.filter(temperatureItem => {
        const temperatureIdentifier = getTemperatureIdentifier(temperatureItem);
        if (temperatureIdentifier == null || temperatureCollectionIdentifiers.includes(temperatureIdentifier)) {
          return false;
        }
        temperatureCollectionIdentifiers.push(temperatureIdentifier);
        return true;
      });
      return [...temperaturesToAdd, ...temperatureCollection];
    }
    return temperatureCollection;
  }
}
