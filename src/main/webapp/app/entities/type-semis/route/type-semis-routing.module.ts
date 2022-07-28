import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TypeSemisComponent } from '../list/type-semis.component';
import { TypeSemisDetailComponent } from '../detail/type-semis-detail.component';
import { TypeSemisUpdateComponent } from '../update/type-semis-update.component';
import { TypeSemisRoutingResolveService } from './type-semis-routing-resolve.service';

const typeSemisRoute: Routes = [
  {
    path: '',
    component: TypeSemisComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TypeSemisDetailComponent,
    resolve: {
      typeSemis: TypeSemisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TypeSemisUpdateComponent,
    resolve: {
      typeSemis: TypeSemisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TypeSemisUpdateComponent,
    resolve: {
      typeSemis: TypeSemisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(typeSemisRoute)],
  exports: [RouterModule],
})
export class TypeSemisRoutingModule {}
