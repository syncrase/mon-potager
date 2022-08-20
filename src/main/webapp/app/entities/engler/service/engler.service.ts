import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEngler, getEnglerIdentifier } from '../engler.model';

export type EntityResponseType = HttpResponse<IEngler>;
export type EntityArrayResponseType = HttpResponse<IEngler[]>;

@Injectable({ providedIn: 'root' })
export class EnglerService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/englers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(engler: IEngler): Observable<EntityResponseType> {
    return this.http.post<IEngler>(this.resourceUrl, engler, { observe: 'response' });
  }

  update(engler: IEngler): Observable<EntityResponseType> {
    return this.http.put<IEngler>(`${this.resourceUrl}/${getEnglerIdentifier(engler) as number}`, engler, { observe: 'response' });
  }

  partialUpdate(engler: IEngler): Observable<EntityResponseType> {
    return this.http.patch<IEngler>(`${this.resourceUrl}/${getEnglerIdentifier(engler) as number}`, engler, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEngler>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEngler[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEnglerToCollectionIfMissing(englerCollection: IEngler[], ...englersToCheck: (IEngler | null | undefined)[]): IEngler[] {
    const englers: IEngler[] = englersToCheck.filter(isPresent);
    if (englers.length > 0) {
      const englerCollectionIdentifiers = englerCollection.map(englerItem => getEnglerIdentifier(englerItem)!);
      const englersToAdd = englers.filter(englerItem => {
        const englerIdentifier = getEnglerIdentifier(englerItem);
        if (englerIdentifier == null || englerCollectionIdentifiers.includes(englerIdentifier)) {
          return false;
        }
        englerCollectionIdentifiers.push(englerIdentifier);
        return true;
      });
      return [...englersToAdd, ...englerCollection];
    }
    return englerCollection;
  }
}
