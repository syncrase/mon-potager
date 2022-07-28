import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { INomVernaculaire, NomVernaculaire } from '../nom-vernaculaire.model';
import { NomVernaculaireService } from '../service/nom-vernaculaire.service';
import { ICronquistRank } from 'app/entities/cronquist-rank/cronquist-rank.model';
import { CronquistRankService } from 'app/entities/cronquist-rank/service/cronquist-rank.service';

@Component({
  selector: 'jhi-nom-vernaculaire-update',
  templateUrl: './nom-vernaculaire-update.component.html',
})
export class NomVernaculaireUpdateComponent implements OnInit {
  isSaving = false;

  cronquistRanksSharedCollection: ICronquistRank[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [null, []],
    cronquistRank: [null, Validators.required],
  });

  constructor(
    protected nomVernaculaireService: NomVernaculaireService,
    protected cronquistRankService: CronquistRankService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ nomVernaculaire }) => {
      this.updateForm(nomVernaculaire);

      this.loadRelationshipsOptions();
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

  trackCronquistRankById(_index: number, item: ICronquistRank): number {
    return item.id!;
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
      cronquistRank: nomVernaculaire.cronquistRank,
    });

    this.cronquistRanksSharedCollection = this.cronquistRankService.addCronquistRankToCollectionIfMissing(
      this.cronquistRanksSharedCollection,
      nomVernaculaire.cronquistRank
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
  }

  protected createFromForm(): INomVernaculaire {
    return {
      ...new NomVernaculaire(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      cronquistRank: this.editForm.get(['cronquistRank'])!.value,
    };
  }
}
