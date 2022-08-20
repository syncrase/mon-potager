import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {APGComponent} from './list/apg.component';
import {APGDetailComponent} from './detail/apg-detail.component';
import {APGUpdateComponent} from './update/apg-update.component';
import {APGDeleteDialogComponent} from './delete/apg-delete-dialog.component';
import {APGRoutingModule} from './route/apg-routing.module';

@NgModule({
  imports: [SharedModule, APGRoutingModule],
  declarations: [APGComponent, APGDetailComponent, APGUpdateComponent, APGDeleteDialogComponent],
  entryComponents: [APGDeleteDialogComponent],
})
export class APGModule {}
