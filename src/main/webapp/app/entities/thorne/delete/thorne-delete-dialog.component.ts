import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IThorne } from '../thorne.model';
import { ThorneService } from '../service/thorne.service';

@Component({
  templateUrl: './thorne-delete-dialog.component.html',
})
export class ThorneDeleteDialogComponent {
  thorne?: IThorne;

  constructor(protected thorneService: ThorneService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.thorneService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
