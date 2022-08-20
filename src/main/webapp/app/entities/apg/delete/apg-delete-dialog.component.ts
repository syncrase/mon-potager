import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {IAPG} from '../apg.model';
import {APGService} from '../service/apg.service';

@Component({
  templateUrl: './apg-delete-dialog.component.html',
})
export class APGDeleteDialogComponent {
  aPG?: IAPG;

  constructor(protected aPGService: APGService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.aPGService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
