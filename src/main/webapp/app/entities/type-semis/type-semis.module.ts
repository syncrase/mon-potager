import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TypeSemisComponent } from './list/type-semis.component';
import { TypeSemisDetailComponent } from './detail/type-semis-detail.component';
import { TypeSemisUpdateComponent } from './update/type-semis-update.component';
import { TypeSemisDeleteDialogComponent } from './delete/type-semis-delete-dialog.component';
import { TypeSemisRoutingModule } from './route/type-semis-routing.module';

@NgModule({
  imports: [SharedModule, TypeSemisRoutingModule],
  declarations: [TypeSemisComponent, TypeSemisDetailComponent, TypeSemisUpdateComponent, TypeSemisDeleteDialogComponent],
  entryComponents: [TypeSemisDeleteDialogComponent],
})
export class TypeSemisModule {}
