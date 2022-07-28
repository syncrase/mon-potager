import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RacineComponent } from './list/racine.component';
import { RacineDetailComponent } from './detail/racine-detail.component';
import { RacineUpdateComponent } from './update/racine-update.component';
import { RacineDeleteDialogComponent } from './delete/racine-delete-dialog.component';
import { RacineRoutingModule } from './route/racine-routing.module';

@NgModule({
  imports: [SharedModule, RacineRoutingModule],
  declarations: [RacineComponent, RacineDetailComponent, RacineUpdateComponent, RacineDeleteDialogComponent],
  entryComponents: [RacineDeleteDialogComponent],
})
export class RacineModule {}
