import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NomVernaculaireComponent } from '../list/nom-vernaculaire.component';
import { NomVernaculaireDetailComponent } from '../detail/nom-vernaculaire-detail.component';
import { NomVernaculaireUpdateComponent } from '../update/nom-vernaculaire-update.component';
import { NomVernaculaireRoutingResolveService } from './nom-vernaculaire-routing-resolve.service';

const nomVernaculaireRoute: Routes = [
  {
    path: '',
    component: NomVernaculaireComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NomVernaculaireDetailComponent,
    resolve: {
      nomVernaculaire: NomVernaculaireRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NomVernaculaireUpdateComponent,
    resolve: {
      nomVernaculaire: NomVernaculaireRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NomVernaculaireUpdateComponent,
    resolve: {
      nomVernaculaire: NomVernaculaireRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(nomVernaculaireRoute)],
  exports: [RouterModule],
})
export class NomVernaculaireRoutingModule {}
