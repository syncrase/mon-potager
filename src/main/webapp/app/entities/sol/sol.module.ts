import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SolComponent } from './list/sol.component';
import { SolDetailComponent } from './detail/sol-detail.component';
import { SolUpdateComponent } from './update/sol-update.component';
import { SolDeleteDialogComponent } from './delete/sol-delete-dialog.component';
import { SolRoutingModule } from './route/sol-routing.module';

@NgModule({
  imports: [SharedModule, SolRoutingModule],
  declarations: [SolComponent, SolDetailComponent, SolUpdateComponent, SolDeleteDialogComponent],
  entryComponents: [SolDeleteDialogComponent],
})
export class SolModule {}
