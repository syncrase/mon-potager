import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { GerminationComponent } from './list/germination.component';
import { GerminationDetailComponent } from './detail/germination-detail.component';
import { GerminationUpdateComponent } from './update/germination-update.component';
import { GerminationDeleteDialogComponent } from './delete/germination-delete-dialog.component';
import { GerminationRoutingModule } from './route/germination-routing.module';

@NgModule({
  imports: [SharedModule, GerminationRoutingModule],
  declarations: [GerminationComponent, GerminationDetailComponent, GerminationUpdateComponent, GerminationDeleteDialogComponent],
  entryComponents: [GerminationDeleteDialogComponent],
})
export class GerminationModule {}
