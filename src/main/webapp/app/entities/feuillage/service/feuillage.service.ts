import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFeuillage, getFeuillageIdentifier } from '../feuillage.model';

export type EntityResponseType = HttpResponse<IFeuillage>;
export type EntityArrayResponseType = HttpResponse<IFeuillage[]>;

@Injectable({ providedIn: 'root' })
export class FeuillageService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/feuillages');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(feuillage: IFeuillage): Observable<EntityResponseType> {
    return this.http.post<IFeuillage>(this.resourceUrl, feuillage, { observe: 'response' });
  }

  update(feuillage: IFeuillage): Observable<EntityResponseType> {
    return this.http.put<IFeuillage>(`${this.resourceUrl}/${getFeuillageIdentifier(feuillage) as number}`, feuillage, {
      observe: 'response',
    });
  }

  partialUpdate(feuillage: IFeuillage): Observable<EntityResponseType> {
    return this.http.patch<IFeuillage>(`${this.resourceUrl}/${getFeuillageIdentifier(feuillage) as number}`, feuillage, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFeuillage>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFeuillage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addFeuillageToCollectionIfMissing(
    feuillageCollection: IFeuillage[],
    ...feuillagesToCheck: (IFeuillage | null | undefined)[]
  ): IFeuillage[] {
    const feuillages: IFeuillage[] = feuillagesToCheck.filter(isPresent);
    if (feuillages.length > 0) {
      const feuillageCollectionIdentifiers = feuillageCollection.map(feuillageItem => getFeuillageIdentifier(feuillageItem)!);
      const feuillagesToAdd = feuillages.filter(feuillageItem => {
        const feuillageIdentifier = getFeuillageIdentifier(feuillageItem);
        if (feuillageIdentifier == null || feuillageCollectionIdentifiers.includes(feuillageIdentifier)) {
          return false;
        }
        feuillageCollectionIdentifiers.push(feuillageIdentifier);
        return true;
      });
      return [...feuillagesToAdd, ...feuillageCollection];
    }
    return feuillageCollection;
  }
}
