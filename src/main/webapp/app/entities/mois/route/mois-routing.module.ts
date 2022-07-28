import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MoisComponent } from '../list/mois.component';
import { MoisDetailComponent } from '../detail/mois-detail.component';
import { MoisUpdateComponent } from '../update/mois-update.component';
import { MoisRoutingResolveService } from './mois-routing-resolve.service';

const moisRoute: Routes = [
  {
    path: '',
    component: MoisComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MoisDetailComponent,
    resolve: {
      mois: MoisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MoisUpdateComponent,
    resolve: {
      mois: MoisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MoisUpdateComponent,
    resolve: {
      mois: MoisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(moisRoute)],
  exports: [RouterModule],
})
export class MoisRoutingModule {}
