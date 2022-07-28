import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'mois',
        data: { pageTitle: 'monolithApp.mois.home.title' },
        loadChildren: () => import('./mois/mois.module').then(m => m.MoisModule),
      },
      {
        path: 'periode-annee',
        data: { pageTitle: 'monolithApp.periodeAnnee.home.title' },
        loadChildren: () => import('./periode-annee/periode-annee.module').then(m => m.PeriodeAnneeModule),
      },
      {
        path: 'plante',
        data: { pageTitle: 'monolithApp.plante.home.title' },
        loadChildren: () => import('./plante/plante.module').then(m => m.PlanteModule),
      },
      {
        path: 'reproduction',
        data: { pageTitle: 'monolithApp.reproduction.home.title' },
        loadChildren: () => import('./reproduction/reproduction.module').then(m => m.ReproductionModule),
      },
      {
        path: 'sol',
        data: { pageTitle: 'monolithApp.sol.home.title' },
        loadChildren: () => import('./sol/sol.module').then(m => m.SolModule),
      },
      {
        path: 'nom-vernaculaire',
        data: { pageTitle: 'monolithApp.nomVernaculaire.home.title' },
        loadChildren: () => import('./nom-vernaculaire/nom-vernaculaire.module').then(m => m.NomVernaculaireModule),
      },
      {
        path: 'semis',
        data: { pageTitle: 'monolithApp.semis.home.title' },
        loadChildren: () => import('./semis/semis.module').then(m => m.SemisModule),
      },
      {
        path: 'type-semis',
        data: { pageTitle: 'monolithApp.typeSemis.home.title' },
        loadChildren: () => import('./type-semis/type-semis.module').then(m => m.TypeSemisModule),
      },
      {
        path: 'cycle-de-vie',
        data: { pageTitle: 'monolithApp.cycleDeVie.home.title' },
        loadChildren: () => import('./cycle-de-vie/cycle-de-vie.module').then(m => m.CycleDeVieModule),
      },
      {
        path: 'germination',
        data: { pageTitle: 'monolithApp.germination.home.title' },
        loadChildren: () => import('./germination/germination.module').then(m => m.GerminationModule),
      },
      {
        path: 'allelopathie',
        data: { pageTitle: 'monolithApp.allelopathie.home.title' },
        loadChildren: () => import('./allelopathie/allelopathie.module').then(m => m.AllelopathieModule),
      },
      {
        path: 'ressemblance',
        data: { pageTitle: 'monolithApp.ressemblance.home.title' },
        loadChildren: () => import('./ressemblance/ressemblance.module').then(m => m.RessemblanceModule),
      },
      {
        path: 'ensoleillement',
        data: { pageTitle: 'monolithApp.ensoleillement.home.title' },
        loadChildren: () => import('./ensoleillement/ensoleillement.module').then(m => m.EnsoleillementModule),
      },
      {
        path: 'temperature',
        data: { pageTitle: 'monolithApp.temperature.home.title' },
        loadChildren: () => import('./temperature/temperature.module').then(m => m.TemperatureModule),
      },
      {
        path: 'racine',
        data: { pageTitle: 'monolithApp.racine.home.title' },
        loadChildren: () => import('./racine/racine.module').then(m => m.RacineModule),
      },
      {
        path: 'strate',
        data: { pageTitle: 'monolithApp.strate.home.title' },
        loadChildren: () => import('./strate/strate.module').then(m => m.StrateModule),
      },
      {
        path: 'feuillage',
        data: { pageTitle: 'monolithApp.feuillage.home.title' },
        loadChildren: () => import('./feuillage/feuillage.module').then(m => m.FeuillageModule),
      },
      {
        path: 'cronquist-rank',
        data: { pageTitle: 'monolithApp.cronquistRank.home.title' },
        loadChildren: () => import('./cronquist-rank/cronquist-rank.module').then(m => m.CronquistRankModule),
      },
      {
        path: 'url',
        data: { pageTitle: 'monolithApp.url.home.title' },
        loadChildren: () => import('./url/url.module').then(m => m.UrlModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
