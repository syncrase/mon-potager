import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ReferenceComponent } from '../list/reference.component';
import { ReferenceDetailComponent } from '../detail/reference-detail.component';
import { ReferenceUpdateComponent } from '../update/reference-update.component';
import { ReferenceRoutingResolveService } from './reference-routing-resolve.service';

const referenceRoute: Routes = [
  {
    path: '',
    component: ReferenceComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ReferenceDetailComponent,
    resolve: {
      reference: ReferenceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ReferenceUpdateComponent,
    resolve: {
      reference: ReferenceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ReferenceUpdateComponent,
    resolve: {
      reference: ReferenceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(referenceRoute)],
  exports: [RouterModule],
})
export class ReferenceRoutingModule {}
