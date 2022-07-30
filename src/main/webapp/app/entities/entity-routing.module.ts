import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'plante',
        data: { pageTitle: 'monolithApp.plante.home.title' },
        loadChildren: () => import('./plante/plante.module').then(m => m.PlanteModule),
      },
      {
        path: 'nom-vernaculaire',
        data: { pageTitle: 'monolithApp.nomVernaculaire.home.title' },
        loadChildren: () => import('./nom-vernaculaire/nom-vernaculaire.module').then(m => m.NomVernaculaireModule),
      },
      {
        path: 'cronquist-rank',
        data: { pageTitle: 'monolithApp.cronquistRank.home.title' },
        loadChildren: () => import('./cronquist-rank/cronquist-rank.module').then(m => m.CronquistRankModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
