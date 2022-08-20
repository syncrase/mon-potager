import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {EMPTY, Observable, of} from 'rxjs';
import {mergeMap} from 'rxjs/operators';

import {Candolle, ICandolle} from '../candolle.model';
import {CandolleService} from '../service/candolle.service';

@Injectable({ providedIn: 'root' })
export class CandolleRoutingResolveService implements Resolve<ICandolle> {
  constructor(protected service: CandolleService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICandolle> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((candolle: HttpResponse<Candolle>) => {
          if (candolle.body) {
            return of(candolle.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Candolle());
  }
}
