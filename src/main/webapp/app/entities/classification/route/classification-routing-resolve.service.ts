import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {EMPTY, Observable, of} from 'rxjs';
import {mergeMap} from 'rxjs/operators';

import {Classification, IClassification} from '../classification.model';
import {ClassificationService} from '../service/classification.service';

@Injectable({ providedIn: 'root' })
export class ClassificationRoutingResolveService implements Resolve<IClassification> {
  constructor(protected service: ClassificationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IClassification> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((classification: HttpResponse<Classification>) => {
          if (classification.body) {
            return of(classification.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Classification());
  }
}
