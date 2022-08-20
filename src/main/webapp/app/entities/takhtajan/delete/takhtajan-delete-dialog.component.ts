import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITakhtajan } from '../takhtajan.model';
import { TakhtajanService } from '../service/takhtajan.service';

@Component({
  templateUrl: './takhtajan-delete-dialog.component.html',
})
export class TakhtajanDeleteDialogComponent {
  takhtajan?: ITakhtajan;

  constructor(protected takhtajanService: TakhtajanService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.takhtajanService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
