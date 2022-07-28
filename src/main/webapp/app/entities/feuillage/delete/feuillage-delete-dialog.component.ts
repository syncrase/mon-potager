import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFeuillage } from '../feuillage.model';
import { FeuillageService } from '../service/feuillage.service';

@Component({
  templateUrl: './feuillage-delete-dialog.component.html',
})
export class FeuillageDeleteDialogComponent {
  feuillage?: IFeuillage;

  constructor(protected feuillageService: FeuillageService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.feuillageService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
