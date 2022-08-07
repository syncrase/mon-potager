import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICronquistRank } from '../cronquist-rank.model';
import { CronquistRankService } from '../service/cronquist-rank.service';

@Component({
  templateUrl: './cronquist-rank-delete-dialog.component.html',
})
export class CronquistRankDeleteDialogComponent {
  cronquistRank?: ICronquistRank;

  constructor(protected cronquistRankService: CronquistRankService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cronquistRankService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
