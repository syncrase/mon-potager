import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFeuillage, Feuillage } from '../feuillage.model';
import { FeuillageService } from '../service/feuillage.service';

@Injectable({ providedIn: 'root' })
export class FeuillageRoutingResolveService implements Resolve<IFeuillage> {
  constructor(protected service: FeuillageService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFeuillage> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((feuillage: HttpResponse<Feuillage>) => {
          if (feuillage.body) {
            return of(feuillage.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Feuillage());
  }
}
