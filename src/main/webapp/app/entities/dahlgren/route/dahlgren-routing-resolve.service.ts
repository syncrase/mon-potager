import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {EMPTY, Observable, of} from 'rxjs';
import {mergeMap} from 'rxjs/operators';

import {Dahlgren, IDahlgren} from '../dahlgren.model';
import {DahlgrenService} from '../service/dahlgren.service';

@Injectable({ providedIn: 'root' })
export class DahlgrenRoutingResolveService implements Resolve<IDahlgren> {
  constructor(protected service: DahlgrenService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDahlgren> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((dahlgren: HttpResponse<Dahlgren>) => {
          if (dahlgren.body) {
            return of(dahlgren.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Dahlgren());
  }
}
