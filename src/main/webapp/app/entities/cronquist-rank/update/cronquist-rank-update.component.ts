import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICronquistRank, CronquistRank } from '../cronquist-rank.model';
import { CronquistRankService } from '../service/cronquist-rank.service';
import { CronquistTaxonomikRanks } from 'app/entities/enumerations/cronquist-taxonomik-ranks.model';

@Component({
  selector: 'jhi-cronquist-rank-update',
  templateUrl: './cronquist-rank-update.component.html',
})
export class CronquistRankUpdateComponent implements OnInit {
  isSaving = false;
  cronquistTaxonomikRanksValues = Object.keys(CronquistTaxonomikRanks);

  cronquistRanksSharedCollection: ICronquistRank[] = [];

  editForm = this.fb.group({
    id: [],
    rank: [null, [Validators.required]],
    nom: [null, [Validators.required]],
    parent: [],
  });

  constructor(protected cronquistRankService: CronquistRankService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cronquistRank }) => {
      this.updateForm(cronquistRank);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cronquistRank = this.createFromForm();
    if (cronquistRank.id !== undefined) {
      this.subscribeToSaveResponse(this.cronquistRankService.update(cronquistRank));
    } else {
      this.subscribeToSaveResponse(this.cronquistRankService.create(cronquistRank));
    }
  }

  trackCronquistRankById(_index: number, item: ICronquistRank): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICronquistRank>>): void {
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

  protected updateForm(cronquistRank: ICronquistRank): void {
    this.editForm.patchValue({
      id: cronquistRank.id,
      rank: cronquistRank.rank,
      nom: cronquistRank.nom,
      parent: cronquistRank.parent,
    });

    this.cronquistRanksSharedCollection = this.cronquistRankService.addCronquistRankToCollectionIfMissing(
      this.cronquistRanksSharedCollection,
      cronquistRank.parent
    );
  }

  protected loadRelationshipsOptions(): void {
    this.cronquistRankService
      .query()
      .pipe(map((res: HttpResponse<ICronquistRank[]>) => res.body ?? []))
      .pipe(
        map((cronquistRanks: ICronquistRank[]) =>
          this.cronquistRankService.addCronquistRankToCollectionIfMissing(cronquistRanks, this.editForm.get('parent')!.value)
        )
      )
      .subscribe((cronquistRanks: ICronquistRank[]) => (this.cronquistRanksSharedCollection = cronquistRanks));
  }

  protected createFromForm(): ICronquistRank {
    return {
      ...new CronquistRank(),
      id: this.editForm.get(['id'])!.value,
      rank: this.editForm.get(['rank'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      parent: this.editForm.get(['parent'])!.value,
    };
  }
}
