import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access.service';
import {PlanteUpdateComponent} from '../update/plante-update.component';
import {SearchPlanteComponent} from "../search/search-plante.component";
import {FichePlanteComponent} from "../fiche-plante/fiche-plante.component";

const planteRoute: Routes = [
  {
    path: '',
    component: SearchPlanteComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'update',
    component: PlanteUpdateComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'fiche',
    component: FichePlanteComponent,
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(planteRoute)],
  exports: [RouterModule],
})
export class AddPlanteRoutingModule {}
