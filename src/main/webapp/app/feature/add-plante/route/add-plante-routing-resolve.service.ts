import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {EMPTY, Observable, of} from 'rxjs';
import {mergeMap} from 'rxjs/operators';
import {AddPlanteService} from "../service/add-plante.service";
import {AlertService} from "../../../core/util/alert.service";
import {IScrapedPlante, ScrapedPlante} from "../scraped-plant.model";

@Injectable({providedIn: 'root'})
export class AddPlanteRoutingResolveService implements Resolve<IScrapedPlante> {

  constructor(
    protected service: AddPlanteService,
    protected router: Router,
    private alertService: AlertService
  ) {
  }

  resolve(route: ActivatedRouteSnapshot): Observable<IScrapedPlante> | Observable<never> {
    const planteName = route.params['planteName'];
    if (planteName) {
      return this.service.search(planteName).pipe(
        mergeMap((plante: HttpResponse<ScrapedPlante>) => {
          switch (plante.status) {
            case 200:
              return plante.body ? of(plante.body) : this.redirectTo404();
            case 204:
              this.alertService.addAlert({type: 'danger', message: 'Impossible de scraper Wikipedia'});
              return EMPTY;
            case 404:
              return this.redirectTo404();
            default:
              return this.redirectTo404();
          }
        })
      );
    }
    return of(new ScrapedPlante());
  }

  private redirectTo404(): Observable<never> {
    this.router.navigate(['404']);
    return EMPTY;
  }
}
