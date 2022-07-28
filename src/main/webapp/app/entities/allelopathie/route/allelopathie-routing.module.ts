import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AllelopathieComponent } from '../list/allelopathie.component';
import { AllelopathieDetailComponent } from '../detail/allelopathie-detail.component';
import { AllelopathieUpdateComponent } from '../update/allelopathie-update.component';
import { AllelopathieRoutingResolveService } from './allelopathie-routing-resolve.service';

const allelopathieRoute: Routes = [
  {
    path: '',
    component: AllelopathieComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AllelopathieDetailComponent,
    resolve: {
      allelopathie: AllelopathieRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AllelopathieUpdateComponent,
    resolve: {
      allelopathie: AllelopathieRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AllelopathieUpdateComponent,
    resolve: {
      allelopathie: AllelopathieRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(allelopathieRoute)],
  exports: [RouterModule],
})
export class AllelopathieRoutingModule {}
