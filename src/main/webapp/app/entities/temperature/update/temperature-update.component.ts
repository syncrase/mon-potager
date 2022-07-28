import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITemperature, Temperature } from '../temperature.model';
import { TemperatureService } from '../service/temperature.service';

@Component({
  selector: 'jhi-temperature-update',
  templateUrl: './temperature-update.component.html',
})
export class TemperatureUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    min: [],
    max: [],
    description: [],
    rusticite: [],
  });

  constructor(protected temperatureService: TemperatureService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ temperature }) => {
      this.updateForm(temperature);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const temperature = this.createFromForm();
    if (temperature.id !== undefined) {
      this.subscribeToSaveResponse(this.temperatureService.update(temperature));
    } else {
      this.subscribeToSaveResponse(this.temperatureService.create(temperature));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITemperature>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(temperature: ITemperature): void {
    this.editForm.patchValue({
      id: temperature.id,
      min: temperature.min,
      max: temperature.max,
      description: temperature.description,
      rusticite: temperature.rusticite,
    });
  }

  protected createFromForm(): ITemperature {
    return {
      ...new Temperature(),
      id: this.editForm.get(['id'])!.value,
      min: this.editForm.get(['min'])!.value,
      max: this.editForm.get(['max'])!.value,
      description: this.editForm.get(['description'])!.value,
      rusticite: this.editForm.get(['rusticite'])!.value,
    };
  }
}
