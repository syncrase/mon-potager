import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEnsoleillement, Ensoleillement } from '../ensoleillement.model';
import { EnsoleillementService } from '../service/ensoleillement.service';

@Injectable({ providedIn: 'root' })
export class EnsoleillementRoutingResolveService implements Resolve<IEnsoleillement> {
  constructor(protected service: EnsoleillementService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEnsoleillement> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((ensoleillement: HttpResponse<Ensoleillement>) => {
          if (ensoleillement.body) {
            return of(ensoleillement.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Ensoleillement());
  }
}
