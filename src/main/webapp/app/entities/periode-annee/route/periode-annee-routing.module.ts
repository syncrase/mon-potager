import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PeriodeAnneeComponent } from '../list/periode-annee.component';
import { PeriodeAnneeDetailComponent } from '../detail/periode-annee-detail.component';
import { PeriodeAnneeUpdateComponent } from '../update/periode-annee-update.component';
import { PeriodeAnneeRoutingResolveService } from './periode-annee-routing-resolve.service';

const periodeAnneeRoute: Routes = [
  {
    path: '',
    component: PeriodeAnneeComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PeriodeAnneeDetailComponent,
    resolve: {
      periodeAnnee: PeriodeAnneeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PeriodeAnneeUpdateComponent,
    resolve: {
      periodeAnnee: PeriodeAnneeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PeriodeAnneeUpdateComponent,
    resolve: {
      periodeAnnee: PeriodeAnneeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(periodeAnneeRoute)],
  exports: [RouterModule],
})
export class PeriodeAnneeRoutingModule {}
