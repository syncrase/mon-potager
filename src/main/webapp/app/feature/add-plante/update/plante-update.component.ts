import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {AbstractControl, FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize} from 'rxjs/operators';

import {AddPlanteService} from "../service/add-plante.service";
import {IScrapedPlante, ScrapedPlante} from "../scraped-plant.model";
import {CronquistRank, ICronquistRank} from "../../../entities/cronquist-rank/cronquist-rank.model";
import {CronquistTaxonomikRanks} from "../../../entities/enumerations/cronquist-taxonomik-ranks.model";

@Component({
  selector: 'jhi-plante-update',
  templateUrl: './plante-update.component.html',
})
export class PlanteUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nomsVernaculaires: [],
    cronquist: this.fb.array([]),
  });
  cronquistRanks: ICronquistRank[] | null | undefined;

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
    this.subscribeToSaveResponse(this.planteService.save(plante));
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IScrapedPlante>>): void {
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

  protected cronquistRanksAsObject(cronquistRanks: ICronquistRank[]): any {
    const classif: {
      [key: string]: any
    } = {};
    const keys = Object.keys(CronquistTaxonomikRanks);
    for (const cronquistRank of cronquistRanks) {
      const found = keys.find(key => key.valueOf() === cronquistRank.rank?.valueOf());
      if (found) {
        classif[found] = cronquistRank.nom;
      }
    }
    return classif;
  }

  protected updateForm(plante: IScrapedPlante): void {
    this.editForm.patchValue({
      id: plante.id,
      nomsVernaculaires: plante.nomsVernaculaires?.map(nv => nv.nom).join(', '),
    });
    this.editForm.setControl('cronquist', this.fb.group(this.cronquistRanksAsObject(plante.lowestClassificationRanks!)));
    this.cronquistRanks = plante.lowestClassificationRanks;
  }

  protected createFromForm(): IScrapedPlante {
    return {
      ...new ScrapedPlante(),
      id: this.editForm.get(['id'])!.value,
      nomsVernaculaires: this.editForm.get(['nomsVernaculaires'])!.value.split(',').map((nv: string) => nv.trim()),
      lowestClassificationRanks: this.objectAsCronquistRanks(this.editForm.get(['cronquist'])!.value),
    };
  }

  protected objectAsCronquistRanks(param: AbstractControl | null): ICronquistRank[] {
    const classification: ICronquistRank[] = [];
    // eslint-disable-next-line guard-for-in
    for (const n in CronquistTaxonomikRanks) {
      const rank = new CronquistRank();
      rank.rank = CronquistTaxonomikRanks[n as keyof typeof CronquistTaxonomikRanks];
      if (param) {
        rank.nom = (param as any)[n]!;
      }
      const receivedRank = this.cronquistRanks?.find(cr => cr.rank === n);
      rank.id = receivedRank?.id
      classification.push(rank);
    }

    return classification;
  }
}
