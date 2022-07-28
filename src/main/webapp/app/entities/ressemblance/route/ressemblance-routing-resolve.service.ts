import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRessemblance, Ressemblance } from '../ressemblance.model';
import { RessemblanceService } from '../service/ressemblance.service';

@Injectable({ providedIn: 'root' })
export class RessemblanceRoutingResolveService implements Resolve<IRessemblance> {
  constructor(protected service: RessemblanceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRessemblance> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((ressemblance: HttpResponse<Ressemblance>) => {
          if (ressemblance.body) {
            return of(ressemblance.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Ressemblance());
  }
}
