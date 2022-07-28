import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IReproduction } from '../reproduction.model';
import { ReproductionService } from '../service/reproduction.service';

@Component({
  templateUrl: './reproduction-delete-dialog.component.html',
})
export class ReproductionDeleteDialogComponent {
  reproduction?: IReproduction;

  constructor(protected reproductionService: ReproductionService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.reproductionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
