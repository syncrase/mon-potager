import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IWettstein } from '../wettstein.model';
import { WettsteinService } from '../service/wettstein.service';

@Component({
  templateUrl: './wettstein-delete-dialog.component.html',
})
export class WettsteinDeleteDialogComponent {
  wettstein?: IWettstein;

  constructor(protected wettsteinService: WettsteinService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.wettsteinService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
