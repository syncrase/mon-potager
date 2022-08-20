import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IUrl, Url } from '../url.model';
import { UrlService } from '../service/url.service';

@Component({
  selector: 'jhi-url-update',
  templateUrl: './url-update.component.html',
})
export class UrlUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    url: [null, []],
  });

  constructor(protected urlService: UrlService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ url }) => {
      this.updateForm(url);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const url = this.createFromForm();
    if (url.id !== undefined) {
      this.subscribeToSaveResponse(this.urlService.update(url));
    } else {
      this.subscribeToSaveResponse(this.urlService.create(url));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUrl>>): void {
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

  protected updateForm(url: IUrl): void {
    this.editForm.patchValue({
      id: url.id,
      url: url.url,
    });
  }

  protected createFromForm(): IUrl {
    return {
      ...new Url(),
      id: this.editForm.get(['id'])!.value,
      url: this.editForm.get(['url'])!.value,
    };
  }
}
