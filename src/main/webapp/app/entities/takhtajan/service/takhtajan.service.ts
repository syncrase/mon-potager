import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITakhtajan, getTakhtajanIdentifier } from '../takhtajan.model';

export type EntityResponseType = HttpResponse<ITakhtajan>;
export type EntityArrayResponseType = HttpResponse<ITakhtajan[]>;

@Injectable({ providedIn: 'root' })
export class TakhtajanService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/takhtajans');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(takhtajan: ITakhtajan): Observable<EntityResponseType> {
    return this.http.post<ITakhtajan>(this.resourceUrl, takhtajan, { observe: 'response' });
  }

  update(takhtajan: ITakhtajan): Observable<EntityResponseType> {
    return this.http.put<ITakhtajan>(`${this.resourceUrl}/${getTakhtajanIdentifier(takhtajan) as number}`, takhtajan, {
      observe: 'response',
    });
  }

  partialUpdate(takhtajan: ITakhtajan): Observable<EntityResponseType> {
    return this.http.patch<ITakhtajan>(`${this.resourceUrl}/${getTakhtajanIdentifier(takhtajan) as number}`, takhtajan, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITakhtajan>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITakhtajan[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTakhtajanToCollectionIfMissing(
    takhtajanCollection: ITakhtajan[],
    ...takhtajansToCheck: (ITakhtajan | null | undefined)[]
  ): ITakhtajan[] {
    const takhtajans: ITakhtajan[] = takhtajansToCheck.filter(isPresent);
    if (takhtajans.length > 0) {
      const takhtajanCollectionIdentifiers = takhtajanCollection.map(takhtajanItem => getTakhtajanIdentifier(takhtajanItem)!);
      const takhtajansToAdd = takhtajans.filter(takhtajanItem => {
        const takhtajanIdentifier = getTakhtajanIdentifier(takhtajanItem);
        if (takhtajanIdentifier == null || takhtajanCollectionIdentifiers.includes(takhtajanIdentifier)) {
          return false;
        }
        takhtajanCollectionIdentifiers.push(takhtajanIdentifier);
        return true;
      });
      return [...takhtajansToAdd, ...takhtajanCollection];
    }
    return takhtajanCollection;
  }
}
