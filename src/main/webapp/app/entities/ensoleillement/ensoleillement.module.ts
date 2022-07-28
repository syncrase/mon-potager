import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EnsoleillementComponent } from './list/ensoleillement.component';
import { EnsoleillementDetailComponent } from './detail/ensoleillement-detail.component';
import { EnsoleillementUpdateComponent } from './update/ensoleillement-update.component';
import { EnsoleillementDeleteDialogComponent } from './delete/ensoleillement-delete-dialog.component';
import { EnsoleillementRoutingModule } from './route/ensoleillement-routing.module';

@NgModule({
  imports: [SharedModule, EnsoleillementRoutingModule],
  declarations: [
    EnsoleillementComponent,
    EnsoleillementDetailComponent,
    EnsoleillementUpdateComponent,
    EnsoleillementDeleteDialogComponent,
  ],
  entryComponents: [EnsoleillementDeleteDialogComponent],
})
export class EnsoleillementModule {}
