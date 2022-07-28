import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PeriodeAnneeComponent } from './list/periode-annee.component';
import { PeriodeAnneeDetailComponent } from './detail/periode-annee-detail.component';
import { PeriodeAnneeUpdateComponent } from './update/periode-annee-update.component';
import { PeriodeAnneeDeleteDialogComponent } from './delete/periode-annee-delete-dialog.component';
import { PeriodeAnneeRoutingModule } from './route/periode-annee-routing.module';

@NgModule({
  imports: [SharedModule, PeriodeAnneeRoutingModule],
  declarations: [PeriodeAnneeComponent, PeriodeAnneeDetailComponent, PeriodeAnneeUpdateComponent, PeriodeAnneeDeleteDialogComponent],
  entryComponents: [PeriodeAnneeDeleteDialogComponent],
})
export class PeriodeAnneeModule {}
