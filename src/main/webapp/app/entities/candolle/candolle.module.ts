import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {CandolleComponent} from './list/candolle.component';
import {CandolleDetailComponent} from './detail/candolle-detail.component';
import {CandolleUpdateComponent} from './update/candolle-update.component';
import {CandolleDeleteDialogComponent} from './delete/candolle-delete-dialog.component';
import {CandolleRoutingModule} from './route/candolle-routing.module';

@NgModule({
  imports: [SharedModule, CandolleRoutingModule],
  declarations: [CandolleComponent, CandolleDetailComponent, CandolleUpdateComponent, CandolleDeleteDialogComponent],
  entryComponents: [CandolleDeleteDialogComponent],
})
export class CandolleModule {}
