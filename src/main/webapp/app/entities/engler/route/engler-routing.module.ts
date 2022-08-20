import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EnglerComponent } from '../list/engler.component';
import { EnglerDetailComponent } from '../detail/engler-detail.component';
import { EnglerUpdateComponent } from '../update/engler-update.component';
import { EnglerRoutingResolveService } from './engler-routing-resolve.service';

const englerRoute: Routes = [
  {
    path: '',
    component: EnglerComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EnglerDetailComponent,
    resolve: {
      engler: EnglerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EnglerUpdateComponent,
    resolve: {
      engler: EnglerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EnglerUpdateComponent,
    resolve: {
      engler: EnglerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(englerRoute)],
  exports: [RouterModule],
})
export class EnglerRoutingModule {}
