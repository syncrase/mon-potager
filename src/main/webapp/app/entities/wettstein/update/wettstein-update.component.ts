import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IWettstein, Wettstein } from '../wettstein.model';
import { WettsteinService } from '../service/wettstein.service';
import { IClassification } from 'app/entities/classification/classification.model';
import { ClassificationService } from 'app/entities/classification/service/classification.service';

@Component({
  selector: 'jhi-wettstein-update',
  templateUrl: './wettstein-update.component.html',
})
export class WettsteinUpdateComponent implements OnInit {
  isSaving = false;

  classificationsCollection: IClassification[] = [];

  editForm = this.fb.group({
    id: [],
    classification: [],
  });

  constructor(
    protected wettsteinService: WettsteinService,
    protected classificationService: ClassificationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ wettstein }) => {
      this.updateForm(wettstein);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const wettstein = this.createFromForm();
    if (wettstein.id !== undefined) {
      this.subscribeToSaveResponse(this.wettsteinService.update(wettstein));
    } else {
      this.subscribeToSaveResponse(this.wettsteinService.create(wettstein));
    }
  }

  trackClassificationById(_index: number, item: IClassification): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWettstein>>): void {
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

  protected updateForm(wettstein: IWettstein): void {
    this.editForm.patchValue({
      id: wettstein.id,
      classification: wettstein.classification,
    });

    this.classificationsCollection = this.classificationService.addClassificationToCollectionIfMissing(
      this.classificationsCollection,
      wettstein.classification
    );
  }

  protected loadRelationshipsOptions(): void {
    this.classificationService
      .query({ 'wettsteinId.specified': 'false' })
      .pipe(map((res: HttpResponse<IClassification[]>) => res.body ?? []))
      .pipe(
        map((classifications: IClassification[]) =>
          this.classificationService.addClassificationToCollectionIfMissing(classifications, this.editForm.get('classification')!.value)
        )
      )
      .subscribe((classifications: IClassification[]) => (this.classificationsCollection = classifications));
  }

  protected createFromForm(): IWettstein {
    return {
      ...new Wettstein(),
      id: this.editForm.get(['id'])!.value,
      classification: this.editForm.get(['classification'])!.value,
    };
  }
}
