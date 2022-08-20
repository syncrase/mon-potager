import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access.service';
import {DahlgrenComponent} from '../list/dahlgren.component';
import {DahlgrenDetailComponent} from '../detail/dahlgren-detail.component';
import {DahlgrenUpdateComponent} from '../update/dahlgren-update.component';
import {DahlgrenRoutingResolveService} from './dahlgren-routing-resolve.service';

const dahlgrenRoute: Routes = [
  {
    path: '',
    component: DahlgrenComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DahlgrenDetailComponent,
    resolve: {
      dahlgren: DahlgrenRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DahlgrenUpdateComponent,
    resolve: {
      dahlgren: DahlgrenRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DahlgrenUpdateComponent,
    resolve: {
      dahlgren: DahlgrenRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(dahlgrenRoute)],
  exports: [RouterModule],
})
export class DahlgrenRoutingModule {}
