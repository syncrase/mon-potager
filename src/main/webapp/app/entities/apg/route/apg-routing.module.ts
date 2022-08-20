import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access.service';
import {APGComponent} from '../list/apg.component';
import {APGDetailComponent} from '../detail/apg-detail.component';
import {APGUpdateComponent} from '../update/apg-update.component';
import {APGRoutingResolveService} from './apg-routing-resolve.service';

const aPGRoute: Routes = [
  {
    path: '',
    component: APGComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: APGDetailComponent,
    resolve: {
      aPG: APGRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: APGUpdateComponent,
    resolve: {
      aPG: APGRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: APGUpdateComponent,
    resolve: {
      aPG: APGRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(aPGRoute)],
  exports: [RouterModule],
})
export class APGRoutingModule {}
