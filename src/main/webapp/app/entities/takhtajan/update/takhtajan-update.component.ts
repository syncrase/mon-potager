import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITakhtajan, Takhtajan } from '../takhtajan.model';
import { TakhtajanService } from '../service/takhtajan.service';
import { IClassification } from 'app/entities/classification/classification.model';
import { ClassificationService } from 'app/entities/classification/service/classification.service';

@Component({
  selector: 'jhi-takhtajan-update',
  templateUrl: './takhtajan-update.component.html',
})
export class TakhtajanUpdateComponent implements OnInit {
  isSaving = false;

  classificationsCollection: IClassification[] = [];

  editForm = this.fb.group({
    id: [],
    classification: [],
  });

  constructor(
    protected takhtajanService: TakhtajanService,
    protected classificationService: ClassificationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ takhtajan }) => {
      this.updateForm(takhtajan);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const takhtajan = this.createFromForm();
    if (takhtajan.id !== undefined) {
      this.subscribeToSaveResponse(this.takhtajanService.update(takhtajan));
    } else {
      this.subscribeToSaveResponse(this.takhtajanService.create(takhtajan));
    }
  }

  trackClassificationById(_index: number, item: IClassification): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITakhtajan>>): void {
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

  protected updateForm(takhtajan: ITakhtajan): void {
    this.editForm.patchValue({
      id: takhtajan.id,
      classification: takhtajan.classification,
    });

    this.classificationsCollection = this.classificationService.addClassificationToCollectionIfMissing(
      this.classificationsCollection,
      takhtajan.classification
    );
  }

  protected loadRelationshipsOptions(): void {
    this.classificationService
      .query({ 'takhtajanId.specified': 'false' })
      .pipe(map((res: HttpResponse<IClassification[]>) => res.body ?? []))
      .pipe(
        map((classifications: IClassification[]) =>
          this.classificationService.addClassificationToCollectionIfMissing(classifications, this.editForm.get('classification')!.value)
        )
      )
      .subscribe((classifications: IClassification[]) => (this.classificationsCollection = classifications));
  }

  protected createFromForm(): ITakhtajan {
    return {
      ...new Takhtajan(),
      id: this.editForm.get(['id'])!.value,
      classification: this.editForm.get(['classification'])!.value,
    };
  }
}
