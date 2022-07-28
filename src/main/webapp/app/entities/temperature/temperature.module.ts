import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TemperatureComponent } from './list/temperature.component';
import { TemperatureDetailComponent } from './detail/temperature-detail.component';
import { TemperatureUpdateComponent } from './update/temperature-update.component';
import { TemperatureDeleteDialogComponent } from './delete/temperature-delete-dialog.component';
import { TemperatureRoutingModule } from './route/temperature-routing.module';

@NgModule({
  imports: [SharedModule, TemperatureRoutingModule],
  declarations: [TemperatureComponent, TemperatureDetailComponent, TemperatureUpdateComponent, TemperatureDeleteDialogComponent],
  entryComponents: [TemperatureDeleteDialogComponent],
})
export class TemperatureModule {}
