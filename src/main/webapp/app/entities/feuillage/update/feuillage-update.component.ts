import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IFeuillage, Feuillage } from '../feuillage.model';
import { FeuillageService } from '../service/feuillage.service';

@Component({
  selector: 'jhi-feuillage-update',
  templateUrl: './feuillage-update.component.html',
})
export class FeuillageUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    type: [],
  });

  constructor(protected feuillageService: FeuillageService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ feuillage }) => {
      this.updateForm(feuillage);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const feuillage = this.createFromForm();
    if (feuillage.id !== undefined) {
      this.subscribeToSaveResponse(this.feuillageService.update(feuillage));
    } else {
      this.subscribeToSaveResponse(this.feuillageService.create(feuillage));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFeuillage>>): void {
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

  protected updateForm(feuillage: IFeuillage): void {
    this.editForm.patchValue({
      id: feuillage.id,
      type: feuillage.type,
    });
  }

  protected createFromForm(): IFeuillage {
    return {
      ...new Feuillage(),
      id: this.editForm.get(['id'])!.value,
      type: this.editForm.get(['type'])!.value,
    };
  }
}
