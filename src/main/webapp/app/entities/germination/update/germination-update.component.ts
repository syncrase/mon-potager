import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IGermination, Germination } from '../germination.model';
import { GerminationService } from '../service/germination.service';

@Component({
  selector: 'jhi-germination-update',
  templateUrl: './germination-update.component.html',
})
export class GerminationUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    tempsDeGermination: [],
    conditionDeGermination: [],
  });

  constructor(protected germinationService: GerminationService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ germination }) => {
      this.updateForm(germination);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const germination = this.createFromForm();
    if (germination.id !== undefined) {
      this.subscribeToSaveResponse(this.germinationService.update(germination));
    } else {
      this.subscribeToSaveResponse(this.germinationService.create(germination));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGermination>>): void {
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

  protected updateForm(germination: IGermination): void {
    this.editForm.patchValue({
      id: germination.id,
      tempsDeGermination: germination.tempsDeGermination,
      conditionDeGermination: germination.conditionDeGermination,
    });
  }

  protected createFromForm(): IGermination {
    return {
      ...new Germination(),
      id: this.editForm.get(['id'])!.value,
      tempsDeGermination: this.editForm.get(['tempsDeGermination'])!.value,
      conditionDeGermination: this.editForm.get(['conditionDeGermination'])!.value,
    };
  }
}
