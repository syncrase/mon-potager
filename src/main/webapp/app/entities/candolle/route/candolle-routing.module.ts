import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access.service';
import {CandolleComponent} from '../list/candolle.component';
import {CandolleDetailComponent} from '../detail/candolle-detail.component';
import {CandolleUpdateComponent} from '../update/candolle-update.component';
import {CandolleRoutingResolveService} from './candolle-routing-resolve.service';

const candolleRoute: Routes = [
  {
    path: '',
    component: CandolleComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CandolleDetailComponent,
    resolve: {
      candolle: CandolleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CandolleUpdateComponent,
    resolve: {
      candolle: CandolleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CandolleUpdateComponent,
    resolve: {
      candolle: CandolleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(candolleRoute)],
  exports: [RouterModule],
})
export class CandolleRoutingModule {}
