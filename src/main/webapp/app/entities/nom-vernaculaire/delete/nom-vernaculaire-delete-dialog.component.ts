import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { INomVernaculaire } from '../nom-vernaculaire.model';
import { NomVernaculaireService } from '../service/nom-vernaculaire.service';

@Component({
  templateUrl: './nom-vernaculaire-delete-dialog.component.html',
})
export class NomVernaculaireDeleteDialogComponent {
  nomVernaculaire?: INomVernaculaire;

  constructor(protected nomVernaculaireService: NomVernaculaireService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.nomVernaculaireService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
