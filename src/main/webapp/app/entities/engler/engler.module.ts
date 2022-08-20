import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EnglerComponent } from './list/engler.component';
import { EnglerDetailComponent } from './detail/engler-detail.component';
import { EnglerUpdateComponent } from './update/engler-update.component';
import { EnglerDeleteDialogComponent } from './delete/engler-delete-dialog.component';
import { EnglerRoutingModule } from './route/engler-routing.module';

@NgModule({
  imports: [SharedModule, EnglerRoutingModule],
  declarations: [EnglerComponent, EnglerDetailComponent, EnglerUpdateComponent, EnglerDeleteDialogComponent],
  entryComponents: [EnglerDeleteDialogComponent],
})
export class EnglerModule {}
