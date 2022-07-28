import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IStrate, Strate } from '../strate.model';
import { StrateService } from '../service/strate.service';

@Component({
  selector: 'jhi-strate-update',
  templateUrl: './strate-update.component.html',
})
export class StrateUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    type: [],
  });

  constructor(protected strateService: StrateService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ strate }) => {
      this.updateForm(strate);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const strate = this.createFromForm();
    if (strate.id !== undefined) {
      this.subscribeToSaveResponse(this.strateService.update(strate));
    } else {
      this.subscribeToSaveResponse(this.strateService.create(strate));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStrate>>): void {
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

  protected updateForm(strate: IStrate): void {
    this.editForm.patchValue({
      id: strate.id,
      type: strate.type,
    });
  }

  protected createFromForm(): IStrate {
    return {
      ...new Strate(),
      id: this.editForm.get(['id'])!.value,
      type: this.editForm.get(['type'])!.value,
    };
  }
}
