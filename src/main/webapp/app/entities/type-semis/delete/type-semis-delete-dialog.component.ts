import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITypeSemis } from '../type-semis.model';
import { TypeSemisService } from '../service/type-semis.service';

@Component({
  templateUrl: './type-semis-delete-dialog.component.html',
})
export class TypeSemisDeleteDialogComponent {
  typeSemis?: ITypeSemis;

  constructor(protected typeSemisService: TypeSemisService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.typeSemisService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
