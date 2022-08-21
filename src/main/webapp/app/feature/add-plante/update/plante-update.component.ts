import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {AbstractControl, FormArray, FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize} from 'rxjs/operators';

import {AddPlanteService} from "../service/add-plante.service";
import {IScrapedPlante, ScrapedPlante} from "../scraped-plant.model";
import {CronquistRank, ICronquistRank} from "../../../entities/cronquist-rank/cronquist-rank.model";
import {CronquistTaxonomicRank} from "../../../entities/enumerations/cronquist-taxonomic-rank.model";
import {SessionContextService} from "../../../shared/session-context/session-context.service";
import {NomVernaculaire} from "../../../entities/nom-vernaculaire/nom-vernaculaire.model";
import {Plante} from "../../../entities/plante/plante.model";

type DynamicKeysObject = {
  [key: string]: any
};

@Component({
  selector: 'jhi-plante-update',
  templateUrl: './plante-update.component.html',
})
export class PlanteUpdateComponent implements OnInit {
  isSaving = false;
  missingPlante = false;

  editForm = this.fb.group({
    id: [],
    nomsVernaculaires: this.fb.array([]),
    cronquist: this.fb.array([]),
  });
  cronquistRanks: ICronquistRank[] | null | undefined;
  private planteInitiale!: IScrapedPlante;

  constructor(
    protected planteService: AddPlanteService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder,
    protected sessionContext: SessionContextService
  ) {
  }

  ngOnInit(): void {
    const plante = this.sessionContext.get('plante') as ScrapedPlante | undefined;
    if (plante) {
      this.updateForm(plante);
    } else {
      // eslint-disable-next-line no-console
      console.log('pas de plante reçue');
      this.missingPlante = true;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    // this.isSaving = true;
    const plante = this.createFromForm();
    this.subscribeToSaveResponse(this.planteService.save(plante));
  }

  ajoutNomVernaculaire(): void {
    (this.editForm.controls['nomsVernaculaires'] as FormArray).push(this.fb.control(new NomVernaculaire()));
  }

  deleteNomVernaculaire(index: number): void {
    (this.editForm.controls['nomsVernaculaires'] as FormArray).removeAt(index);
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

  protected formFriendlyCronquistRanks(cronquistRanks: ICronquistRank[]): any {
    // TODO refactor this in order to don't use this.cronquistRanks anymore
    const classif: DynamicKeysObject = {};
    this.cronquistRanks = [];
    // eslint-disable-next-line guard-for-in
    for (const taxonomicRank in CronquistTaxonomicRank) {
      const cronquistRank = cronquistRanks.find(value => value.rank === taxonomicRank);
      if (cronquistRank) {
        this.cronquistRanks.push(cronquistRank);
      } else {
        const cr = new CronquistRank();
        cr.rank = taxonomicRank as CronquistTaxonomicRank;
        cr.nom = null;
        cr.children = null;
        cr.parent = null;
        this.cronquistRanks.push(cr);
      }
      classif[taxonomicRank] = cronquistRank?.nom;
    }
    return classif;
  }

  protected updateForm(plante: IScrapedPlante): void {
    this.planteInitiale = plante;
    this.editForm.patchValue({
      id: plante.plante?.id,
      // TODO traiter les références, les images et les sources
    });
    if (plante.plante?.nomsVernaculaires) {
      for (const nv of plante.plante.nomsVernaculaires) {
        (this.editForm.controls['nomsVernaculaires'] as FormArray).push(this.fb.control(nv));
      }
    }
    if (plante.cronquistClassificationBranch) {
      this.editForm.setControl('cronquist', this.fb.group(this.formFriendlyCronquistRanks(plante.cronquistClassificationBranch)));
    }
  }

  protected createFromForm(): IScrapedPlante {
    return {
      ...new ScrapedPlante(),
      plante: this.getPlanteFromForm(),
      cronquistClassificationBranch: this.objectAsCronquistRanks(this.editForm.get(['cronquist'])!.value),
    };
  }

  private objectAsCronquistRanks(param: AbstractControl | null): ICronquistRank[] {
    const classification: ICronquistRank[] = [];
    // eslint-disable-next-line guard-for-in
    for (const n in CronquistTaxonomicRank) {
      const rank = new CronquistRank();
      rank.rank = n as CronquistTaxonomicRank;
      if (param) {
        rank.nom = (param as any)[n]!;
      }
      const receivedRank = this.cronquistRanks?.find(cr => cr.rank === n);
      rank.id = receivedRank?.id
      classification.push(rank);
    }

    return classification;
  }

  private getPlanteFromForm(): Plante {
    const plante = new Plante();
    plante.id = this.editForm.get(['id'])!.value;
    plante.nomsVernaculaires = this.nomsVernaculaires();
    // TODO traiter les références, les images et les sources
    return plante;
  }

  private nomsVernaculaires(): NomVernaculaire[] | null {
    const nomVernaculairesForm = this.editForm.get(['nomsVernaculaires']);
    if (nomVernaculairesForm!.value) {
      const nomVernaculairesExtractedFromForm: NomVernaculaire[] = [];
      // nomVernaculairesForm.value is not iterable
      // eslint-disable-next-line guard-for-in
      for (const nomVernaculairesFormKey in nomVernaculairesForm!.value) {
        nomVernaculairesExtractedFromForm.push(nomVernaculairesForm!.value[nomVernaculairesFormKey] as NomVernaculaire)
      }
      return nomVernaculairesExtractedFromForm;
    }
    return null;
  }
}
