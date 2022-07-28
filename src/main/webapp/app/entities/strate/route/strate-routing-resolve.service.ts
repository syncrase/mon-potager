import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStrate, Strate } from '../strate.model';
import { StrateService } from '../service/strate.service';

@Injectable({ providedIn: 'root' })
export class StrateRoutingResolveService implements Resolve<IStrate> {
  constructor(protected service: StrateService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IStrate> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((strate: HttpResponse<Strate>) => {
          if (strate.body) {
            return of(strate.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Strate());
  }
}
