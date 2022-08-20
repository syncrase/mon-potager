import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getClassificationIdentifier, IClassification} from '../classification.model';

export type EntityResponseType = HttpResponse<IClassification>;
export type EntityArrayResponseType = HttpResponse<IClassification[]>;

@Injectable({ providedIn: 'root' })
export class ClassificationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/classifications');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(classification: IClassification): Observable<EntityResponseType> {
    return this.http.post<IClassification>(this.resourceUrl, classification, { observe: 'response' });
  }

  update(classification: IClassification): Observable<EntityResponseType> {
    return this.http.put<IClassification>(`${this.resourceUrl}/${getClassificationIdentifier(classification) as number}`, classification, {
      observe: 'response',
    });
  }

  partialUpdate(classification: IClassification): Observable<EntityResponseType> {
    return this.http.patch<IClassification>(
      `${this.resourceUrl}/${getClassificationIdentifier(classification) as number}`,
      classification,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IClassification>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IClassification[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addClassificationToCollectionIfMissing(
    classificationCollection: IClassification[],
    ...classificationsToCheck: (IClassification | null | undefined)[]
  ): IClassification[] {
    const classifications: IClassification[] = classificationsToCheck.filter(isPresent);
    if (classifications.length > 0) {
      const classificationCollectionIdentifiers = classificationCollection.map(
        classificationItem => getClassificationIdentifier(classificationItem)!
      );
      const classificationsToAdd = classifications.filter(classificationItem => {
        const classificationIdentifier = getClassificationIdentifier(classificationItem);
        if (classificationIdentifier == null || classificationCollectionIdentifiers.includes(classificationIdentifier)) {
          return false;
        }
        classificationCollectionIdentifiers.push(classificationIdentifier);
        return true;
      });
      return [...classificationsToAdd, ...classificationCollection];
    }
    return classificationCollection;
  }
}
