import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {EMPTY, Observable, of} from 'rxjs';
import {mergeMap} from 'rxjs/operators';

import {CronquistRank, ICronquistRank} from '../cronquist-rank.model';
import {CronquistRankService} from '../service/cronquist-rank.service';

@Injectable({ providedIn: 'root' })
export class CronquistRankRoutingResolveService implements Resolve<ICronquistRank> {
  constructor(protected service: CronquistRankService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICronquistRank> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((cronquistRank: HttpResponse<CronquistRank>) => {
          if (cronquistRank.body) {
            return of(cronquistRank.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CronquistRank());
  }
}
