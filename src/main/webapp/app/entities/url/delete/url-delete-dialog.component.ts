import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUrl } from '../url.model';
import { UrlService } from '../service/url.service';

@Component({
  templateUrl: './url-delete-dialog.component.html',
})
export class UrlDeleteDialogComponent {
  url?: IUrl;

  constructor(protected urlService: UrlService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.urlService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
