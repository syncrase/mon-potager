import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITakhtajan, Takhtajan } from '../takhtajan.model';
import { TakhtajanService } from '../service/takhtajan.service';

@Injectable({ providedIn: 'root' })
export class TakhtajanRoutingResolveService implements Resolve<ITakhtajan> {
  constructor(protected service: TakhtajanService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITakhtajan> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((takhtajan: HttpResponse<Takhtajan>) => {
          if (takhtajan.body) {
            return of(takhtajan.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Takhtajan());
  }
}
