import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { INomVernaculaire, getNomVernaculaireIdentifier } from '../nom-vernaculaire.model';

export type EntityResponseType = HttpResponse<INomVernaculaire>;
export type EntityArrayResponseType = HttpResponse<INomVernaculaire[]>;

@Injectable({ providedIn: 'root' })
export class NomVernaculaireService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/nom-vernaculaires');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(nomVernaculaire: INomVernaculaire): Observable<EntityResponseType> {
    return this.http.post<INomVernaculaire>(this.resourceUrl, nomVernaculaire, { observe: 'response' });
  }

  update(nomVernaculaire: INomVernaculaire): Observable<EntityResponseType> {
    return this.http.put<INomVernaculaire>(
      `${this.resourceUrl}/${getNomVernaculaireIdentifier(nomVernaculaire) as number}`,
      nomVernaculaire,
      { observe: 'response' }
    );
  }

  partialUpdate(nomVernaculaire: INomVernaculaire): Observable<EntityResponseType> {
    return this.http.patch<INomVernaculaire>(
      `${this.resourceUrl}/${getNomVernaculaireIdentifier(nomVernaculaire) as number}`,
      nomVernaculaire,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<INomVernaculaire>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INomVernaculaire[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addNomVernaculaireToCollectionIfMissing(
    nomVernaculaireCollection: INomVernaculaire[],
    ...nomVernaculairesToCheck: (INomVernaculaire | null | undefined)[]
  ): INomVernaculaire[] {
    const nomVernaculaires: INomVernaculaire[] = nomVernaculairesToCheck.filter(isPresent);
    if (nomVernaculaires.length > 0) {
      const nomVernaculaireCollectionIdentifiers = nomVernaculaireCollection.map(
        nomVernaculaireItem => getNomVernaculaireIdentifier(nomVernaculaireItem)!
      );
      const nomVernaculairesToAdd = nomVernaculaires.filter(nomVernaculaireItem => {
        const nomVernaculaireIdentifier = getNomVernaculaireIdentifier(nomVernaculaireItem);
        if (nomVernaculaireIdentifier == null || nomVernaculaireCollectionIdentifiers.includes(nomVernaculaireIdentifier)) {
          return false;
        }
        nomVernaculaireCollectionIdentifiers.push(nomVernaculaireIdentifier);
        return true;
      });
      return [...nomVernaculairesToAdd, ...nomVernaculaireCollection];
    }
    return nomVernaculaireCollection;
  }
}
