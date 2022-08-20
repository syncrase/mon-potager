import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {AbstractControl, FormBuilder} from '@angular/forms';
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

@Component({
  selector: 'jhi-plante-update',
  templateUrl: './plante-update.component.html',
})
export class PlanteUpdateComponent implements OnInit {
  isSaving = false;
  missingPlante = false;

  editForm = this.fb.group({
    id: [],
    nomsVernaculaires: [],
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
      nomsVernaculaires: plante.plante?.nomsVernaculaires?.map(nv => nv.nom).join(', '),
      // TODO traiter les références, les images et les sources
    });
    if (plante.cronquistClassificationBranch) {
      this.editForm.setControl('cronquist', this.fb.group(this.cronquistRanksAsObject(plante.cronquistClassificationBranch)));
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
      // rank.rank = CronquistTaxonomicRank[n as keyof typeof CronquistTaxonomicRank];
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
      return nomVernaculairesForm!.value.split(',').map((nv: string) => {
        const nom = new NomVernaculaire();
        nom.nom = nv.trim();
        return nom;
      }) as NomVernaculaire[];
    }
    return null;
  }
}
