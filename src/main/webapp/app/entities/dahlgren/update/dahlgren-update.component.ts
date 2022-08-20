import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize, map} from 'rxjs/operators';

import {Dahlgren, IDahlgren} from '../dahlgren.model';
import {DahlgrenService} from '../service/dahlgren.service';
import {IClassification} from 'app/entities/classification/classification.model';
import {ClassificationService} from 'app/entities/classification/service/classification.service';

@Component({
  selector: 'jhi-dahlgren-update',
  templateUrl: './dahlgren-update.component.html',
})
export class DahlgrenUpdateComponent implements OnInit {
  isSaving = false;

  classificationsCollection: IClassification[] = [];

  editForm = this.fb.group({
    id: [],
    classification: [],
  });

  constructor(
    protected dahlgrenService: DahlgrenService,
    protected classificationService: ClassificationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dahlgren }) => {
      this.updateForm(dahlgren);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const dahlgren = this.createFromForm();
    if (dahlgren.id !== undefined) {
      this.subscribeToSaveResponse(this.dahlgrenService.update(dahlgren));
    } else {
      this.subscribeToSaveResponse(this.dahlgrenService.create(dahlgren));
    }
  }

  trackClassificationById(_index: number, item: IClassification): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDahlgren>>): void {
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

  protected updateForm(dahlgren: IDahlgren): void {
    this.editForm.patchValue({
      id: dahlgren.id,
      classification: dahlgren.classification,
    });

    this.classificationsCollection = this.classificationService.addClassificationToCollectionIfMissing(
      this.classificationsCollection,
      dahlgren.classification
    );
  }

  protected loadRelationshipsOptions(): void {
    this.classificationService
      .query({ 'dahlgrenId.specified': 'false' })
      .pipe(map((res: HttpResponse<IClassification[]>) => res.body ?? []))
      .pipe(
        map((classifications: IClassification[]) =>
          this.classificationService.addClassificationToCollectionIfMissing(classifications, this.editForm.get('classification')!.value)
        )
      )
      .subscribe((classifications: IClassification[]) => (this.classificationsCollection = classifications));
  }

  protected createFromForm(): IDahlgren {
    return {
      ...new Dahlgren(),
      id: this.editForm.get(['id'])!.value,
      classification: this.editForm.get(['classification'])!.value,
    };
  }
}
