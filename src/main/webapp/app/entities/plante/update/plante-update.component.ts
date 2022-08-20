import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPlante, Plante } from '../plante.model';
import { PlanteService } from '../service/plante.service';
import { IClassification } from 'app/entities/classification/classification.model';
import { ClassificationService } from 'app/entities/classification/service/classification.service';
import { INomVernaculaire } from 'app/entities/nom-vernaculaire/nom-vernaculaire.model';
import { NomVernaculaireService } from 'app/entities/nom-vernaculaire/service/nom-vernaculaire.service';
import { IReference } from 'app/entities/reference/reference.model';
import { ReferenceService } from 'app/entities/reference/service/reference.service';

@Component({
  selector: 'jhi-plante-update',
  templateUrl: './plante-update.component.html',
})
export class PlanteUpdateComponent implements OnInit {
  isSaving = false;

  classificationsSharedCollection: IClassification[] = [];
  nomVernaculairesSharedCollection: INomVernaculaire[] = [];
  referencesSharedCollection: IReference[] = [];

  editForm = this.fb.group({
    id: [],
    classification: [],
    nomsVernaculaires: [],
    references: [],
  });

  constructor(
    protected planteService: PlanteService,
    protected classificationService: ClassificationService,
    protected nomVernaculaireService: NomVernaculaireService,
    protected referenceService: ReferenceService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ plante }) => {
      this.updateForm(plante);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const plante = this.createFromForm();
    if (plante.id !== undefined) {
      this.subscribeToSaveResponse(this.planteService.update(plante));
    } else {
      this.subscribeToSaveResponse(this.planteService.create(plante));
    }
  }

  trackClassificationById(_index: number, item: IClassification): number {
    return item.id!;
  }

  trackNomVernaculaireById(_index: number, item: INomVernaculaire): number {
    return item.id!;
  }

  trackReferenceById(_index: number, item: IReference): number {
    return item.id!;
  }

  getSelectedNomVernaculaire(option: INomVernaculaire, selectedVals?: INomVernaculaire[]): INomVernaculaire {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  getSelectedReference(option: IReference, selectedVals?: IReference[]): IReference {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlante>>): void {
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

  protected updateForm(plante: IPlante): void {
    this.editForm.patchValue({
      id: plante.id,
      classification: plante.classification,
      nomsVernaculaires: plante.nomsVernaculaires,
      references: plante.references,
    });

    this.classificationsSharedCollection = this.classificationService.addClassificationToCollectionIfMissing(
      this.classificationsSharedCollection,
      plante.classification
    );
    this.nomVernaculairesSharedCollection = this.nomVernaculaireService.addNomVernaculaireToCollectionIfMissing(
      this.nomVernaculairesSharedCollection,
      ...(plante.nomsVernaculaires ?? [])
    );
    this.referencesSharedCollection = this.referenceService.addReferenceToCollectionIfMissing(
      this.referencesSharedCollection,
      ...(plante.references ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.classificationService
      .query()
      .pipe(map((res: HttpResponse<IClassification[]>) => res.body ?? []))
      .pipe(
        map((classifications: IClassification[]) =>
          this.classificationService.addClassificationToCollectionIfMissing(classifications, this.editForm.get('classification')!.value)
        )
      )
      .subscribe((classifications: IClassification[]) => (this.classificationsSharedCollection = classifications));

    this.nomVernaculaireService
      .query()
      .pipe(map((res: HttpResponse<INomVernaculaire[]>) => res.body ?? []))
      .pipe(
        map((nomVernaculaires: INomVernaculaire[]) =>
          this.nomVernaculaireService.addNomVernaculaireToCollectionIfMissing(
            nomVernaculaires,
            ...(this.editForm.get('nomsVernaculaires')!.value ?? [])
          )
        )
      )
      .subscribe((nomVernaculaires: INomVernaculaire[]) => (this.nomVernaculairesSharedCollection = nomVernaculaires));

    this.referenceService
      .query()
      .pipe(map((res: HttpResponse<IReference[]>) => res.body ?? []))
      .pipe(
        map((references: IReference[]) =>
          this.referenceService.addReferenceToCollectionIfMissing(references, ...(this.editForm.get('references')!.value ?? []))
        )
      )
      .subscribe((references: IReference[]) => (this.referencesSharedCollection = references));
  }

  protected createFromForm(): IPlante {
    return {
      ...new Plante(),
      id: this.editForm.get(['id'])!.value,
      classification: this.editForm.get(['classification'])!.value,
      nomsVernaculaires: this.editForm.get(['nomsVernaculaires'])!.value,
      references: this.editForm.get(['references'])!.value,
    };
  }
}
