import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { StrateComponent } from '../list/strate.component';
import { StrateDetailComponent } from '../detail/strate-detail.component';
import { StrateUpdateComponent } from '../update/strate-update.component';
import { StrateRoutingResolveService } from './strate-routing-resolve.service';

const strateRoute: Routes = [
  {
    path: '',
    component: StrateComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: StrateDetailComponent,
    resolve: {
      strate: StrateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: StrateUpdateComponent,
    resolve: {
      strate: StrateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: StrateUpdateComponent,
    resolve: {
      strate: StrateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(strateRoute)],
  exports: [RouterModule],
})
export class StrateRoutingModule {}
