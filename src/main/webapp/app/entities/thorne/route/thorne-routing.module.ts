import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ThorneComponent } from '../list/thorne.component';
import { ThorneDetailComponent } from '../detail/thorne-detail.component';
import { ThorneUpdateComponent } from '../update/thorne-update.component';
import { ThorneRoutingResolveService } from './thorne-routing-resolve.service';

const thorneRoute: Routes = [
  {
    path: '',
    component: ThorneComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ThorneDetailComponent,
    resolve: {
      thorne: ThorneRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ThorneUpdateComponent,
    resolve: {
      thorne: ThorneRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ThorneUpdateComponent,
    resolve: {
      thorne: ThorneRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(thorneRoute)],
  exports: [RouterModule],
})
export class ThorneRoutingModule {}
