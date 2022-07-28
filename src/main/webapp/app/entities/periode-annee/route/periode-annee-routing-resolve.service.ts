import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPeriodeAnnee, PeriodeAnnee } from '../periode-annee.model';
import { PeriodeAnneeService } from '../service/periode-annee.service';

@Injectable({ providedIn: 'root' })
export class PeriodeAnneeRoutingResolveService implements Resolve<IPeriodeAnnee> {
  constructor(protected service: PeriodeAnneeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPeriodeAnnee> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((periodeAnnee: HttpResponse<PeriodeAnnee>) => {
          if (periodeAnnee.body) {
            return of(periodeAnnee.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new PeriodeAnnee());
  }
}
