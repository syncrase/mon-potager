import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISol } from '../sol.model';
import { SolService } from '../service/sol.service';

@Component({
  templateUrl: './sol-delete-dialog.component.html',
})
export class SolDeleteDialogComponent {
  sol?: ISol;

  constructor(protected solService: SolService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.solService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
