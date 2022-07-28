import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRacine } from '../racine.model';
import { RacineService } from '../service/racine.service';

@Component({
  templateUrl: './racine-delete-dialog.component.html',
})
export class RacineDeleteDialogComponent {
  racine?: IRacine;

  constructor(protected racineService: RacineService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.racineService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
