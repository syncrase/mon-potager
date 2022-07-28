import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPeriodeAnnee, getPeriodeAnneeIdentifier } from '../periode-annee.model';

export type EntityResponseType = HttpResponse<IPeriodeAnnee>;
export type EntityArrayResponseType = HttpResponse<IPeriodeAnnee[]>;

@Injectable({ providedIn: 'root' })
export class PeriodeAnneeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/periode-annees');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(periodeAnnee: IPeriodeAnnee): Observable<EntityResponseType> {
    return this.http.post<IPeriodeAnnee>(this.resourceUrl, periodeAnnee, { observe: 'response' });
  }

  update(periodeAnnee: IPeriodeAnnee): Observable<EntityResponseType> {
    return this.http.put<IPeriodeAnnee>(`${this.resourceUrl}/${getPeriodeAnneeIdentifier(periodeAnnee) as number}`, periodeAnnee, {
      observe: 'response',
    });
  }

  partialUpdate(periodeAnnee: IPeriodeAnnee): Observable<EntityResponseType> {
    return this.http.patch<IPeriodeAnnee>(`${this.resourceUrl}/${getPeriodeAnneeIdentifier(periodeAnnee) as number}`, periodeAnnee, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPeriodeAnnee>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPeriodeAnnee[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPeriodeAnneeToCollectionIfMissing(
    periodeAnneeCollection: IPeriodeAnnee[],
    ...periodeAnneesToCheck: (IPeriodeAnnee | null | undefined)[]
  ): IPeriodeAnnee[] {
    const periodeAnnees: IPeriodeAnnee[] = periodeAnneesToCheck.filter(isPresent);
    if (periodeAnnees.length > 0) {
      const periodeAnneeCollectionIdentifiers = periodeAnneeCollection.map(
        periodeAnneeItem => getPeriodeAnneeIdentifier(periodeAnneeItem)!
      );
      const periodeAnneesToAdd = periodeAnnees.filter(periodeAnneeItem => {
        const periodeAnneeIdentifier = getPeriodeAnneeIdentifier(periodeAnneeItem);
        if (periodeAnneeIdentifier == null || periodeAnneeCollectionIdentifiers.includes(periodeAnneeIdentifier)) {
          return false;
        }
        periodeAnneeCollectionIdentifiers.push(periodeAnneeIdentifier);
        return true;
      });
      return [...periodeAnneesToAdd, ...periodeAnneeCollection];
    }
    return periodeAnneeCollection;
  }
}
