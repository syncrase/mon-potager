import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IReference, Reference } from '../reference.model';
import { ReferenceService } from '../service/reference.service';
import { IUrl } from 'app/entities/url/url.model';
import { UrlService } from 'app/entities/url/service/url.service';
import { ReferenceType } from 'app/entities/enumerations/reference-type.model';

@Component({
  selector: 'jhi-reference-update',
  templateUrl: './reference-update.component.html',
})
export class ReferenceUpdateComponent implements OnInit {
  isSaving = false;
  referenceTypeValues = Object.keys(ReferenceType);

  urlsSharedCollection: IUrl[] = [];

  editForm = this.fb.group({
    id: [],
    description: [],
    type: [null, [Validators.required]],
    url: [],
  });

  constructor(
    protected referenceService: ReferenceService,
    protected urlService: UrlService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reference }) => {
      this.updateForm(reference);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const reference = this.createFromForm();
    if (reference.id !== undefined) {
      this.subscribeToSaveResponse(this.referenceService.update(reference));
    } else {
      this.subscribeToSaveResponse(this.referenceService.create(reference));
    }
  }

  trackUrlById(_index: number, item: IUrl): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReference>>): void {
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

  protected updateForm(reference: IReference): void {
    this.editForm.patchValue({
      id: reference.id,
      description: reference.description,
      type: reference.type,
      url: reference.url,
    });

    this.urlsSharedCollection = this.urlService.addUrlToCollectionIfMissing(this.urlsSharedCollection, reference.url);
  }

  protected loadRelationshipsOptions(): void {
    this.urlService
      .query()
      .pipe(map((res: HttpResponse<IUrl[]>) => res.body ?? []))
      .pipe(map((urls: IUrl[]) => this.urlService.addUrlToCollectionIfMissing(urls, this.editForm.get('url')!.value)))
      .subscribe((urls: IUrl[]) => (this.urlsSharedCollection = urls));
  }

  protected createFromForm(): IReference {
    return {
      ...new Reference(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      type: this.editForm.get(['type'])!.value,
      url: this.editForm.get(['url'])!.value,
    };
  }
}
