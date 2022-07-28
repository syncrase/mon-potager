import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { StrateComponent } from './list/strate.component';
import { StrateDetailComponent } from './detail/strate-detail.component';
import { StrateUpdateComponent } from './update/strate-update.component';
import { StrateDeleteDialogComponent } from './delete/strate-delete-dialog.component';
import { StrateRoutingModule } from './route/strate-routing.module';

@NgModule({
  imports: [SharedModule, StrateRoutingModule],
  declarations: [StrateComponent, StrateDetailComponent, StrateUpdateComponent, StrateDeleteDialogComponent],
  entryComponents: [StrateDeleteDialogComponent],
})
export class StrateModule {}
