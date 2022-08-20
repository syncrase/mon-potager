import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ThorneComponent } from './list/thorne.component';
import { ThorneDetailComponent } from './detail/thorne-detail.component';
import { ThorneUpdateComponent } from './update/thorne-update.component';
import { ThorneDeleteDialogComponent } from './delete/thorne-delete-dialog.component';
import { ThorneRoutingModule } from './route/thorne-routing.module';

@NgModule({
  imports: [SharedModule, ThorneRoutingModule],
  declarations: [ThorneComponent, ThorneDetailComponent, ThorneUpdateComponent, ThorneDeleteDialogComponent],
  entryComponents: [ThorneDeleteDialogComponent],
})
export class ThorneModule {}
