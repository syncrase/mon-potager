import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access.service';
import {PlanteUpdateComponent} from '../update/plante-update.component';
import {SearchPlanteComponent} from "../search/search-plante.component";

const planteRoute: Routes = [
  {
    path: '',
    component: SearchPlanteComponent,
    canActivate: [UserRouteAccessService],
  },
  // {
  //   path: 'update/:planteName',
  //   component: PlanteUpdateComponent,
  //   resolve: {
  //     plante: AddPlanteRoutingResolveService,
  //   },
  //   canActivate: [UserRouteAccessService],
  // },
  {
    path: 'update',
    component: PlanteUpdateComponent,
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(planteRoute)],
  exports: [RouterModule],
})
export class AddPlanteRoutingModule {}
