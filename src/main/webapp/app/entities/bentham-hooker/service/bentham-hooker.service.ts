import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getBenthamHookerIdentifier, IBenthamHooker} from '../bentham-hooker.model';

export type EntityResponseType = HttpResponse<IBenthamHooker>;
export type EntityArrayResponseType = HttpResponse<IBenthamHooker[]>;

@Injectable({ providedIn: 'root' })
export class BenthamHookerService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/bentham-hookers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(benthamHooker: IBenthamHooker): Observable<EntityResponseType> {
    return this.http.post<IBenthamHooker>(this.resourceUrl, benthamHooker, { observe: 'response' });
  }

  update(benthamHooker: IBenthamHooker): Observable<EntityResponseType> {
    return this.http.put<IBenthamHooker>(`${this.resourceUrl}/${getBenthamHookerIdentifier(benthamHooker) as number}`, benthamHooker, {
      observe: 'response',
    });
  }

  partialUpdate(benthamHooker: IBenthamHooker): Observable<EntityResponseType> {
    return this.http.patch<IBenthamHooker>(`${this.resourceUrl}/${getBenthamHookerIdentifier(benthamHooker) as number}`, benthamHooker, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBenthamHooker>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBenthamHooker[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addBenthamHookerToCollectionIfMissing(
    benthamHookerCollection: IBenthamHooker[],
    ...benthamHookersToCheck: (IBenthamHooker | null | undefined)[]
  ): IBenthamHooker[] {
    const benthamHookers: IBenthamHooker[] = benthamHookersToCheck.filter(isPresent);
    if (benthamHookers.length > 0) {
      const benthamHookerCollectionIdentifiers = benthamHookerCollection.map(
        benthamHookerItem => getBenthamHookerIdentifier(benthamHookerItem)!
      );
      const benthamHookersToAdd = benthamHookers.filter(benthamHookerItem => {
        const benthamHookerIdentifier = getBenthamHookerIdentifier(benthamHookerItem);
        if (benthamHookerIdentifier == null || benthamHookerCollectionIdentifiers.includes(benthamHookerIdentifier)) {
          return false;
        }
        benthamHookerCollectionIdentifiers.push(benthamHookerIdentifier);
        return true;
      });
      return [...benthamHookersToAdd, ...benthamHookerCollection];
    }
    return benthamHookerCollection;
  }
}
