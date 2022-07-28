import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MoisComponent } from './list/mois.component';
import { MoisDetailComponent } from './detail/mois-detail.component';
import { MoisUpdateComponent } from './update/mois-update.component';
import { MoisDeleteDialogComponent } from './delete/mois-delete-dialog.component';
import { MoisRoutingModule } from './route/mois-routing.module';

@NgModule({
  imports: [SharedModule, MoisRoutingModule],
  declarations: [MoisComponent, MoisDetailComponent, MoisUpdateComponent, MoisDeleteDialogComponent],
  entryComponents: [MoisDeleteDialogComponent],
})
export class MoisModule {}
