import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IReference, getReferenceIdentifier } from '../reference.model';

export type EntityResponseType = HttpResponse<IReference>;
export type EntityArrayResponseType = HttpResponse<IReference[]>;

@Injectable({ providedIn: 'root' })
export class ReferenceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/references');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(reference: IReference): Observable<EntityResponseType> {
    return this.http.post<IReference>(this.resourceUrl, reference, { observe: 'response' });
  }

  update(reference: IReference): Observable<EntityResponseType> {
    return this.http.put<IReference>(`${this.resourceUrl}/${getReferenceIdentifier(reference) as number}`, reference, {
      observe: 'response',
    });
  }

  partialUpdate(reference: IReference): Observable<EntityResponseType> {
    return this.http.patch<IReference>(`${this.resourceUrl}/${getReferenceIdentifier(reference) as number}`, reference, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IReference>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IReference[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addReferenceToCollectionIfMissing(
    referenceCollection: IReference[],
    ...referencesToCheck: (IReference | null | undefined)[]
  ): IReference[] {
    const references: IReference[] = referencesToCheck.filter(isPresent);
    if (references.length > 0) {
      const referenceCollectionIdentifiers = referenceCollection.map(referenceItem => getReferenceIdentifier(referenceItem)!);
      const referencesToAdd = references.filter(referenceItem => {
        const referenceIdentifier = getReferenceIdentifier(referenceItem);
        if (referenceIdentifier == null || referenceCollectionIdentifiers.includes(referenceIdentifier)) {
          return false;
        }
        referenceCollectionIdentifiers.push(referenceIdentifier);
        return true;
      });
      return [...referencesToAdd, ...referenceCollection];
    }
    return referenceCollection;
  }
}
