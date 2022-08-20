import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IThorne, getThorneIdentifier } from '../thorne.model';

export type EntityResponseType = HttpResponse<IThorne>;
export type EntityArrayResponseType = HttpResponse<IThorne[]>;

@Injectable({ providedIn: 'root' })
export class ThorneService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/thornes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(thorne: IThorne): Observable<EntityResponseType> {
    return this.http.post<IThorne>(this.resourceUrl, thorne, { observe: 'response' });
  }

  update(thorne: IThorne): Observable<EntityResponseType> {
    return this.http.put<IThorne>(`${this.resourceUrl}/${getThorneIdentifier(thorne) as number}`, thorne, { observe: 'response' });
  }

  partialUpdate(thorne: IThorne): Observable<EntityResponseType> {
    return this.http.patch<IThorne>(`${this.resourceUrl}/${getThorneIdentifier(thorne) as number}`, thorne, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IThorne>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IThorne[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addThorneToCollectionIfMissing(thorneCollection: IThorne[], ...thornesToCheck: (IThorne | null | undefined)[]): IThorne[] {
    const thornes: IThorne[] = thornesToCheck.filter(isPresent);
    if (thornes.length > 0) {
      const thorneCollectionIdentifiers = thorneCollection.map(thorneItem => getThorneIdentifier(thorneItem)!);
      const thornesToAdd = thornes.filter(thorneItem => {
        const thorneIdentifier = getThorneIdentifier(thorneItem);
        if (thorneIdentifier == null || thorneCollectionIdentifiers.includes(thorneIdentifier)) {
          return false;
        }
        thorneCollectionIdentifiers.push(thorneIdentifier);
        return true;
      });
      return [...thornesToAdd, ...thorneCollection];
    }
    return thorneCollection;
  }
}
