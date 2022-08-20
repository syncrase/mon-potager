import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TakhtajanComponent } from './list/takhtajan.component';
import { TakhtajanDetailComponent } from './detail/takhtajan-detail.component';
import { TakhtajanUpdateComponent } from './update/takhtajan-update.component';
import { TakhtajanDeleteDialogComponent } from './delete/takhtajan-delete-dialog.component';
import { TakhtajanRoutingModule } from './route/takhtajan-routing.module';

@NgModule({
  imports: [SharedModule, TakhtajanRoutingModule],
  declarations: [TakhtajanComponent, TakhtajanDetailComponent, TakhtajanUpdateComponent, TakhtajanDeleteDialogComponent],
  entryComponents: [TakhtajanDeleteDialogComponent],
})
export class TakhtajanModule {}
