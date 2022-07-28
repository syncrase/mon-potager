import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SemisComponent } from './list/semis.component';
import { SemisDetailComponent } from './detail/semis-detail.component';
import { SemisUpdateComponent } from './update/semis-update.component';
import { SemisDeleteDialogComponent } from './delete/semis-delete-dialog.component';
import { SemisRoutingModule } from './route/semis-routing.module';

@NgModule({
  imports: [SharedModule, SemisRoutingModule],
  declarations: [SemisComponent, SemisDetailComponent, SemisUpdateComponent, SemisDeleteDialogComponent],
  entryComponents: [SemisDeleteDialogComponent],
})
export class SemisModule {}
