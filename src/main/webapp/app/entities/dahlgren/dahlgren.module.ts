import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {DahlgrenComponent} from './list/dahlgren.component';
import {DahlgrenDetailComponent} from './detail/dahlgren-detail.component';
import {DahlgrenUpdateComponent} from './update/dahlgren-update.component';
import {DahlgrenDeleteDialogComponent} from './delete/dahlgren-delete-dialog.component';
import {DahlgrenRoutingModule} from './route/dahlgren-routing.module';

@NgModule({
  imports: [SharedModule, DahlgrenRoutingModule],
  declarations: [DahlgrenComponent, DahlgrenDetailComponent, DahlgrenUpdateComponent, DahlgrenDeleteDialogComponent],
  entryComponents: [DahlgrenDeleteDialogComponent],
})
export class DahlgrenModule {}
