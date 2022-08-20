import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { INomVernaculaire, NomVernaculaire } from '../nom-vernaculaire.model';
import { NomVernaculaireService } from '../service/nom-vernaculaire.service';

@Component({
  selector: 'jhi-nom-vernaculaire-update',
  templateUrl: './nom-vernaculaire-update.component.html',
})
export class NomVernaculaireUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nom: [null, [Validators.required]],
    description: [],
  });

  constructor(
    protected nomVernaculaireService: NomVernaculaireService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ nomVernaculaire }) => {
      this.updateForm(nomVernaculaire);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const nomVernaculaire = this.createFromForm();
    if (nomVernaculaire.id !== undefined) {
      this.subscribeToSaveResponse(this.nomVernaculaireService.update(nomVernaculaire));
    } else {
      this.subscribeToSaveResponse(this.nomVernaculaireService.create(nomVernaculaire));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INomVernaculaire>>): void {
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

  protected updateForm(nomVernaculaire: INomVernaculaire): void {
    this.editForm.patchValue({
      id: nomVernaculaire.id,
      nom: nomVernaculaire.nom,
      description: nomVernaculaire.description,
    });
  }

  protected createFromForm(): INomVernaculaire {
    return {
      ...new NomVernaculaire(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }
}
