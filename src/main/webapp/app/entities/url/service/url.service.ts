import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUrl, getUrlIdentifier } from '../url.model';

export type EntityResponseType = HttpResponse<IUrl>;
export type EntityArrayResponseType = HttpResponse<IUrl[]>;

@Injectable({ providedIn: 'root' })
export class UrlService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/urls');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(url: IUrl): Observable<EntityResponseType> {
    return this.http.post<IUrl>(this.resourceUrl, url, { observe: 'response' });
  }

  update(url: IUrl): Observable<EntityResponseType> {
    return this.http.put<IUrl>(`${this.resourceUrl}/${getUrlIdentifier(url) as number}`, url, { observe: 'response' });
  }

  partialUpdate(url: IUrl): Observable<EntityResponseType> {
    return this.http.patch<IUrl>(`${this.resourceUrl}/${getUrlIdentifier(url) as number}`, url, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUrl>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUrl[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addUrlToCollectionIfMissing(urlCollection: IUrl[], ...urlsToCheck: (IUrl | null | undefined)[]): IUrl[] {
    const urls: IUrl[] = urlsToCheck.filter(isPresent);
    if (urls.length > 0) {
      const urlCollectionIdentifiers = urlCollection.map(urlItem => getUrlIdentifier(urlItem)!);
      const urlsToAdd = urls.filter(urlItem => {
        const urlIdentifier = getUrlIdentifier(urlItem);
        if (urlIdentifier == null || urlCollectionIdentifiers.includes(urlIdentifier)) {
          return false;
        }
        urlCollectionIdentifiers.push(urlIdentifier);
        return true;
      });
      return [...urlsToAdd, ...urlCollection];
    }
    return urlCollection;
  }
}
