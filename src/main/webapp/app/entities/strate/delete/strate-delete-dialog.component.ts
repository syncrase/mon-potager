import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IStrate } from '../strate.model';
import { StrateService } from '../service/strate.service';

@Component({
  templateUrl: './strate-delete-dialog.component.html',
})
export class StrateDeleteDialogComponent {
  strate?: IStrate;

  constructor(protected strateService: StrateService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.strateService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
