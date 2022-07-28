import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRacine, Racine } from '../racine.model';
import { RacineService } from '../service/racine.service';

@Injectable({ providedIn: 'root' })
export class RacineRoutingResolveService implements Resolve<IRacine> {
  constructor(protected service: RacineService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRacine> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((racine: HttpResponse<Racine>) => {
          if (racine.body) {
            return of(racine.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Racine());
  }
}
