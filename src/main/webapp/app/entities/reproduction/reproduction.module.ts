import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ReproductionComponent } from './list/reproduction.component';
import { ReproductionDetailComponent } from './detail/reproduction-detail.component';
import { ReproductionUpdateComponent } from './update/reproduction-update.component';
import { ReproductionDeleteDialogComponent } from './delete/reproduction-delete-dialog.component';
import { ReproductionRoutingModule } from './route/reproduction-routing.module';

@NgModule({
  imports: [SharedModule, ReproductionRoutingModule],
  declarations: [ReproductionComponent, ReproductionDetailComponent, ReproductionUpdateComponent, ReproductionDeleteDialogComponent],
  entryComponents: [ReproductionDeleteDialogComponent],
})
export class ReproductionModule {}
