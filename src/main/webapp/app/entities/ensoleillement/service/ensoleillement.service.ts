import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEnsoleillement, getEnsoleillementIdentifier } from '../ensoleillement.model';

export type EntityResponseType = HttpResponse<IEnsoleillement>;
export type EntityArrayResponseType = HttpResponse<IEnsoleillement[]>;

@Injectable({ providedIn: 'root' })
export class EnsoleillementService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ensoleillements');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(ensoleillement: IEnsoleillement): Observable<EntityResponseType> {
    return this.http.post<IEnsoleillement>(this.resourceUrl, ensoleillement, { observe: 'response' });
  }

  update(ensoleillement: IEnsoleillement): Observable<EntityResponseType> {
    return this.http.put<IEnsoleillement>(`${this.resourceUrl}/${getEnsoleillementIdentifier(ensoleillement) as number}`, ensoleillement, {
      observe: 'response',
    });
  }

  partialUpdate(ensoleillement: IEnsoleillement): Observable<EntityResponseType> {
    return this.http.patch<IEnsoleillement>(
      `${this.resourceUrl}/${getEnsoleillementIdentifier(ensoleillement) as number}`,
      ensoleillement,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEnsoleillement>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEnsoleillement[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEnsoleillementToCollectionIfMissing(
    ensoleillementCollection: IEnsoleillement[],
    ...ensoleillementsToCheck: (IEnsoleillement | null | undefined)[]
  ): IEnsoleillement[] {
    const ensoleillements: IEnsoleillement[] = ensoleillementsToCheck.filter(isPresent);
    if (ensoleillements.length > 0) {
      const ensoleillementCollectionIdentifiers = ensoleillementCollection.map(
        ensoleillementItem => getEnsoleillementIdentifier(ensoleillementItem)!
      );
      const ensoleillementsToAdd = ensoleillements.filter(ensoleillementItem => {
        const ensoleillementIdentifier = getEnsoleillementIdentifier(ensoleillementItem);
        if (ensoleillementIdentifier == null || ensoleillementCollectionIdentifiers.includes(ensoleillementIdentifier)) {
          return false;
        }
        ensoleillementCollectionIdentifiers.push(ensoleillementIdentifier);
        return true;
      });
      return [...ensoleillementsToAdd, ...ensoleillementCollection];
    }
    return ensoleillementCollection;
  }
}
