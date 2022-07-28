import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITemperature } from '../temperature.model';
import { TemperatureService } from '../service/temperature.service';

@Component({
  templateUrl: './temperature-delete-dialog.component.html',
})
export class TemperatureDeleteDialogComponent {
  temperature?: ITemperature;

  constructor(protected temperatureService: TemperatureService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.temperatureService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
