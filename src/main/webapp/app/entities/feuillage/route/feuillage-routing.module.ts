import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FeuillageComponent } from '../list/feuillage.component';
import { FeuillageDetailComponent } from '../detail/feuillage-detail.component';
import { FeuillageUpdateComponent } from '../update/feuillage-update.component';
import { FeuillageRoutingResolveService } from './feuillage-routing-resolve.service';

const feuillageRoute: Routes = [
  {
    path: '',
    component: FeuillageComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FeuillageDetailComponent,
    resolve: {
      feuillage: FeuillageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FeuillageUpdateComponent,
    resolve: {
      feuillage: FeuillageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FeuillageUpdateComponent,
    resolve: {
      feuillage: FeuillageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(feuillageRoute)],
  exports: [RouterModule],
})
export class FeuillageRoutingModule {}
