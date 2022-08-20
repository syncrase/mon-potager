import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPlante, Plante } from '../plante.model';
import { PlanteService } from '../service/plante.service';

@Injectable({ providedIn: 'root' })
export class PlanteRoutingResolveService implements Resolve<IPlante> {
  constructor(protected service: PlanteService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPlante> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((plante: HttpResponse<Plante>) => {
          if (plante.body) {
            return of(plante.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Plante());
  }
}
