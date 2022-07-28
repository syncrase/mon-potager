import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IMois, Mois } from '../mois.model';
import { MoisService } from '../service/mois.service';

@Component({
  selector: 'jhi-mois-update',
  templateUrl: './mois-update.component.html',
})
export class MoisUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    numero: [null, [Validators.required]],
    nom: [null, [Validators.required]],
  });

  constructor(protected moisService: MoisService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ mois }) => {
      this.updateForm(mois);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const mois = this.createFromForm();
    if (mois.id !== undefined) {
      this.subscribeToSaveResponse(this.moisService.update(mois));
    } else {
      this.subscribeToSaveResponse(this.moisService.create(mois));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMois>>): void {
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

  protected updateForm(mois: IMois): void {
    this.editForm.patchValue({
      id: mois.id,
      numero: mois.numero,
      nom: mois.nom,
    });
  }

  protected createFromForm(): IMois {
    return {
      ...new Mois(),
      id: this.editForm.get(['id'])!.value,
      numero: this.editForm.get(['numero'])!.value,
      nom: this.editForm.get(['nom'])!.value,
    };
  }
}
