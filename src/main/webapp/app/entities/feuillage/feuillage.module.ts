import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FeuillageComponent } from './list/feuillage.component';
import { FeuillageDetailComponent } from './detail/feuillage-detail.component';
import { FeuillageUpdateComponent } from './update/feuillage-update.component';
import { FeuillageDeleteDialogComponent } from './delete/feuillage-delete-dialog.component';
import { FeuillageRoutingModule } from './route/feuillage-routing.module';

@NgModule({
  imports: [SharedModule, FeuillageRoutingModule],
  declarations: [FeuillageComponent, FeuillageDetailComponent, FeuillageUpdateComponent, FeuillageDeleteDialogComponent],
  entryComponents: [FeuillageDeleteDialogComponent],
})
export class FeuillageModule {}
