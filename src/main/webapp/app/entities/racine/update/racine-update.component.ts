import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IRacine, Racine } from '../racine.model';
import { RacineService } from '../service/racine.service';

@Component({
  selector: 'jhi-racine-update',
  templateUrl: './racine-update.component.html',
})
export class RacineUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    type: [],
  });

  constructor(protected racineService: RacineService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ racine }) => {
      this.updateForm(racine);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const racine = this.createFromForm();
    if (racine.id !== undefined) {
      this.subscribeToSaveResponse(this.racineService.update(racine));
    } else {
      this.subscribeToSaveResponse(this.racineService.create(racine));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRacine>>): void {
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

  protected updateForm(racine: IRacine): void {
    this.editForm.patchValue({
      id: racine.id,
      type: racine.type,
    });
  }

  protected createFromForm(): IRacine {
    return {
      ...new Racine(),
      id: this.editForm.get(['id'])!.value,
      type: this.editForm.get(['type'])!.value,
    };
  }
}
