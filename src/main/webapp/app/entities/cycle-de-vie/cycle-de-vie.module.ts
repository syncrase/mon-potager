import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CycleDeVieComponent } from './list/cycle-de-vie.component';
import { CycleDeVieDetailComponent } from './detail/cycle-de-vie-detail.component';
import { CycleDeVieUpdateComponent } from './update/cycle-de-vie-update.component';
import { CycleDeVieDeleteDialogComponent } from './delete/cycle-de-vie-delete-dialog.component';
import { CycleDeVieRoutingModule } from './route/cycle-de-vie-routing.module';

@NgModule({
  imports: [SharedModule, CycleDeVieRoutingModule],
  declarations: [CycleDeVieComponent, CycleDeVieDetailComponent, CycleDeVieUpdateComponent, CycleDeVieDeleteDialogComponent],
  entryComponents: [CycleDeVieDeleteDialogComponent],
})
export class CycleDeVieModule {}
