import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICycleDeVie, CycleDeVie } from '../cycle-de-vie.model';
import { CycleDeVieService } from '../service/cycle-de-vie.service';

@Injectable({ providedIn: 'root' })
export class CycleDeVieRoutingResolveService implements Resolve<ICycleDeVie> {
  constructor(protected service: CycleDeVieService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICycleDeVie> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((cycleDeVie: HttpResponse<CycleDeVie>) => {
          if (cycleDeVie.body) {
            return of(cycleDeVie.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CycleDeVie());
  }
}
