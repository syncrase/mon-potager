import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IReproduction, Reproduction } from '../reproduction.model';
import { ReproductionService } from '../service/reproduction.service';

@Component({
  selector: 'jhi-reproduction-update',
  templateUrl: './reproduction-update.component.html',
})
export class ReproductionUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    vitesse: [],
    type: [],
  });

  constructor(protected reproductionService: ReproductionService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reproduction }) => {
      this.updateForm(reproduction);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const reproduction = this.createFromForm();
    if (reproduction.id !== undefined) {
      this.subscribeToSaveResponse(this.reproductionService.update(reproduction));
    } else {
      this.subscribeToSaveResponse(this.reproductionService.create(reproduction));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReproduction>>): void {
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

  protected updateForm(reproduction: IReproduction): void {
    this.editForm.patchValue({
      id: reproduction.id,
      vitesse: reproduction.vitesse,
      type: reproduction.type,
    });
  }

  protected createFromForm(): IReproduction {
    return {
      ...new Reproduction(),
      id: this.editForm.get(['id'])!.value,
      vitesse: this.editForm.get(['vitesse'])!.value,
      type: this.editForm.get(['type'])!.value,
    };
  }
}
