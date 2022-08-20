import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {EMPTY, Observable, of} from 'rxjs';
import {mergeMap} from 'rxjs/operators';

import {BenthamHooker, IBenthamHooker} from '../bentham-hooker.model';
import {BenthamHookerService} from '../service/bentham-hooker.service';

@Injectable({ providedIn: 'root' })
export class BenthamHookerRoutingResolveService implements Resolve<IBenthamHooker> {
  constructor(protected service: BenthamHookerService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBenthamHooker> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((benthamHooker: HttpResponse<BenthamHooker>) => {
          if (benthamHooker.body) {
            return of(benthamHooker.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new BenthamHooker());
  }
}
