import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getDahlgrenIdentifier, IDahlgren} from '../dahlgren.model';

export type EntityResponseType = HttpResponse<IDahlgren>;
export type EntityArrayResponseType = HttpResponse<IDahlgren[]>;

@Injectable({ providedIn: 'root' })
export class DahlgrenService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/dahlgrens');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(dahlgren: IDahlgren): Observable<EntityResponseType> {
    return this.http.post<IDahlgren>(this.resourceUrl, dahlgren, { observe: 'response' });
  }

  update(dahlgren: IDahlgren): Observable<EntityResponseType> {
    return this.http.put<IDahlgren>(`${this.resourceUrl}/${getDahlgrenIdentifier(dahlgren) as number}`, dahlgren, { observe: 'response' });
  }

  partialUpdate(dahlgren: IDahlgren): Observable<EntityResponseType> {
    return this.http.patch<IDahlgren>(`${this.resourceUrl}/${getDahlgrenIdentifier(dahlgren) as number}`, dahlgren, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDahlgren>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDahlgren[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addDahlgrenToCollectionIfMissing(dahlgrenCollection: IDahlgren[], ...dahlgrensToCheck: (IDahlgren | null | undefined)[]): IDahlgren[] {
    const dahlgrens: IDahlgren[] = dahlgrensToCheck.filter(isPresent);
    if (dahlgrens.length > 0) {
      const dahlgrenCollectionIdentifiers = dahlgrenCollection.map(dahlgrenItem => getDahlgrenIdentifier(dahlgrenItem)!);
      const dahlgrensToAdd = dahlgrens.filter(dahlgrenItem => {
        const dahlgrenIdentifier = getDahlgrenIdentifier(dahlgrenItem);
        if (dahlgrenIdentifier == null || dahlgrenCollectionIdentifiers.includes(dahlgrenIdentifier)) {
          return false;
        }
        dahlgrenCollectionIdentifiers.push(dahlgrenIdentifier);
        return true;
      });
      return [...dahlgrensToAdd, ...dahlgrenCollection];
    }
    return dahlgrenCollection;
  }
}
