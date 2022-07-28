import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INomVernaculaire, NomVernaculaire } from '../nom-vernaculaire.model';
import { NomVernaculaireService } from '../service/nom-vernaculaire.service';

@Injectable({ providedIn: 'root' })
export class NomVernaculaireRoutingResolveService implements Resolve<INomVernaculaire> {
  constructor(protected service: NomVernaculaireService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INomVernaculaire> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((nomVernaculaire: HttpResponse<NomVernaculaire>) => {
          if (nomVernaculaire.body) {
            return of(nomVernaculaire.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new NomVernaculaire());
  }
}
