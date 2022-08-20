import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEngler, Engler } from '../engler.model';
import { EnglerService } from '../service/engler.service';

@Injectable({ providedIn: 'root' })
export class EnglerRoutingResolveService implements Resolve<IEngler> {
  constructor(protected service: EnglerService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEngler> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((engler: HttpResponse<Engler>) => {
          if (engler.body) {
            return of(engler.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Engler());
  }
}
