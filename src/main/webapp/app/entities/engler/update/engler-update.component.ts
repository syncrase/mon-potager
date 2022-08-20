import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IEngler, Engler } from '../engler.model';
import { EnglerService } from '../service/engler.service';
import { IClassification } from 'app/entities/classification/classification.model';
import { ClassificationService } from 'app/entities/classification/service/classification.service';

@Component({
  selector: 'jhi-engler-update',
  templateUrl: './engler-update.component.html',
})
export class EnglerUpdateComponent implements OnInit {
  isSaving = false;

  classificationsCollection: IClassification[] = [];

  editForm = this.fb.group({
    id: [],
    classification: [],
  });

  constructor(
    protected englerService: EnglerService,
    protected classificationService: ClassificationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ engler }) => {
      this.updateForm(engler);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const engler = this.createFromForm();
    if (engler.id !== undefined) {
      this.subscribeToSaveResponse(this.englerService.update(engler));
    } else {
      this.subscribeToSaveResponse(this.englerService.create(engler));
    }
  }

  trackClassificationById(_index: number, item: IClassification): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEngler>>): void {
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

  protected updateForm(engler: IEngler): void {
    this.editForm.patchValue({
      id: engler.id,
      classification: engler.classification,
    });

    this.classificationsCollection = this.classificationService.addClassificationToCollectionIfMissing(
      this.classificationsCollection,
      engler.classification
    );
  }

  protected loadRelationshipsOptions(): void {
    this.classificationService
      .query({ 'englerId.specified': 'false' })
      .pipe(map((res: HttpResponse<IClassification[]>) => res.body ?? []))
      .pipe(
        map((classifications: IClassification[]) =>
          this.classificationService.addClassificationToCollectionIfMissing(classifications, this.editForm.get('classification')!.value)
        )
      )
      .subscribe((classifications: IClassification[]) => (this.classificationsCollection = classifications));
  }

  protected createFromForm(): IEngler {
    return {
      ...new Engler(),
      id: this.editForm.get(['id'])!.value,
      classification: this.editForm.get(['classification'])!.value,
    };
  }
}
