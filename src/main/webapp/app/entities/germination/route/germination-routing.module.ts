import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GerminationComponent } from '../list/germination.component';
import { GerminationDetailComponent } from '../detail/germination-detail.component';
import { GerminationUpdateComponent } from '../update/germination-update.component';
import { GerminationRoutingResolveService } from './germination-routing-resolve.service';

const germinationRoute: Routes = [
  {
    path: '',
    component: GerminationComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GerminationDetailComponent,
    resolve: {
      germination: GerminationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GerminationUpdateComponent,
    resolve: {
      germination: GerminationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GerminationUpdateComponent,
    resolve: {
      germination: GerminationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(germinationRoute)],
  exports: [RouterModule],
})
export class GerminationRoutingModule {}
