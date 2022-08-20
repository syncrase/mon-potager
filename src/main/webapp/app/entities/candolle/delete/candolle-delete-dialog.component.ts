import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {ICandolle} from '../candolle.model';
import {CandolleService} from '../service/candolle.service';

@Component({
  templateUrl: './candolle-delete-dialog.component.html',
})
export class CandolleDeleteDialogComponent {
  candolle?: ICandolle;

  constructor(protected candolleService: CandolleService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.candolleService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
