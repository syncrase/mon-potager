import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {IBenthamHooker} from '../bentham-hooker.model';
import {BenthamHookerService} from '../service/bentham-hooker.service';

@Component({
  templateUrl: './bentham-hooker-delete-dialog.component.html',
})
export class BenthamHookerDeleteDialogComponent {
  benthamHooker?: IBenthamHooker;

  constructor(protected benthamHookerService: BenthamHookerService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.benthamHookerService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
