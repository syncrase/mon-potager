import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IUrl, Url } from '../url.model';
import { UrlService } from '../service/url.service';
import { ICronquistRank } from 'app/entities/cronquist-rank/cronquist-rank.model';
import { CronquistRankService } from 'app/entities/cronquist-rank/service/cronquist-rank.service';

@Component({
  selector: 'jhi-url-update',
  templateUrl: './url-update.component.html',
})
export class UrlUpdateComponent implements OnInit {
  isSaving = false;

  cronquistRanksSharedCollection: ICronquistRank[] = [];

  editForm = this.fb.group({
    id: [],
    url: [null, [Validators.required]],
    cronquistRank: [],
  });

  constructor(
    protected urlService: UrlService,
    protected cronquistRankService: CronquistRankService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ url }) => {
      this.updateForm(url);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const url = this.createFromForm();
    if (url.id !== undefined) {
      this.subscribeToSaveResponse(this.urlService.update(url));
    } else {
      this.subscribeToSaveResponse(this.urlService.create(url));
    }
  }

  trackCronquistRankById(_index: number, item: ICronquistRank): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUrl>>): void {
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

  protected updateForm(url: IUrl): void {
    this.editForm.patchValue({
      id: url.id,
      url: url.url,
      cronquistRank: url.cronquistRank,
    });

    this.cronquistRanksSharedCollection = this.cronquistRankService.addCronquistRankToCollectionIfMissing(
      this.cronquistRanksSharedCollection,
      url.cronquistRank
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

  protected createFromForm(): IUrl {
    return {
      ...new Url(),
      id: this.editForm.get(['id'])!.value,
      url: this.editForm.get(['url'])!.value,
      cronquistRank: this.editForm.get(['cronquistRank'])!.value,
    };
  }
}
