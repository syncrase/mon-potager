import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IAllelopathie, Allelopathie } from '../allelopathie.model';
import { AllelopathieService } from '../service/allelopathie.service';
import { IPlante } from 'app/entities/plante/plante.model';
import { PlanteService } from 'app/entities/plante/service/plante.service';

@Component({
  selector: 'jhi-allelopathie-update',
  templateUrl: './allelopathie-update.component.html',
})
export class AllelopathieUpdateComponent implements OnInit {
  isSaving = false;

  plantesSharedCollection: IPlante[] = [];

  editForm = this.fb.group({
    id: [],
    type: [null, [Validators.required]],
    description: [],
    impact: [null, [Validators.min(-10), Validators.max(10)]],
    cible: [null, Validators.required],
    origine: [null, Validators.required],
  });

  constructor(
    protected allelopathieService: AllelopathieService,
    protected planteService: PlanteService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ allelopathie }) => {
      this.updateForm(allelopathie);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const allelopathie = this.createFromForm();
    if (allelopathie.id !== undefined) {
      this.subscribeToSaveResponse(this.allelopathieService.update(allelopathie));
    } else {
      this.subscribeToSaveResponse(this.allelopathieService.create(allelopathie));
    }
  }

  trackPlanteById(_index: number, item: IPlante): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAllelopathie>>): void {
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

  protected updateForm(allelopathie: IAllelopathie): void {
    this.editForm.patchValue({
      id: allelopathie.id,
      type: allelopathie.type,
      description: allelopathie.description,
      impact: allelopathie.impact,
      cible: allelopathie.cible,
      origine: allelopathie.origine,
    });

    this.plantesSharedCollection = this.planteService.addPlanteToCollectionIfMissing(
      this.plantesSharedCollection,
      allelopathie.cible,
      allelopathie.origine
    );
  }

  protected loadRelationshipsOptions(): void {
    this.planteService
      .query()
      .pipe(map((res: HttpResponse<IPlante[]>) => res.body ?? []))
      .pipe(
        map((plantes: IPlante[]) =>
          this.planteService.addPlanteToCollectionIfMissing(plantes, this.editForm.get('cible')!.value, this.editForm.get('origine')!.value)
        )
      )
      .subscribe((plantes: IPlante[]) => (this.plantesSharedCollection = plantes));
  }

  protected createFromForm(): IAllelopathie {
    return {
      ...new Allelopathie(),
      id: this.editForm.get(['id'])!.value,
      type: this.editForm.get(['type'])!.value,
      description: this.editForm.get(['description'])!.value,
      impact: this.editForm.get(['impact'])!.value,
      cible: this.editForm.get(['cible'])!.value,
      origine: this.editForm.get(['origine'])!.value,
    };
  }
}
