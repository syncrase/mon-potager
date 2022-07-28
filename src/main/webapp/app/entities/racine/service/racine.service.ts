import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRacine, getRacineIdentifier } from '../racine.model';

export type EntityResponseType = HttpResponse<IRacine>;
export type EntityArrayResponseType = HttpResponse<IRacine[]>;

@Injectable({ providedIn: 'root' })
export class RacineService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/racines');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(racine: IRacine): Observable<EntityResponseType> {
    return this.http.post<IRacine>(this.resourceUrl, racine, { observe: 'response' });
  }

  update(racine: IRacine): Observable<EntityResponseType> {
    return this.http.put<IRacine>(`${this.resourceUrl}/${getRacineIdentifier(racine) as number}`, racine, { observe: 'response' });
  }

  partialUpdate(racine: IRacine): Observable<EntityResponseType> {
    return this.http.patch<IRacine>(`${this.resourceUrl}/${getRacineIdentifier(racine) as number}`, racine, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRacine>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRacine[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addRacineToCollectionIfMissing(racineCollection: IRacine[], ...racinesToCheck: (IRacine | null | undefined)[]): IRacine[] {
    const racines: IRacine[] = racinesToCheck.filter(isPresent);
    if (racines.length > 0) {
      const racineCollectionIdentifiers = racineCollection.map(racineItem => getRacineIdentifier(racineItem)!);
      const racinesToAdd = racines.filter(racineItem => {
        const racineIdentifier = getRacineIdentifier(racineItem);
        if (racineIdentifier == null || racineCollectionIdentifiers.includes(racineIdentifier)) {
          return false;
        }
        racineCollectionIdentifiers.push(racineIdentifier);
        return true;
      });
      return [...racinesToAdd, ...racineCollection];
    }
    return racineCollection;
  }
}
