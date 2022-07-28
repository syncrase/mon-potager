import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReproduction, Reproduction } from '../reproduction.model';
import { ReproductionService } from '../service/reproduction.service';

@Injectable({ providedIn: 'root' })
export class ReproductionRoutingResolveService implements Resolve<IReproduction> {
  constructor(protected service: ReproductionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IReproduction> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((reproduction: HttpResponse<Reproduction>) => {
          if (reproduction.body) {
            return of(reproduction.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Reproduction());
  }
}
