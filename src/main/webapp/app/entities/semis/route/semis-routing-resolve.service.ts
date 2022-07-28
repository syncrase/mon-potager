import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISemis, Semis } from '../semis.model';
import { SemisService } from '../service/semis.service';

@Injectable({ providedIn: 'root' })
export class SemisRoutingResolveService implements Resolve<ISemis> {
  constructor(protected service: SemisService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISemis> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((semis: HttpResponse<Semis>) => {
          if (semis.body) {
            return of(semis.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Semis());
  }
}
