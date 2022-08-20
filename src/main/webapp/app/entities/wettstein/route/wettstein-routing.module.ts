import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { WettsteinComponent } from '../list/wettstein.component';
import { WettsteinDetailComponent } from '../detail/wettstein-detail.component';
import { WettsteinUpdateComponent } from '../update/wettstein-update.component';
import { WettsteinRoutingResolveService } from './wettstein-routing-resolve.service';

const wettsteinRoute: Routes = [
  {
    path: '',
    component: WettsteinComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: WettsteinDetailComponent,
    resolve: {
      wettstein: WettsteinRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: WettsteinUpdateComponent,
    resolve: {
      wettstein: WettsteinRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: WettsteinUpdateComponent,
    resolve: {
      wettstein: WettsteinRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(wettsteinRoute)],
  exports: [RouterModule],
})
export class WettsteinRoutingModule {}
