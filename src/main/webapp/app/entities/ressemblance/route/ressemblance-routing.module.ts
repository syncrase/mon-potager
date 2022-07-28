import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RessemblanceComponent } from '../list/ressemblance.component';
import { RessemblanceDetailComponent } from '../detail/ressemblance-detail.component';
import { RessemblanceUpdateComponent } from '../update/ressemblance-update.component';
import { RessemblanceRoutingResolveService } from './ressemblance-routing-resolve.service';

const ressemblanceRoute: Routes = [
  {
    path: '',
    component: RessemblanceComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RessemblanceDetailComponent,
    resolve: {
      ressemblance: RessemblanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RessemblanceUpdateComponent,
    resolve: {
      ressemblance: RessemblanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RessemblanceUpdateComponent,
    resolve: {
      ressemblance: RessemblanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(ressemblanceRoute)],
  exports: [RouterModule],
})
export class RessemblanceRoutingModule {}
