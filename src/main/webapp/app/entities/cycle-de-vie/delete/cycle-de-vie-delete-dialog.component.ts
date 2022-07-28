import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICycleDeVie } from '../cycle-de-vie.model';
import { CycleDeVieService } from '../service/cycle-de-vie.service';

@Component({
  templateUrl: './cycle-de-vie-delete-dialog.component.html',
})
export class CycleDeVieDeleteDialogComponent {
  cycleDeVie?: ICycleDeVie;

  constructor(protected cycleDeVieService: CycleDeVieService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cycleDeVieService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
