import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISol, getSolIdentifier } from '../sol.model';

export type EntityResponseType = HttpResponse<ISol>;
export type EntityArrayResponseType = HttpResponse<ISol[]>;

@Injectable({ providedIn: 'root' })
export class SolService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sols');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(sol: ISol): Observable<EntityResponseType> {
    return this.http.post<ISol>(this.resourceUrl, sol, { observe: 'response' });
  }

  update(sol: ISol): Observable<EntityResponseType> {
    return this.http.put<ISol>(`${this.resourceUrl}/${getSolIdentifier(sol) as number}`, sol, { observe: 'response' });
  }

  partialUpdate(sol: ISol): Observable<EntityResponseType> {
    return this.http.patch<ISol>(`${this.resourceUrl}/${getSolIdentifier(sol) as number}`, sol, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISol>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISol[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSolToCollectionIfMissing(solCollection: ISol[], ...solsToCheck: (ISol | null | undefined)[]): ISol[] {
    const sols: ISol[] = solsToCheck.filter(isPresent);
    if (sols.length > 0) {
      const solCollectionIdentifiers = solCollection.map(solItem => getSolIdentifier(solItem)!);
      const solsToAdd = sols.filter(solItem => {
        const solIdentifier = getSolIdentifier(solItem);
        if (solIdentifier == null || solCollectionIdentifiers.includes(solIdentifier)) {
          return false;
        }
        solCollectionIdentifiers.push(solIdentifier);
        return true;
      });
      return [...solsToAdd, ...solCollection];
    }
    return solCollection;
  }
}
