import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IEnsoleillement, Ensoleillement } from '../ensoleillement.model';
import { EnsoleillementService } from '../service/ensoleillement.service';
import { IPlante } from 'app/entities/plante/plante.model';
import { PlanteService } from 'app/entities/plante/service/plante.service';

@Component({
  selector: 'jhi-ensoleillement-update',
  templateUrl: './ensoleillement-update.component.html',
})
export class EnsoleillementUpdateComponent implements OnInit {
  isSaving = false;

  plantesSharedCollection: IPlante[] = [];

  editForm = this.fb.group({
    id: [],
    orientation: [],
    ensoleilement: [],
    plante: [],
  });

  constructor(
    protected ensoleillementService: EnsoleillementService,
    protected planteService: PlanteService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ensoleillement }) => {
      this.updateForm(ensoleillement);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ensoleillement = this.createFromForm();
    if (ensoleillement.id !== undefined) {
      this.subscribeToSaveResponse(this.ensoleillementService.update(ensoleillement));
    } else {
      this.subscribeToSaveResponse(this.ensoleillementService.create(ensoleillement));
    }
  }

  trackPlanteById(_index: number, item: IPlante): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEnsoleillement>>): void {
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

  protected updateForm(ensoleillement: IEnsoleillement): void {
    this.editForm.patchValue({
      id: ensoleillement.id,
      orientation: ensoleillement.orientation,
      ensoleilement: ensoleillement.ensoleilement,
      plante: ensoleillement.plante,
    });

    this.plantesSharedCollection = this.planteService.addPlanteToCollectionIfMissing(this.plantesSharedCollection, ensoleillement.plante);
  }

  protected loadRelationshipsOptions(): void {
    this.planteService
      .query()
      .pipe(map((res: HttpResponse<IPlante[]>) => res.body ?? []))
      .pipe(map((plantes: IPlante[]) => this.planteService.addPlanteToCollectionIfMissing(plantes, this.editForm.get('plante')!.value)))
      .subscribe((plantes: IPlante[]) => (this.plantesSharedCollection = plantes));
  }

  protected createFromForm(): IEnsoleillement {
    return {
      ...new Ensoleillement(),
      id: this.editForm.get(['id'])!.value,
      orientation: this.editForm.get(['orientation'])!.value,
      ensoleilement: this.editForm.get(['ensoleilement'])!.value,
      plante: this.editForm.get(['plante'])!.value,
    };
  }
}
