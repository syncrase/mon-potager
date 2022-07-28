import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMois, Mois } from '../mois.model';
import { MoisService } from '../service/mois.service';

@Injectable({ providedIn: 'root' })
export class MoisRoutingResolveService implements Resolve<IMois> {
  constructor(protected service: MoisService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMois> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((mois: HttpResponse<Mois>) => {
          if (mois.body) {
            return of(mois.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Mois());
  }
}
