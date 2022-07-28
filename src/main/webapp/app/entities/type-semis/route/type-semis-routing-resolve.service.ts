import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITypeSemis, TypeSemis } from '../type-semis.model';
import { TypeSemisService } from '../service/type-semis.service';

@Injectable({ providedIn: 'root' })
export class TypeSemisRoutingResolveService implements Resolve<ITypeSemis> {
  constructor(protected service: TypeSemisService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITypeSemis> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((typeSemis: HttpResponse<TypeSemis>) => {
          if (typeSemis.body) {
            return of(typeSemis.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TypeSemis());
  }
}
