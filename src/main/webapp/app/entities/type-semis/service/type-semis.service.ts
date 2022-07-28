import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITypeSemis, getTypeSemisIdentifier } from '../type-semis.model';

export type EntityResponseType = HttpResponse<ITypeSemis>;
export type EntityArrayResponseType = HttpResponse<ITypeSemis[]>;

@Injectable({ providedIn: 'root' })
export class TypeSemisService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/type-semis');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(typeSemis: ITypeSemis): Observable<EntityResponseType> {
    return this.http.post<ITypeSemis>(this.resourceUrl, typeSemis, { observe: 'response' });
  }

  update(typeSemis: ITypeSemis): Observable<EntityResponseType> {
    return this.http.put<ITypeSemis>(`${this.resourceUrl}/${getTypeSemisIdentifier(typeSemis) as number}`, typeSemis, {
      observe: 'response',
    });
  }

  partialUpdate(typeSemis: ITypeSemis): Observable<EntityResponseType> {
    return this.http.patch<ITypeSemis>(`${this.resourceUrl}/${getTypeSemisIdentifier(typeSemis) as number}`, typeSemis, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITypeSemis>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITypeSemis[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTypeSemisToCollectionIfMissing(
    typeSemisCollection: ITypeSemis[],
    ...typeSemisToCheck: (ITypeSemis | null | undefined)[]
  ): ITypeSemis[] {
    const typeSemis: ITypeSemis[] = typeSemisToCheck.filter(isPresent);
    if (typeSemis.length > 0) {
      const typeSemisCollectionIdentifiers = typeSemisCollection.map(typeSemisItem => getTypeSemisIdentifier(typeSemisItem)!);
      const typeSemisToAdd = typeSemis.filter(typeSemisItem => {
        const typeSemisIdentifier = getTypeSemisIdentifier(typeSemisItem);
        if (typeSemisIdentifier == null || typeSemisCollectionIdentifiers.includes(typeSemisIdentifier)) {
          return false;
        }
        typeSemisCollectionIdentifiers.push(typeSemisIdentifier);
        return true;
      });
      return [...typeSemisToAdd, ...typeSemisCollection];
    }
    return typeSemisCollection;
  }
}
