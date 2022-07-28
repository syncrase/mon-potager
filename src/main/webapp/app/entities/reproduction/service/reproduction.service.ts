import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IReproduction, getReproductionIdentifier } from '../reproduction.model';

export type EntityResponseType = HttpResponse<IReproduction>;
export type EntityArrayResponseType = HttpResponse<IReproduction[]>;

@Injectable({ providedIn: 'root' })
export class ReproductionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/reproductions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(reproduction: IReproduction): Observable<EntityResponseType> {
    return this.http.post<IReproduction>(this.resourceUrl, reproduction, { observe: 'response' });
  }

  update(reproduction: IReproduction): Observable<EntityResponseType> {
    return this.http.put<IReproduction>(`${this.resourceUrl}/${getReproductionIdentifier(reproduction) as number}`, reproduction, {
      observe: 'response',
    });
  }

  partialUpdate(reproduction: IReproduction): Observable<EntityResponseType> {
    return this.http.patch<IReproduction>(`${this.resourceUrl}/${getReproductionIdentifier(reproduction) as number}`, reproduction, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IReproduction>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IReproduction[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addReproductionToCollectionIfMissing(
    reproductionCollection: IReproduction[],
    ...reproductionsToCheck: (IReproduction | null | undefined)[]
  ): IReproduction[] {
    const reproductions: IReproduction[] = reproductionsToCheck.filter(isPresent);
    if (reproductions.length > 0) {
      const reproductionCollectionIdentifiers = reproductionCollection.map(
        reproductionItem => getReproductionIdentifier(reproductionItem)!
      );
      const reproductionsToAdd = reproductions.filter(reproductionItem => {
        const reproductionIdentifier = getReproductionIdentifier(reproductionItem);
        if (reproductionIdentifier == null || reproductionCollectionIdentifiers.includes(reproductionIdentifier)) {
          return false;
        }
        reproductionCollectionIdentifiers.push(reproductionIdentifier);
        return true;
      });
      return [...reproductionsToAdd, ...reproductionCollection];
    }
    return reproductionCollection;
  }
}
