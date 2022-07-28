import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PlanteComponent } from '../list/plante.component';
import { PlanteDetailComponent } from '../detail/plante-detail.component';
import { PlanteUpdateComponent } from '../update/plante-update.component';
import { PlanteRoutingResolveService } from './plante-routing-resolve.service';

const planteRoute: Routes = [
  {
    path: '',
    component: PlanteComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PlanteDetailComponent,
    resolve: {
      plante: PlanteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PlanteUpdateComponent,
    resolve: {
      plante: PlanteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PlanteUpdateComponent,
    resolve: {
      plante: PlanteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(planteRoute)],
  exports: [RouterModule],
})
export class PlanteRoutingModule {}
