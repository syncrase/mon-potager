import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getAPGIdentifier, IAPG} from '../apg.model';

export type EntityResponseType = HttpResponse<IAPG>;
export type EntityArrayResponseType = HttpResponse<IAPG[]>;

@Injectable({ providedIn: 'root' })
export class APGService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/apgs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(aPG: IAPG): Observable<EntityResponseType> {
    return this.http.post<IAPG>(this.resourceUrl, aPG, { observe: 'response' });
  }

  update(aPG: IAPG): Observable<EntityResponseType> {
    return this.http.put<IAPG>(`${this.resourceUrl}/${getAPGIdentifier(aPG) as number}`, aPG, { observe: 'response' });
  }

  partialUpdate(aPG: IAPG): Observable<EntityResponseType> {
    return this.http.patch<IAPG>(`${this.resourceUrl}/${getAPGIdentifier(aPG) as number}`, aPG, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAPG>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAPG[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAPGToCollectionIfMissing(aPGCollection: IAPG[], ...aPGSToCheck: (IAPG | null | undefined)[]): IAPG[] {
    const aPGS: IAPG[] = aPGSToCheck.filter(isPresent);
    if (aPGS.length > 0) {
      const aPGCollectionIdentifiers = aPGCollection.map(aPGItem => getAPGIdentifier(aPGItem)!);
      const aPGSToAdd = aPGS.filter(aPGItem => {
        const aPGIdentifier = getAPGIdentifier(aPGItem);
        if (aPGIdentifier == null || aPGCollectionIdentifiers.includes(aPGIdentifier)) {
          return false;
        }
        aPGCollectionIdentifiers.push(aPGIdentifier);
        return true;
      });
      return [...aPGSToAdd, ...aPGCollection];
    }
    return aPGCollection;
  }
}
