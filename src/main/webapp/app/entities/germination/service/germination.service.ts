import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGermination, getGerminationIdentifier } from '../germination.model';

export type EntityResponseType = HttpResponse<IGermination>;
export type EntityArrayResponseType = HttpResponse<IGermination[]>;

@Injectable({ providedIn: 'root' })
export class GerminationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/germinations');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(germination: IGermination): Observable<EntityResponseType> {
    return this.http.post<IGermination>(this.resourceUrl, germination, { observe: 'response' });
  }

  update(germination: IGermination): Observable<EntityResponseType> {
    return this.http.put<IGermination>(`${this.resourceUrl}/${getGerminationIdentifier(germination) as number}`, germination, {
      observe: 'response',
    });
  }

  partialUpdate(germination: IGermination): Observable<EntityResponseType> {
    return this.http.patch<IGermination>(`${this.resourceUrl}/${getGerminationIdentifier(germination) as number}`, germination, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGermination>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGermination[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addGerminationToCollectionIfMissing(
    germinationCollection: IGermination[],
    ...germinationsToCheck: (IGermination | null | undefined)[]
  ): IGermination[] {
    const germinations: IGermination[] = germinationsToCheck.filter(isPresent);
    if (germinations.length > 0) {
      const germinationCollectionIdentifiers = germinationCollection.map(germinationItem => getGerminationIdentifier(germinationItem)!);
      const germinationsToAdd = germinations.filter(germinationItem => {
        const germinationIdentifier = getGerminationIdentifier(germinationItem);
        if (germinationIdentifier == null || germinationCollectionIdentifiers.includes(germinationIdentifier)) {
          return false;
        }
        germinationCollectionIdentifiers.push(germinationIdentifier);
        return true;
      });
      return [...germinationsToAdd, ...germinationCollection];
    }
    return germinationCollection;
  }
}
