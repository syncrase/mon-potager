import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ISol, Sol } from '../sol.model';
import { SolService } from '../service/sol.service';

@Component({
  selector: 'jhi-sol-update',
  templateUrl: './sol-update.component.html',
})
export class SolUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    phMin: [],
    phMax: [],
    type: [],
    richesse: [],
  });

  constructor(protected solService: SolService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sol }) => {
      this.updateForm(sol);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sol = this.createFromForm();
    if (sol.id !== undefined) {
      this.subscribeToSaveResponse(this.solService.update(sol));
    } else {
      this.subscribeToSaveResponse(this.solService.create(sol));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISol>>): void {
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

  protected updateForm(sol: ISol): void {
    this.editForm.patchValue({
      id: sol.id,
      phMin: sol.phMin,
      phMax: sol.phMax,
      type: sol.type,
      richesse: sol.richesse,
    });
  }

  protected createFromForm(): ISol {
    return {
      ...new Sol(),
      id: this.editForm.get(['id'])!.value,
      phMin: this.editForm.get(['phMin'])!.value,
      phMax: this.editForm.get(['phMax'])!.value,
      type: this.editForm.get(['type'])!.value,
      richesse: this.editForm.get(['richesse'])!.value,
    };
  }
}
