import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMois, getMoisIdentifier } from '../mois.model';

export type EntityResponseType = HttpResponse<IMois>;
export type EntityArrayResponseType = HttpResponse<IMois[]>;

@Injectable({ providedIn: 'root' })
export class MoisService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/mois');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(mois: IMois): Observable<EntityResponseType> {
    return this.http.post<IMois>(this.resourceUrl, mois, { observe: 'response' });
  }

  update(mois: IMois): Observable<EntityResponseType> {
    return this.http.put<IMois>(`${this.resourceUrl}/${getMoisIdentifier(mois) as number}`, mois, { observe: 'response' });
  }

  partialUpdate(mois: IMois): Observable<EntityResponseType> {
    return this.http.patch<IMois>(`${this.resourceUrl}/${getMoisIdentifier(mois) as number}`, mois, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMois>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMois[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addMoisToCollectionIfMissing(moisCollection: IMois[], ...moisToCheck: (IMois | null | undefined)[]): IMois[] {
    const mois: IMois[] = moisToCheck.filter(isPresent);
    if (mois.length > 0) {
      const moisCollectionIdentifiers = moisCollection.map(moisItem => getMoisIdentifier(moisItem)!);
      const moisToAdd = mois.filter(moisItem => {
        const moisIdentifier = getMoisIdentifier(moisItem);
        if (moisIdentifier == null || moisCollectionIdentifiers.includes(moisIdentifier)) {
          return false;
        }
        moisCollectionIdentifiers.push(moisIdentifier);
        return true;
      });
      return [...moisToAdd, ...moisCollection];
    }
    return moisCollection;
  }
}
