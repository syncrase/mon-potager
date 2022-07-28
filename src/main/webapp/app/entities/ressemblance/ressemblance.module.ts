import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RessemblanceComponent } from './list/ressemblance.component';
import { RessemblanceDetailComponent } from './detail/ressemblance-detail.component';
import { RessemblanceUpdateComponent } from './update/ressemblance-update.component';
import { RessemblanceDeleteDialogComponent } from './delete/ressemblance-delete-dialog.component';
import { RessemblanceRoutingModule } from './route/ressemblance-routing.module';

@NgModule({
  imports: [SharedModule, RessemblanceRoutingModule],
  declarations: [RessemblanceComponent, RessemblanceDetailComponent, RessemblanceUpdateComponent, RessemblanceDeleteDialogComponent],
  entryComponents: [RessemblanceDeleteDialogComponent],
})
export class RessemblanceModule {}
