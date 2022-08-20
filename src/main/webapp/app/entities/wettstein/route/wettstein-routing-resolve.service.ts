import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IWettstein, Wettstein } from '../wettstein.model';
import { WettsteinService } from '../service/wettstein.service';

@Injectable({ providedIn: 'root' })
export class WettsteinRoutingResolveService implements Resolve<IWettstein> {
  constructor(protected service: WettsteinService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IWettstein> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((wettstein: HttpResponse<Wettstein>) => {
          if (wettstein.body) {
            return of(wettstein.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Wettstein());
  }
}
