import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IThorne, Thorne } from '../thorne.model';
import { ThorneService } from '../service/thorne.service';

@Injectable({ providedIn: 'root' })
export class ThorneRoutingResolveService implements Resolve<IThorne> {
  constructor(protected service: ThorneService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IThorne> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((thorne: HttpResponse<Thorne>) => {
          if (thorne.body) {
            return of(thorne.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Thorne());
  }
}
