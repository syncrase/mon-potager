import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAllelopathie, getAllelopathieIdentifier } from '../allelopathie.model';

export type EntityResponseType = HttpResponse<IAllelopathie>;
export type EntityArrayResponseType = HttpResponse<IAllelopathie[]>;

@Injectable({ providedIn: 'root' })
export class AllelopathieService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/allelopathies');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(allelopathie: IAllelopathie): Observable<EntityResponseType> {
    return this.http.post<IAllelopathie>(this.resourceUrl, allelopathie, { observe: 'response' });
  }

  update(allelopathie: IAllelopathie): Observable<EntityResponseType> {
    return this.http.put<IAllelopathie>(`${this.resourceUrl}/${getAllelopathieIdentifier(allelopathie) as number}`, allelopathie, {
      observe: 'response',
    });
  }

  partialUpdate(allelopathie: IAllelopathie): Observable<EntityResponseType> {
    return this.http.patch<IAllelopathie>(`${this.resourceUrl}/${getAllelopathieIdentifier(allelopathie) as number}`, allelopathie, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAllelopathie>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAllelopathie[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAllelopathieToCollectionIfMissing(
    allelopathieCollection: IAllelopathie[],
    ...allelopathiesToCheck: (IAllelopathie | null | undefined)[]
  ): IAllelopathie[] {
    const allelopathies: IAllelopathie[] = allelopathiesToCheck.filter(isPresent);
    if (allelopathies.length > 0) {
      const allelopathieCollectionIdentifiers = allelopathieCollection.map(
        allelopathieItem => getAllelopathieIdentifier(allelopathieItem)!
      );
      const allelopathiesToAdd = allelopathies.filter(allelopathieItem => {
        const allelopathieIdentifier = getAllelopathieIdentifier(allelopathieItem);
        if (allelopathieIdentifier == null || allelopathieCollectionIdentifiers.includes(allelopathieIdentifier)) {
          return false;
        }
        allelopathieCollectionIdentifiers.push(allelopathieIdentifier);
        return true;
      });
      return [...allelopathiesToAdd, ...allelopathieCollection];
    }
    return allelopathieCollection;
  }
}
