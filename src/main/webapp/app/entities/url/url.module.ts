import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UrlComponent } from './list/url.component';
import { UrlDetailComponent } from './detail/url-detail.component';
import { UrlUpdateComponent } from './update/url-update.component';
import { UrlDeleteDialogComponent } from './delete/url-delete-dialog.component';
import { UrlRoutingModule } from './route/url-routing.module';

@NgModule({
  imports: [SharedModule, UrlRoutingModule],
  declarations: [UrlComponent, UrlDetailComponent, UrlUpdateComponent, UrlDeleteDialogComponent],
  entryComponents: [UrlDeleteDialogComponent],
})
export class UrlModule {}
