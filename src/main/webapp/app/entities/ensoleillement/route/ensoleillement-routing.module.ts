import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EnsoleillementComponent } from '../list/ensoleillement.component';
import { EnsoleillementDetailComponent } from '../detail/ensoleillement-detail.component';
import { EnsoleillementUpdateComponent } from '../update/ensoleillement-update.component';
import { EnsoleillementRoutingResolveService } from './ensoleillement-routing-resolve.service';

const ensoleillementRoute: Routes = [
  {
    path: '',
    component: EnsoleillementComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EnsoleillementDetailComponent,
    resolve: {
      ensoleillement: EnsoleillementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EnsoleillementUpdateComponent,
    resolve: {
      ensoleillement: EnsoleillementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EnsoleillementUpdateComponent,
    resolve: {
      ensoleillement: EnsoleillementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(ensoleillementRoute)],
  exports: [RouterModule],
})
export class EnsoleillementRoutingModule {}
