import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEngler } from '../engler.model';
import { EnglerService } from '../service/engler.service';

@Component({
  templateUrl: './engler-delete-dialog.component.html',
})
export class EnglerDeleteDialogComponent {
  engler?: IEngler;

  constructor(protected englerService: EnglerService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.englerService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
