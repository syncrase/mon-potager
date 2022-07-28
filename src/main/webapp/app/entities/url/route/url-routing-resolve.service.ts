import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUrl, Url } from '../url.model';
import { UrlService } from '../service/url.service';

@Injectable({ providedIn: 'root' })
export class UrlRoutingResolveService implements Resolve<IUrl> {
  constructor(protected service: UrlService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUrl> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((url: HttpResponse<Url>) => {
          if (url.body) {
            return of(url.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Url());
  }
}
