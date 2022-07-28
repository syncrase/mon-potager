import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGermination, Germination } from '../germination.model';
import { GerminationService } from '../service/germination.service';

@Injectable({ providedIn: 'root' })
export class GerminationRoutingResolveService implements Resolve<IGermination> {
  constructor(protected service: GerminationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGermination> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((germination: HttpResponse<Germination>) => {
          if (germination.body) {
            return of(germination.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Germination());
  }
}
