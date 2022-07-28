import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPeriodeAnnee } from '../periode-annee.model';
import { PeriodeAnneeService } from '../service/periode-annee.service';

@Component({
  templateUrl: './periode-annee-delete-dialog.component.html',
})
export class PeriodeAnneeDeleteDialogComponent {
  periodeAnnee?: IPeriodeAnnee;

  constructor(protected periodeAnneeService: PeriodeAnneeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.periodeAnneeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
