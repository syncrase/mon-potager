import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TakhtajanComponent } from '../list/takhtajan.component';
import { TakhtajanDetailComponent } from '../detail/takhtajan-detail.component';
import { TakhtajanUpdateComponent } from '../update/takhtajan-update.component';
import { TakhtajanRoutingResolveService } from './takhtajan-routing-resolve.service';

const takhtajanRoute: Routes = [
  {
    path: '',
    component: TakhtajanComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TakhtajanDetailComponent,
    resolve: {
      takhtajan: TakhtajanRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TakhtajanUpdateComponent,
    resolve: {
      takhtajan: TakhtajanRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TakhtajanUpdateComponent,
    resolve: {
      takhtajan: TakhtajanRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(takhtajanRoute)],
  exports: [RouterModule],
})
export class TakhtajanRoutingModule {}
