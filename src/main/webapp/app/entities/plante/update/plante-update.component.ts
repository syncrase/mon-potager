import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPlante, Plante } from '../plante.model';
import { PlanteService } from '../service/plante.service';
import { ICronquistRank } from 'app/entities/cronquist-rank/cronquist-rank.model';
import { CronquistRankService } from 'app/entities/cronquist-rank/service/cronquist-rank.service';
import { INomVernaculaire } from 'app/entities/nom-vernaculaire/nom-vernaculaire.model';
import { NomVernaculaireService } from 'app/entities/nom-vernaculaire/service/nom-vernaculaire.service';

@Component({
  selector: 'jhi-plante-update',
  templateUrl: './plante-update.component.html',
})
export class PlanteUpdateComponent implements OnInit {
  isSaving = false;

  cronquistRanksSharedCollection: ICronquistRank[] = [];
  nomVernaculairesSharedCollection: INomVernaculaire[] = [];

  editForm = this.fb.group({
    id: [],
    cronquistRank: [],
    nomsVernaculaires: [],
  });

  constructor(
    protected planteService: PlanteService,
    protected cronquistRankService: CronquistRankService,
    protected nomVernaculaireService: NomVernaculaireService,
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

  trackCronquistRankById(_index: number, item: ICronquistRank): number {
    return item.id!;
  }

  trackNomVernaculaireById(_index: number, item: INomVernaculaire): number {
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
      cronquistRank: plante.cronquistRank,
      nomsVernaculaires: plante.nomsVernaculaires,
    });

    this.cronquistRanksSharedCollection = this.cronquistRankService.addCronquistRankToCollectionIfMissing(
      this.cronquistRanksSharedCollection,
      plante.cronquistRank
    );
    this.nomVernaculairesSharedCollection = this.nomVernaculaireService.addNomVernaculaireToCollectionIfMissing(
      this.nomVernaculairesSharedCollection,
      ...(plante.nomsVernaculaires ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.cronquistRankService
      .query()
      .pipe(map((res: HttpResponse<ICronquistRank[]>) => res.body ?? []))
      .pipe(
        map((cronquistRanks: ICronquistRank[]) =>
          this.cronquistRankService.addCronquistRankToCollectionIfMissing(cronquistRanks, this.editForm.get('cronquistRank')!.value)
        )
      )
      .subscribe((cronquistRanks: ICronquistRank[]) => (this.cronquistRanksSharedCollection = cronquistRanks));

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
  }

  protected createFromForm(): IPlante {
    return {
      ...new Plante(),
      id: this.editForm.get(['id'])!.value,
      cronquistRank: this.editForm.get(['cronquistRank'])!.value,
      nomsVernaculaires: this.editForm.get(['nomsVernaculaires'])!.value,
    };
  }
}
