import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRessemblance, getRessemblanceIdentifier } from '../ressemblance.model';

export type EntityResponseType = HttpResponse<IRessemblance>;
export type EntityArrayResponseType = HttpResponse<IRessemblance[]>;

@Injectable({ providedIn: 'root' })
export class RessemblanceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ressemblances');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(ressemblance: IRessemblance): Observable<EntityResponseType> {
    return this.http.post<IRessemblance>(this.resourceUrl, ressemblance, { observe: 'response' });
  }

  update(ressemblance: IRessemblance): Observable<EntityResponseType> {
    return this.http.put<IRessemblance>(`${this.resourceUrl}/${getRessemblanceIdentifier(ressemblance) as number}`, ressemblance, {
      observe: 'response',
    });
  }

  partialUpdate(ressemblance: IRessemblance): Observable<EntityResponseType> {
    return this.http.patch<IRessemblance>(`${this.resourceUrl}/${getRessemblanceIdentifier(ressemblance) as number}`, ressemblance, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRessemblance>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRessemblance[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addRessemblanceToCollectionIfMissing(
    ressemblanceCollection: IRessemblance[],
    ...ressemblancesToCheck: (IRessemblance | null | undefined)[]
  ): IRessemblance[] {
    const ressemblances: IRessemblance[] = ressemblancesToCheck.filter(isPresent);
    if (ressemblances.length > 0) {
      const ressemblanceCollectionIdentifiers = ressemblanceCollection.map(
        ressemblanceItem => getRessemblanceIdentifier(ressemblanceItem)!
      );
      const ressemblancesToAdd = ressemblances.filter(ressemblanceItem => {
        const ressemblanceIdentifier = getRessemblanceIdentifier(ressemblanceItem);
        if (ressemblanceIdentifier == null || ressemblanceCollectionIdentifiers.includes(ressemblanceIdentifier)) {
          return false;
        }
        ressemblanceCollectionIdentifiers.push(ressemblanceIdentifier);
        return true;
      });
      return [...ressemblancesToAdd, ...ressemblanceCollection];
    }
    return ressemblanceCollection;
  }
}
