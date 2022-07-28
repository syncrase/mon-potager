import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMois } from '../mois.model';
import { MoisService } from '../service/mois.service';

@Component({
  templateUrl: './mois-delete-dialog.component.html',
})
export class MoisDeleteDialogComponent {
  mois?: IMois;

  constructor(protected moisService: MoisService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.moisService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
