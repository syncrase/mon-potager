import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access.service';
import {ClassificationComponent} from '../list/classification.component';
import {ClassificationDetailComponent} from '../detail/classification-detail.component';
import {ClassificationUpdateComponent} from '../update/classification-update.component';
import {ClassificationRoutingResolveService} from './classification-routing-resolve.service';

const classificationRoute: Routes = [
  {
    path: '',
    component: ClassificationComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ClassificationDetailComponent,
    resolve: {
      classification: ClassificationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ClassificationUpdateComponent,
    resolve: {
      classification: ClassificationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ClassificationUpdateComponent,
    resolve: {
      classification: ClassificationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(classificationRoute)],
  exports: [RouterModule],
})
export class ClassificationRoutingModule {}
