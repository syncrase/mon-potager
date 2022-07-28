import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IStrate, getStrateIdentifier } from '../strate.model';

export type EntityResponseType = HttpResponse<IStrate>;
export type EntityArrayResponseType = HttpResponse<IStrate[]>;

@Injectable({ providedIn: 'root' })
export class StrateService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/strates');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(strate: IStrate): Observable<EntityResponseType> {
    return this.http.post<IStrate>(this.resourceUrl, strate, { observe: 'response' });
  }

  update(strate: IStrate): Observable<EntityResponseType> {
    return this.http.put<IStrate>(`${this.resourceUrl}/${getStrateIdentifier(strate) as number}`, strate, { observe: 'response' });
  }

  partialUpdate(strate: IStrate): Observable<EntityResponseType> {
    return this.http.patch<IStrate>(`${this.resourceUrl}/${getStrateIdentifier(strate) as number}`, strate, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IStrate>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IStrate[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addStrateToCollectionIfMissing(strateCollection: IStrate[], ...stratesToCheck: (IStrate | null | undefined)[]): IStrate[] {
    const strates: IStrate[] = stratesToCheck.filter(isPresent);
    if (strates.length > 0) {
      const strateCollectionIdentifiers = strateCollection.map(strateItem => getStrateIdentifier(strateItem)!);
      const stratesToAdd = strates.filter(strateItem => {
        const strateIdentifier = getStrateIdentifier(strateItem);
        if (strateIdentifier == null || strateCollectionIdentifiers.includes(strateIdentifier)) {
          return false;
        }
        strateCollectionIdentifiers.push(strateIdentifier);
        return true;
      });
      return [...stratesToAdd, ...strateCollection];
    }
    return strateCollection;
  }
}
