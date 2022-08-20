import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {BenthamHookerComponent} from './list/bentham-hooker.component';
import {BenthamHookerDetailComponent} from './detail/bentham-hooker-detail.component';
import {BenthamHookerUpdateComponent} from './update/bentham-hooker-update.component';
import {BenthamHookerDeleteDialogComponent} from './delete/bentham-hooker-delete-dialog.component';
import {BenthamHookerRoutingModule} from './route/bentham-hooker-routing.module';

@NgModule({
  imports: [SharedModule, BenthamHookerRoutingModule],
  declarations: [BenthamHookerComponent, BenthamHookerDetailComponent, BenthamHookerUpdateComponent, BenthamHookerDeleteDialogComponent],
  entryComponents: [BenthamHookerDeleteDialogComponent],
})
export class BenthamHookerModule {}
