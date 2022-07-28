import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AllelopathieComponent } from './list/allelopathie.component';
import { AllelopathieDetailComponent } from './detail/allelopathie-detail.component';
import { AllelopathieUpdateComponent } from './update/allelopathie-update.component';
import { AllelopathieDeleteDialogComponent } from './delete/allelopathie-delete-dialog.component';
import { AllelopathieRoutingModule } from './route/allelopathie-routing.module';

@NgModule({
  imports: [SharedModule, AllelopathieRoutingModule],
  declarations: [AllelopathieComponent, AllelopathieDetailComponent, AllelopathieUpdateComponent, AllelopathieDeleteDialogComponent],
  entryComponents: [AllelopathieDeleteDialogComponent],
})
export class AllelopathieModule {}
