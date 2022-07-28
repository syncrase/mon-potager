import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAllelopathie, Allelopathie } from '../allelopathie.model';
import { AllelopathieService } from '../service/allelopathie.service';

@Injectable({ providedIn: 'root' })
export class AllelopathieRoutingResolveService implements Resolve<IAllelopathie> {
  constructor(protected service: AllelopathieService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAllelopathie> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((allelopathie: HttpResponse<Allelopathie>) => {
          if (allelopathie.body) {
            return of(allelopathie.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Allelopathie());
  }
}
