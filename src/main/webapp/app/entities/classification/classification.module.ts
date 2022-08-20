import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {ClassificationComponent} from './list/classification.component';
import {ClassificationDetailComponent} from './detail/classification-detail.component';
import {ClassificationUpdateComponent} from './update/classification-update.component';
import {ClassificationDeleteDialogComponent} from './delete/classification-delete-dialog.component';
import {ClassificationRoutingModule} from './route/classification-routing.module';

@NgModule({
  imports: [SharedModule, ClassificationRoutingModule],
  declarations: [
    ClassificationComponent,
    ClassificationDetailComponent,
    ClassificationUpdateComponent,
    ClassificationDeleteDialogComponent,
  ],
  entryComponents: [ClassificationDeleteDialogComponent],
})
export class ClassificationModule {}
