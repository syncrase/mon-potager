import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReference, Reference } from '../reference.model';
import { ReferenceService } from '../service/reference.service';

@Injectable({ providedIn: 'root' })
export class ReferenceRoutingResolveService implements Resolve<IReference> {
  constructor(protected service: ReferenceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IReference> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((reference: HttpResponse<Reference>) => {
          if (reference.body) {
            return of(reference.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Reference());
  }
}
