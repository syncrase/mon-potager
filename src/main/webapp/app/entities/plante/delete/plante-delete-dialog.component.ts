import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPlante } from '../plante.model';
import { PlanteService } from '../service/plante.service';

@Component({
  templateUrl: './plante-delete-dialog.component.html',
})
export class PlanteDeleteDialogComponent {
  plante?: IPlante;

  constructor(protected planteService: PlanteService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.planteService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
