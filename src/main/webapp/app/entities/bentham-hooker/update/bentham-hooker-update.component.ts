import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize, map} from 'rxjs/operators';

import {BenthamHooker, IBenthamHooker} from '../bentham-hooker.model';
import {BenthamHookerService} from '../service/bentham-hooker.service';
import {IClassification} from 'app/entities/classification/classification.model';
import {ClassificationService} from 'app/entities/classification/service/classification.service';

@Component({
  selector: 'jhi-bentham-hooker-update',
  templateUrl: './bentham-hooker-update.component.html',
})
export class BenthamHookerUpdateComponent implements OnInit {
  isSaving = false;

  classificationsCollection: IClassification[] = [];

  editForm = this.fb.group({
    id: [],
    classification: [],
  });

  constructor(
    protected benthamHookerService: BenthamHookerService,
    protected classificationService: ClassificationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ benthamHooker }) => {
      this.updateForm(benthamHooker);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const benthamHooker = this.createFromForm();
    if (benthamHooker.id !== undefined) {
      this.subscribeToSaveResponse(this.benthamHookerService.update(benthamHooker));
    } else {
      this.subscribeToSaveResponse(this.benthamHookerService.create(benthamHooker));
    }
  }

  trackClassificationById(_index: number, item: IClassification): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBenthamHooker>>): void {
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

  protected updateForm(benthamHooker: IBenthamHooker): void {
    this.editForm.patchValue({
      id: benthamHooker.id,
      classification: benthamHooker.classification,
    });

    this.classificationsCollection = this.classificationService.addClassificationToCollectionIfMissing(
      this.classificationsCollection,
      benthamHooker.classification
    );
  }

  protected loadRelationshipsOptions(): void {
    this.classificationService
      .query({ 'benthamHookerId.specified': 'false' })
      .pipe(map((res: HttpResponse<IClassification[]>) => res.body ?? []))
      .pipe(
        map((classifications: IClassification[]) =>
          this.classificationService.addClassificationToCollectionIfMissing(classifications, this.editForm.get('classification')!.value)
        )
      )
      .subscribe((classifications: IClassification[]) => (this.classificationsCollection = classifications));
  }

  protected createFromForm(): IBenthamHooker {
    return {
      ...new BenthamHooker(),
      id: this.editForm.get(['id'])!.value,
      classification: this.editForm.get(['classification'])!.value,
    };
  }
}
