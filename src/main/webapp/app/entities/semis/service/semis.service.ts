import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISemis, getSemisIdentifier } from '../semis.model';

export type EntityResponseType = HttpResponse<ISemis>;
export type EntityArrayResponseType = HttpResponse<ISemis[]>;

@Injectable({ providedIn: 'root' })
export class SemisService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/semis');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(semis: ISemis): Observable<EntityResponseType> {
    return this.http.post<ISemis>(this.resourceUrl, semis, { observe: 'response' });
  }

  update(semis: ISemis): Observable<EntityResponseType> {
    return this.http.put<ISemis>(`${this.resourceUrl}/${getSemisIdentifier(semis) as number}`, semis, { observe: 'response' });
  }

  partialUpdate(semis: ISemis): Observable<EntityResponseType> {
    return this.http.patch<ISemis>(`${this.resourceUrl}/${getSemisIdentifier(semis) as number}`, semis, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISemis>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISemis[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSemisToCollectionIfMissing(semisCollection: ISemis[], ...semisToCheck: (ISemis | null | undefined)[]): ISemis[] {
    const semis: ISemis[] = semisToCheck.filter(isPresent);
    if (semis.length > 0) {
      const semisCollectionIdentifiers = semisCollection.map(semisItem => getSemisIdentifier(semisItem)!);
      const semisToAdd = semis.filter(semisItem => {
        const semisIdentifier = getSemisIdentifier(semisItem);
        if (semisIdentifier == null || semisCollectionIdentifiers.includes(semisIdentifier)) {
          return false;
        }
        semisCollectionIdentifiers.push(semisIdentifier);
        return true;
      });
      return [...semisToAdd, ...semisCollection];
    }
    return semisCollection;
  }
}
