import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UrlComponent } from '../list/url.component';
import { UrlDetailComponent } from '../detail/url-detail.component';
import { UrlUpdateComponent } from '../update/url-update.component';
import { UrlRoutingResolveService } from './url-routing-resolve.service';

const urlRoute: Routes = [
  {
    path: '',
    component: UrlComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UrlDetailComponent,
    resolve: {
      url: UrlRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UrlUpdateComponent,
    resolve: {
      url: UrlRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UrlUpdateComponent,
    resolve: {
      url: UrlRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(urlRoute)],
  exports: [RouterModule],
})
export class UrlRoutingModule {}
