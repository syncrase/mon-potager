import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {EMPTY, Observable, of} from 'rxjs';
import {mergeMap} from 'rxjs/operators';

import {APG, IAPG} from '../apg.model';
import {APGService} from '../service/apg.service';

@Injectable({ providedIn: 'root' })
export class APGRoutingResolveService implements Resolve<IAPG> {
  constructor(protected service: APGService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAPG> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((aPG: HttpResponse<APG>) => {
          if (aPG.body) {
            return of(aPG.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new APG());
  }
}
