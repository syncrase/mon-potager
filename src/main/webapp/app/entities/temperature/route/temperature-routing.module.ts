import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TemperatureComponent } from '../list/temperature.component';
import { TemperatureDetailComponent } from '../detail/temperature-detail.component';
import { TemperatureUpdateComponent } from '../update/temperature-update.component';
import { TemperatureRoutingResolveService } from './temperature-routing-resolve.service';

const temperatureRoute: Routes = [
  {
    path: '',
    component: TemperatureComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TemperatureDetailComponent,
    resolve: {
      temperature: TemperatureRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TemperatureUpdateComponent,
    resolve: {
      temperature: TemperatureRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TemperatureUpdateComponent,
    resolve: {
      temperature: TemperatureRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(temperatureRoute)],
  exports: [RouterModule],
})
export class TemperatureRoutingModule {}
