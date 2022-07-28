import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CycleDeVieComponent } from '../list/cycle-de-vie.component';
import { CycleDeVieDetailComponent } from '../detail/cycle-de-vie-detail.component';
import { CycleDeVieUpdateComponent } from '../update/cycle-de-vie-update.component';
import { CycleDeVieRoutingResolveService } from './cycle-de-vie-routing-resolve.service';

const cycleDeVieRoute: Routes = [
  {
    path: '',
    component: CycleDeVieComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CycleDeVieDetailComponent,
    resolve: {
      cycleDeVie: CycleDeVieRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CycleDeVieUpdateComponent,
    resolve: {
      cycleDeVie: CycleDeVieRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CycleDeVieUpdateComponent,
    resolve: {
      cycleDeVie: CycleDeVieRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(cycleDeVieRoute)],
  exports: [RouterModule],
})
export class CycleDeVieRoutingModule {}
