import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'add-plante',
        loadChildren: () => import('./add-plante/add-plante.module').then(m => m.AddPlanteModule),
        data: {
          pageTitle: 'addPlante.home.title',
        },
      },
    ]),
  ],
})
export class FeatureRoutingModule {}
