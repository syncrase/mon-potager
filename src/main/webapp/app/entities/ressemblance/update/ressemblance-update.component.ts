import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IRessemblance, Ressemblance } from '../ressemblance.model';
import { RessemblanceService } from '../service/ressemblance.service';
import { IPlante } from 'app/entities/plante/plante.model';
import { PlanteService } from 'app/entities/plante/service/plante.service';

@Component({
  selector: 'jhi-ressemblance-update',
  templateUrl: './ressemblance-update.component.html',
})
export class RessemblanceUpdateComponent implements OnInit {
  isSaving = false;

  plantesSharedCollection: IPlante[] = [];

  editForm = this.fb.group({
    id: [],
    description: [],
    planteRessemblant: [],
  });

  constructor(
    protected ressemblanceService: RessemblanceService,
    protected planteService: PlanteService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ressemblance }) => {
      this.updateForm(ressemblance);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ressemblance = this.createFromForm();
    if (ressemblance.id !== undefined) {
      this.subscribeToSaveResponse(this.ressemblanceService.update(ressemblance));
    } else {
      this.subscribeToSaveResponse(this.ressemblanceService.create(ressemblance));
    }
  }

  trackPlanteById(_index: number, item: IPlante): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRessemblance>>): void {
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

  protected updateForm(ressemblance: IRessemblance): void {
    this.editForm.patchValue({
      id: ressemblance.id,
      description: ressemblance.description,
      planteRessemblant: ressemblance.planteRessemblant,
    });

    this.plantesSharedCollection = this.planteService.addPlanteToCollectionIfMissing(
      this.plantesSharedCollection,
      ressemblance.planteRessemblant
    );
  }

  protected loadRelationshipsOptions(): void {
    this.planteService
      .query()
      .pipe(map((res: HttpResponse<IPlante[]>) => res.body ?? []))
      .pipe(
        map((plantes: IPlante[]) =>
          this.planteService.addPlanteToCollectionIfMissing(plantes, this.editForm.get('planteRessemblant')!.value)
        )
      )
      .subscribe((plantes: IPlante[]) => (this.plantesSharedCollection = plantes));
  }

  protected createFromForm(): IRessemblance {
    return {
      ...new Ressemblance(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      planteRessemblant: this.editForm.get(['planteRessemblant'])!.value,
    };
  }
}
