import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access.service';
import {BenthamHookerComponent} from '../list/bentham-hooker.component';
import {BenthamHookerDetailComponent} from '../detail/bentham-hooker-detail.component';
import {BenthamHookerUpdateComponent} from '../update/bentham-hooker-update.component';
import {BenthamHookerRoutingResolveService} from './bentham-hooker-routing-resolve.service';

const benthamHookerRoute: Routes = [
  {
    path: '',
    component: BenthamHookerComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BenthamHookerDetailComponent,
    resolve: {
      benthamHooker: BenthamHookerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BenthamHookerUpdateComponent,
    resolve: {
      benthamHooker: BenthamHookerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BenthamHookerUpdateComponent,
    resolve: {
      benthamHooker: BenthamHookerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(benthamHookerRoute)],
  exports: [RouterModule],
})
export class BenthamHookerRoutingModule {}
