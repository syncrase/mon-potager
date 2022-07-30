import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {Resolve, ActivatedRouteSnapshot, Router} from '@angular/router';
import {Observable, of, EMPTY} from 'rxjs';
import {mergeMap} from 'rxjs/operators';
import {IPlante, Plante} from "../../../entities/plante/plante.model";
import {AddPlanteService} from "../service/add-plante.service";

@Injectable({providedIn: 'root'})
export class AddPlanteRoutingResolveService implements Resolve<IPlante> {
  constructor(protected service: AddPlanteService, protected router: Router) {
  }

  resolve(route: ActivatedRouteSnapshot): Observable<IPlante> | Observable<never> {
    const planteName = route.params['planteName'];
    if (planteName) {
      return this.service.search(planteName).pipe(
        mergeMap((plante: HttpResponse<Plante>) => {
          if (plante.body) {
            return of(plante.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Plante());
  }
}
