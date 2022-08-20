import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getCronquistRankIdentifier, ICronquistRank} from '../cronquist-rank.model';

export type EntityResponseType = HttpResponse<ICronquistRank>;
export type EntityArrayResponseType = HttpResponse<ICronquistRank[]>;

@Injectable({ providedIn: 'root' })
export class CronquistRankService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cronquist-ranks');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(cronquistRank: ICronquistRank): Observable<EntityResponseType> {
    return this.http.post<ICronquistRank>(this.resourceUrl, cronquistRank, { observe: 'response' });
  }

  update(cronquistRank: ICronquistRank): Observable<EntityResponseType> {
    return this.http.put<ICronquistRank>(`${this.resourceUrl}/${getCronquistRankIdentifier(cronquistRank) as number}`, cronquistRank, {
      observe: 'response',
    });
  }

  partialUpdate(cronquistRank: ICronquistRank): Observable<EntityResponseType> {
    return this.http.patch<ICronquistRank>(`${this.resourceUrl}/${getCronquistRankIdentifier(cronquistRank) as number}`, cronquistRank, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICronquistRank>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICronquistRank[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCronquistRankToCollectionIfMissing(
    cronquistRankCollection: ICronquistRank[],
    ...cronquistRanksToCheck: (ICronquistRank | null | undefined)[]
  ): ICronquistRank[] {
    const cronquistRanks: ICronquistRank[] = cronquistRanksToCheck.filter(isPresent);
    if (cronquistRanks.length > 0) {
      const cronquistRankCollectionIdentifiers = cronquistRankCollection.map(
        cronquistRankItem => getCronquistRankIdentifier(cronquistRankItem)!
      );
      const cronquistRanksToAdd = cronquistRanks.filter(cronquistRankItem => {
        const cronquistRankIdentifier = getCronquistRankIdentifier(cronquistRankItem);
        if (cronquistRankIdentifier == null || cronquistRankCollectionIdentifiers.includes(cronquistRankIdentifier)) {
          return false;
        }
        cronquistRankCollectionIdentifiers.push(cronquistRankIdentifier);
        return true;
      });
      return [...cronquistRanksToAdd, ...cronquistRankCollection];
    }
    return cronquistRankCollection;
  }
}
