import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IReference } from '../reference.model';
import { ReferenceService } from '../service/reference.service';

@Component({
  templateUrl: './reference-delete-dialog.component.html',
})
export class ReferenceDeleteDialogComponent {
  reference?: IReference;

  constructor(protected referenceService: ReferenceService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.referenceService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
