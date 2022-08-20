import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {IDahlgren} from '../dahlgren.model';
import {DahlgrenService} from '../service/dahlgren.service';

@Component({
  templateUrl: './dahlgren-delete-dialog.component.html',
})
export class DahlgrenDeleteDialogComponent {
  dahlgren?: IDahlgren;

  constructor(protected dahlgrenService: DahlgrenService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.dahlgrenService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
