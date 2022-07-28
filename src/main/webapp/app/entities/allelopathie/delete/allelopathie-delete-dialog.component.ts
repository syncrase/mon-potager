import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAllelopathie } from '../allelopathie.model';
import { AllelopathieService } from '../service/allelopathie.service';

@Component({
  templateUrl: './allelopathie-delete-dialog.component.html',
})
export class AllelopathieDeleteDialogComponent {
  allelopathie?: IAllelopathie;

  constructor(protected allelopathieService: AllelopathieService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.allelopathieService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
