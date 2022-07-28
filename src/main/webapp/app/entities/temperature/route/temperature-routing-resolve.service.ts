import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITemperature, Temperature } from '../temperature.model';
import { TemperatureService } from '../service/temperature.service';

@Injectable({ providedIn: 'root' })
export class TemperatureRoutingResolveService implements Resolve<ITemperature> {
  constructor(protected service: TemperatureService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITemperature> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((temperature: HttpResponse<Temperature>) => {
          if (temperature.body) {
            return of(temperature.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Temperature());
  }
}
