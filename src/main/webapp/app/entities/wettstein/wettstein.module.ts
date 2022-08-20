import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { WettsteinComponent } from './list/wettstein.component';
import { WettsteinDetailComponent } from './detail/wettstein-detail.component';
import { WettsteinUpdateComponent } from './update/wettstein-update.component';
import { WettsteinDeleteDialogComponent } from './delete/wettstein-delete-dialog.component';
import { WettsteinRoutingModule } from './route/wettstein-routing.module';

@NgModule({
  imports: [SharedModule, WettsteinRoutingModule],
  declarations: [WettsteinComponent, WettsteinDetailComponent, WettsteinUpdateComponent, WettsteinDeleteDialogComponent],
  entryComponents: [WettsteinDeleteDialogComponent],
})
export class WettsteinModule {}
