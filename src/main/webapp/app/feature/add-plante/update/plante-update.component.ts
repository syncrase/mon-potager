import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize} from 'rxjs/operators';

import {INomVernaculaire, NomVernaculaire} from 'app/entities/nom-vernaculaire/nom-vernaculaire.model';
import {AddPlanteService} from "../service/add-plante.service";
import {IPlante, Plante} from "../../../entities/plante/plante.model";

@Component({
  selector: 'jhi-plante-update',
  templateUrl: './plante-update.component.html',
})
export class PlanteUpdateComponent implements OnInit {
  isSaving = false;

  // nomVernaculairesSharedCollection: INomVernaculaire[] = [];

  editForm = this.fb.group({
    id: [],
    nomsVernaculaires: [],
  });

  constructor(
    protected planteService: AddPlanteService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({plante}) => {
      this.updateForm(plante);

      // this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const plante = this.createFromForm();
    if (plante.id !== undefined) {
      // this.subscribeToSaveResponse(this.planteService.update(plante));
    } else {
      // this.subscribeToSaveResponse(this.planteService.create(plante));
    }
  }

  trackNomVernaculaireById(_index: number, item: INomVernaculaire): string {
    return item.nom!;
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
      nomsVernaculaires: plante.nomsVernaculaires?.map(nv => nv.nom).join(', '),
    });
  }

  protected createFromForm(): IPlante {
    return {
      ...new Plante(),
      id: this.editForm.get(['id'])!.value,
      nomsVernaculaires: this.editForm.get(['nomsVernaculaires'])!.value.split(',').map((nv: string) => nv.trim()),
    };
  }
}
