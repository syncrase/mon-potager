import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IGermination } from '../germination.model';
import { GerminationService } from '../service/germination.service';

@Component({
  templateUrl: './germination-delete-dialog.component.html',
})
export class GerminationDeleteDialogComponent {
  germination?: IGermination;

  constructor(protected germinationService: GerminationService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.germinationService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
