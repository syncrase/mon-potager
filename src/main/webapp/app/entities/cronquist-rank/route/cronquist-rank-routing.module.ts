import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access.service';
import {CronquistRankComponent} from '../list/cronquist-rank.component';
import {CronquistRankDetailComponent} from '../detail/cronquist-rank-detail.component';
import {CronquistRankUpdateComponent} from '../update/cronquist-rank-update.component';
import {CronquistRankRoutingResolveService} from './cronquist-rank-routing-resolve.service';

const cronquistRankRoute: Routes = [
  {
    path: '',
    component: CronquistRankComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CronquistRankDetailComponent,
    resolve: {
      cronquistRank: CronquistRankRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CronquistRankUpdateComponent,
    resolve: {
      cronquistRank: CronquistRankRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CronquistRankUpdateComponent,
    resolve: {
      cronquistRank: CronquistRankRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(cronquistRankRoute)],
  exports: [RouterModule],
})
export class CronquistRankRoutingModule {}
