import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RacineComponent } from '../list/racine.component';
import { RacineDetailComponent } from '../detail/racine-detail.component';
import { RacineUpdateComponent } from '../update/racine-update.component';
import { RacineRoutingResolveService } from './racine-routing-resolve.service';

const racineRoute: Routes = [
  {
    path: '',
    component: RacineComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RacineDetailComponent,
    resolve: {
      racine: RacineRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RacineUpdateComponent,
    resolve: {
      racine: RacineRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RacineUpdateComponent,
    resolve: {
      racine: RacineRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(racineRoute)],
  exports: [RouterModule],
})
export class RacineRoutingModule {}
