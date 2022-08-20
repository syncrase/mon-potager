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
        path: 'reference',
        data: { pageTitle: 'monolithApp.reference.home.title' },
        loadChildren: () => import('./reference/reference.module').then(m => m.ReferenceModule),
      },
      {
        path: 'url',
        data: { pageTitle: 'monolithApp.url.home.title' },
        loadChildren: () => import('./url/url.module').then(m => m.UrlModule),
      },
      {
        path: 'classification',
        data: { pageTitle: 'monolithApp.classification.home.title' },
        loadChildren: () => import('./classification/classification.module').then(m => m.ClassificationModule),
      },
      {
        path: 'cronquist-rank',
        data: { pageTitle: 'monolithApp.cronquistRank.home.title' },
        loadChildren: () => import('./cronquist-rank/cronquist-rank.module').then(m => m.CronquistRankModule),
      },
      {
        path: 'apg',
        data: { pageTitle: 'monolithApp.aPG.home.title' },
        loadChildren: () => import('./apg/apg.module').then(m => m.APGModule),
      },
      {
        path: 'bentham-hooker',
        data: { pageTitle: 'monolithApp.benthamHooker.home.title' },
        loadChildren: () => import('./bentham-hooker/bentham-hooker.module').then(m => m.BenthamHookerModule),
      },
      {
        path: 'wettstein',
        data: { pageTitle: 'monolithApp.wettstein.home.title' },
        loadChildren: () => import('./wettstein/wettstein.module').then(m => m.WettsteinModule),
      },
      {
        path: 'thorne',
        data: { pageTitle: 'monolithApp.thorne.home.title' },
        loadChildren: () => import('./thorne/thorne.module').then(m => m.ThorneModule),
      },
      {
        path: 'takhtajan',
        data: { pageTitle: 'monolithApp.takhtajan.home.title' },
        loadChildren: () => import('./takhtajan/takhtajan.module').then(m => m.TakhtajanModule),
      },
      {
        path: 'engler',
        data: { pageTitle: 'monolithApp.engler.home.title' },
        loadChildren: () => import('./engler/engler.module').then(m => m.EnglerModule),
      },
      {
        path: 'candolle',
        data: { pageTitle: 'monolithApp.candolle.home.title' },
        loadChildren: () => import('./candolle/candolle.module').then(m => m.CandolleModule),
      },
      {
        path: 'dahlgren',
        data: { pageTitle: 'monolithApp.dahlgren.home.title' },
        loadChildren: () => import('./dahlgren/dahlgren.module').then(m => m.DahlgrenModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
