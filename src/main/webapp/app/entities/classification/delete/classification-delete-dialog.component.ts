import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {IClassification} from '../classification.model';
import {ClassificationService} from '../service/classification.service';

@Component({
  templateUrl: './classification-delete-dialog.component.html',
})
export class ClassificationDeleteDialogComponent {
  classification?: IClassification;

  constructor(protected classificationService: ClassificationService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.classificationService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
