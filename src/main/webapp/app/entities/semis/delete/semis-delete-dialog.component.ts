import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISemis } from '../semis.model';
import { SemisService } from '../service/semis.service';

@Component({
  templateUrl: './semis-delete-dialog.component.html',
})
export class SemisDeleteDialogComponent {
  semis?: ISemis;

  constructor(protected semisService: SemisService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.semisService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
