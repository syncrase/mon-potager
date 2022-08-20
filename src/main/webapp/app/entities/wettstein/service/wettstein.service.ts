import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IWettstein, getWettsteinIdentifier } from '../wettstein.model';

export type EntityResponseType = HttpResponse<IWettstein>;
export type EntityArrayResponseType = HttpResponse<IWettstein[]>;

@Injectable({ providedIn: 'root' })
export class WettsteinService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/wettsteins');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(wettstein: IWettstein): Observable<EntityResponseType> {
    return this.http.post<IWettstein>(this.resourceUrl, wettstein, { observe: 'response' });
  }

  update(wettstein: IWettstein): Observable<EntityResponseType> {
    return this.http.put<IWettstein>(`${this.resourceUrl}/${getWettsteinIdentifier(wettstein) as number}`, wettstein, {
      observe: 'response',
    });
  }

  partialUpdate(wettstein: IWettstein): Observable<EntityResponseType> {
    return this.http.patch<IWettstein>(`${this.resourceUrl}/${getWettsteinIdentifier(wettstein) as number}`, wettstein, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IWettstein>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IWettstein[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addWettsteinToCollectionIfMissing(
    wettsteinCollection: IWettstein[],
    ...wettsteinsToCheck: (IWettstein | null | undefined)[]
  ): IWettstein[] {
    const wettsteins: IWettstein[] = wettsteinsToCheck.filter(isPresent);
    if (wettsteins.length > 0) {
      const wettsteinCollectionIdentifiers = wettsteinCollection.map(wettsteinItem => getWettsteinIdentifier(wettsteinItem)!);
      const wettsteinsToAdd = wettsteins.filter(wettsteinItem => {
        const wettsteinIdentifier = getWettsteinIdentifier(wettsteinItem);
        if (wettsteinIdentifier == null || wettsteinCollectionIdentifiers.includes(wettsteinIdentifier)) {
          return false;
        }
        wettsteinCollectionIdentifiers.push(wettsteinIdentifier);
        return true;
      });
      return [...wettsteinsToAdd, ...wettsteinCollection];
    }
    return wettsteinCollection;
  }
}
