import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICycleDeVie, getCycleDeVieIdentifier } from '../cycle-de-vie.model';

export type EntityResponseType = HttpResponse<ICycleDeVie>;
export type EntityArrayResponseType = HttpResponse<ICycleDeVie[]>;

@Injectable({ providedIn: 'root' })
export class CycleDeVieService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cycle-de-vies');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(cycleDeVie: ICycleDeVie): Observable<EntityResponseType> {
    return this.http.post<ICycleDeVie>(this.resourceUrl, cycleDeVie, { observe: 'response' });
  }

  update(cycleDeVie: ICycleDeVie): Observable<EntityResponseType> {
    return this.http.put<ICycleDeVie>(`${this.resourceUrl}/${getCycleDeVieIdentifier(cycleDeVie) as number}`, cycleDeVie, {
      observe: 'response',
    });
  }

  partialUpdate(cycleDeVie: ICycleDeVie): Observable<EntityResponseType> {
    return this.http.patch<ICycleDeVie>(`${this.resourceUrl}/${getCycleDeVieIdentifier(cycleDeVie) as number}`, cycleDeVie, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICycleDeVie>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICycleDeVie[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCycleDeVieToCollectionIfMissing(
    cycleDeVieCollection: ICycleDeVie[],
    ...cycleDeViesToCheck: (ICycleDeVie | null | undefined)[]
  ): ICycleDeVie[] {
    const cycleDeVies: ICycleDeVie[] = cycleDeViesToCheck.filter(isPresent);
    if (cycleDeVies.length > 0) {
      const cycleDeVieCollectionIdentifiers = cycleDeVieCollection.map(cycleDeVieItem => getCycleDeVieIdentifier(cycleDeVieItem)!);
      const cycleDeViesToAdd = cycleDeVies.filter(cycleDeVieItem => {
        const cycleDeVieIdentifier = getCycleDeVieIdentifier(cycleDeVieItem);
        if (cycleDeVieIdentifier == null || cycleDeVieCollectionIdentifiers.includes(cycleDeVieIdentifier)) {
          return false;
        }
        cycleDeVieCollectionIdentifiers.push(cycleDeVieIdentifier);
        return true;
      });
      return [...cycleDeViesToAdd, ...cycleDeVieCollection];
    }
    return cycleDeVieCollection;
  }
}
