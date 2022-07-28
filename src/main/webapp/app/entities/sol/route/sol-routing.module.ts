import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SolComponent } from '../list/sol.component';
import { SolDetailComponent } from '../detail/sol-detail.component';
import { SolUpdateComponent } from '../update/sol-update.component';
import { SolRoutingResolveService } from './sol-routing-resolve.service';

const solRoute: Routes = [
  {
    path: '',
    component: SolComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SolDetailComponent,
    resolve: {
      sol: SolRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SolUpdateComponent,
    resolve: {
      sol: SolRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SolUpdateComponent,
    resolve: {
      sol: SolRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(solRoute)],
  exports: [RouterModule],
})
export class SolRoutingModule {}
