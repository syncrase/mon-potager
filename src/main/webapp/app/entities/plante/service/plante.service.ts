import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPlante, getPlanteIdentifier } from '../plante.model';

export type EntityResponseType = HttpResponse<IPlante>;
export type EntityArrayResponseType = HttpResponse<IPlante[]>;

@Injectable({ providedIn: 'root' })
export class PlanteService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/plantes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(plante: IPlante): Observable<EntityResponseType> {
    return this.http.post<IPlante>(this.resourceUrl, plante, { observe: 'response' });
  }

  update(plante: IPlante): Observable<EntityResponseType> {
    return this.http.put<IPlante>(`${this.resourceUrl}/${getPlanteIdentifier(plante) as number}`, plante, { observe: 'response' });
  }

  partialUpdate(plante: IPlante): Observable<EntityResponseType> {
    return this.http.patch<IPlante>(`${this.resourceUrl}/${getPlanteIdentifier(plante) as number}`, plante, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPlante>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPlante[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPlanteToCollectionIfMissing(planteCollection: IPlante[], ...plantesToCheck: (IPlante | null | undefined)[]): IPlante[] {
    const plantes: IPlante[] = plantesToCheck.filter(isPresent);
    if (plantes.length > 0) {
      const planteCollectionIdentifiers = planteCollection.map(planteItem => getPlanteIdentifier(planteItem)!);
      const plantesToAdd = plantes.filter(planteItem => {
        const planteIdentifier = getPlanteIdentifier(planteItem);
        if (planteIdentifier == null || planteCollectionIdentifiers.includes(planteIdentifier)) {
          return false;
        }
        planteCollectionIdentifiers.push(planteIdentifier);
        return true;
      });
      return [...plantesToAdd, ...planteCollection];
    }
    return planteCollection;
  }
}
