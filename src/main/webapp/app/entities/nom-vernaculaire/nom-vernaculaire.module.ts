import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { NomVernaculaireComponent } from './list/nom-vernaculaire.component';
import { NomVernaculaireDetailComponent } from './detail/nom-vernaculaire-detail.component';
import { NomVernaculaireUpdateComponent } from './update/nom-vernaculaire-update.component';
import { NomVernaculaireDeleteDialogComponent } from './delete/nom-vernaculaire-delete-dialog.component';
import { NomVernaculaireRoutingModule } from './route/nom-vernaculaire-routing.module';

@NgModule({
  imports: [SharedModule, NomVernaculaireRoutingModule],
  declarations: [
    NomVernaculaireComponent,
    NomVernaculaireDetailComponent,
    NomVernaculaireUpdateComponent,
    NomVernaculaireDeleteDialogComponent,
  ],
  entryComponents: [NomVernaculaireDeleteDialogComponent],
})
export class NomVernaculaireModule {}
