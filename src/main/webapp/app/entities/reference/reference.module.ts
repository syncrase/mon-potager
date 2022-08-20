import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ReferenceComponent } from './list/reference.component';
import { ReferenceDetailComponent } from './detail/reference-detail.component';
import { ReferenceUpdateComponent } from './update/reference-update.component';
import { ReferenceDeleteDialogComponent } from './delete/reference-delete-dialog.component';
import { ReferenceRoutingModule } from './route/reference-routing.module';

@NgModule({
  imports: [SharedModule, ReferenceRoutingModule],
  declarations: [ReferenceComponent, ReferenceDetailComponent, ReferenceUpdateComponent, ReferenceDeleteDialogComponent],
  entryComponents: [ReferenceDeleteDialogComponent],
})
export class ReferenceModule {}
