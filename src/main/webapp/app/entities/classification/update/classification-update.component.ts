import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize} from 'rxjs/operators';

import {Classification, IClassification} from '../classification.model';
import {ClassificationService} from '../service/classification.service';

@Component({
  selector: 'jhi-classification-update',
  templateUrl: './classification-update.component.html',
})
export class ClassificationUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
  });

  constructor(
    protected classificationService: ClassificationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ classification }) => {
      this.updateForm(classification);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const classification = this.createFromForm();
    if (classification.id !== undefined) {
      this.subscribeToSaveResponse(this.classificationService.update(classification));
    } else {
      this.subscribeToSaveResponse(this.classificationService.create(classification));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClassification>>): void {
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

  protected updateForm(classification: IClassification): void {
    this.editForm.patchValue({
      id: classification.id,
    });
  }

  protected createFromForm(): IClassification {
    return {
      ...new Classification(),
      id: this.editForm.get(['id'])!.value,
    };
  }
}
