import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEnsoleillement } from '../ensoleillement.model';
import { EnsoleillementService } from '../service/ensoleillement.service';

@Component({
  templateUrl: './ensoleillement-delete-dialog.component.html',
})
export class EnsoleillementDeleteDialogComponent {
  ensoleillement?: IEnsoleillement;

  constructor(protected ensoleillementService: EnsoleillementService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.ensoleillementService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
