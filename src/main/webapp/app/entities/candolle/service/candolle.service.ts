import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getCandolleIdentifier, ICandolle} from '../candolle.model';

export type EntityResponseType = HttpResponse<ICandolle>;
export type EntityArrayResponseType = HttpResponse<ICandolle[]>;

@Injectable({ providedIn: 'root' })
export class CandolleService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/candolles');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(candolle: ICandolle): Observable<EntityResponseType> {
    return this.http.post<ICandolle>(this.resourceUrl, candolle, { observe: 'response' });
  }

  update(candolle: ICandolle): Observable<EntityResponseType> {
    return this.http.put<ICandolle>(`${this.resourceUrl}/${getCandolleIdentifier(candolle) as number}`, candolle, { observe: 'response' });
  }

  partialUpdate(candolle: ICandolle): Observable<EntityResponseType> {
    return this.http.patch<ICandolle>(`${this.resourceUrl}/${getCandolleIdentifier(candolle) as number}`, candolle, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICandolle>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICandolle[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCandolleToCollectionIfMissing(candolleCollection: ICandolle[], ...candollesToCheck: (ICandolle | null | undefined)[]): ICandolle[] {
    const candolles: ICandolle[] = candollesToCheck.filter(isPresent);
    if (candolles.length > 0) {
      const candolleCollectionIdentifiers = candolleCollection.map(candolleItem => getCandolleIdentifier(candolleItem)!);
      const candollesToAdd = candolles.filter(candolleItem => {
        const candolleIdentifier = getCandolleIdentifier(candolleItem);
        if (candolleIdentifier == null || candolleCollectionIdentifiers.includes(candolleIdentifier)) {
          return false;
        }
        candolleCollectionIdentifiers.push(candolleIdentifier);
        return true;
      });
      return [...candollesToAdd, ...candolleCollection];
    }
    return candolleCollection;
  }
}
