import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SemisComponent } from '../list/semis.component';
import { SemisDetailComponent } from '../detail/semis-detail.component';
import { SemisUpdateComponent } from '../update/semis-update.component';
import { SemisRoutingResolveService } from './semis-routing-resolve.service';

const semisRoute: Routes = [
  {
    path: '',
    component: SemisComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SemisDetailComponent,
    resolve: {
      semis: SemisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SemisUpdateComponent,
    resolve: {
      semis: SemisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SemisUpdateComponent,
    resolve: {
      semis: SemisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(semisRoute)],
  exports: [RouterModule],
})
export class SemisRoutingModule {}
