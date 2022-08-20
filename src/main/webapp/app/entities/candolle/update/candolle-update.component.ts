import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize, map} from 'rxjs/operators';

import {Candolle, ICandolle} from '../candolle.model';
import {CandolleService} from '../service/candolle.service';
import {IClassification} from 'app/entities/classification/classification.model';
import {ClassificationService} from 'app/entities/classification/service/classification.service';

@Component({
  selector: 'jhi-candolle-update',
  templateUrl: './candolle-update.component.html',
})
export class CandolleUpdateComponent implements OnInit {
  isSaving = false;

  classificationsCollection: IClassification[] = [];

  editForm = this.fb.group({
    id: [],
    classification: [],
  });

  constructor(
    protected candolleService: CandolleService,
    protected classificationService: ClassificationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ candolle }) => {
      this.updateForm(candolle);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const candolle = this.createFromForm();
    if (candolle.id !== undefined) {
      this.subscribeToSaveResponse(this.candolleService.update(candolle));
    } else {
      this.subscribeToSaveResponse(this.candolleService.create(candolle));
    }
  }

  trackClassificationById(_index: number, item: IClassification): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICandolle>>): void {
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

  protected updateForm(candolle: ICandolle): void {
    this.editForm.patchValue({
      id: candolle.id,
      classification: candolle.classification,
    });

    this.classificationsCollection = this.classificationService.addClassificationToCollectionIfMissing(
      this.classificationsCollection,
      candolle.classification
    );
  }

  protected loadRelationshipsOptions(): void {
    this.classificationService
      .query({ 'candolleId.specified': 'false' })
      .pipe(map((res: HttpResponse<IClassification[]>) => res.body ?? []))
      .pipe(
        map((classifications: IClassification[]) =>
          this.classificationService.addClassificationToCollectionIfMissing(classifications, this.editForm.get('classification')!.value)
        )
      )
      .subscribe((classifications: IClassification[]) => (this.classificationsCollection = classifications));
  }

  protected createFromForm(): ICandolle {
    return {
      ...new Candolle(),
      id: this.editForm.get(['id'])!.value,
      classification: this.editForm.get(['classification'])!.value,
    };
  }
}
