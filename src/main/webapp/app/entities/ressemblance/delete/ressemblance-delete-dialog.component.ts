import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRessemblance } from '../ressemblance.model';
import { RessemblanceService } from '../service/ressemblance.service';

@Component({
  templateUrl: './ressemblance-delete-dialog.component.html',
})
export class RessemblanceDeleteDialogComponent {
  ressemblance?: IRessemblance;

  constructor(protected ressemblanceService: RessemblanceService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.ressemblanceService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
