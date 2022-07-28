import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ReproductionComponent } from '../list/reproduction.component';
import { ReproductionDetailComponent } from '../detail/reproduction-detail.component';
import { ReproductionUpdateComponent } from '../update/reproduction-update.component';
import { ReproductionRoutingResolveService } from './reproduction-routing-resolve.service';

const reproductionRoute: Routes = [
  {
    path: '',
    component: ReproductionComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ReproductionDetailComponent,
    resolve: {
      reproduction: ReproductionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ReproductionUpdateComponent,
    resolve: {
      reproduction: ReproductionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ReproductionUpdateComponent,
    resolve: {
      reproduction: ReproductionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(reproductionRoute)],
  exports: [RouterModule],
})
export class ReproductionRoutingModule {}
