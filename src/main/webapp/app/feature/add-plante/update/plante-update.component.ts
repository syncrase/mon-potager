import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize} from 'rxjs/operators';

import {AddPlanteService, EntityResponseType} from "../service/add-plante.service";
import {IScrapedPlante, ScrapedPlante} from "../scraped-plant.model";
import {CronquistRank, ICronquistRank} from "../../../entities/cronquist-rank/cronquist-rank.model";
import {CronquistTaxonomicRank} from "../../../entities/enumerations/cronquist-taxonomic-rank.model";
import {SessionContextService} from "../../../shared/session-context/session-context.service";
import {INomVernaculaire, NomVernaculaire} from "../../../entities/nom-vernaculaire/nom-vernaculaire.model";
import {Plante} from "../../../entities/plante/plante.model";
import {ReferenceType} from "../../../entities/enumerations/reference-type.model";
import {IReference, Reference} from "../../../entities/reference/reference.model";
import {Url} from "../../../entities/url/url.model";

type DynamicKeysObject = {
  [key: string]: any
};


function urlValidator(control: AbstractControl): DynamicKeysObject | null {
  const regex = new RegExp("^(http[s]?:\\/\\/(www\\.)?|ftp:\\/\\/(www\\.)?|www\\.){1}([0-9A-Za-z-.@:%_+~#=]+)+((\\.[a-zA-Z]{2,3})+)(/(.)*)?(\\?(.)*)?");
  const url = control.value.url.url;
  return regex.test(url) ? null : {invalidFormat: true}
}

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
    images: this.fb.array([]),
    sources: this.fb.array([]),
  });
  cronquistRanks: ICronquistRank[] | null | undefined;
  private planteInitiale!: IScrapedPlante;

  constructor(
    protected planteService: AddPlanteService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
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

  get images(): FormArray {
    return this.editForm.controls['images'] as FormArray;
  }

  ajoutImage(): void {
    const reference = new Reference();
    reference.type = ReferenceType.IMAGE;
    reference.url = new Url();
    (this.editForm.controls['images'] as FormArray).push(this.fb.control(reference));
  }

  deleteImage(index: number): void {
    (this.editForm.controls['images'] as FormArray).removeAt(index);
  }

  get sources(): FormArray {
    return this.editForm.controls['sources'] as FormArray;
  }

  ajoutSource(): void {
    const reference = new Reference();
    reference.type = ReferenceType.SOURCE;
    reference.url = new Url();
    (this.editForm.controls['sources'] as FormArray).push(this.fb.control(reference, control => urlValidator(control)));
    (this.editForm.controls['sources'] as FormArray).disable();
  }

  deleteSource(index: number): void {
    (this.editForm.controls['sources'] as FormArray).removeAt(index);
  }

  toggleSourceEdit(index: number): void {
    const abstractControl = (this.editForm.controls['sources'] as FormArray).at(index);
    if (abstractControl.disabled) {
      abstractControl.enable();
    } else {
      abstractControl.disable();
    }
  }

  isDisabled(index: number): boolean {
    return (this.editForm.controls['sources'] as FormArray).at(index).disabled;
  }

  /**
   * Ouvre le lien dans un nouvel onglet
   * @param i index de la source dans la liste de sources
   */
  redirect(i: number): void {
    const control = this.sources.at(i);
    if (!urlValidator(control)) {
      window.open(
        control.value.url.url,
        '_blank'
      );
    }
  }

  protected subscribeToSaveResponse(result: Observable<EntityResponseType>): void {
    result.pipe(
      finalize(() => this.onSaveFinalize())
    ).subscribe({
      next: (savedPlante) => this.onSaveSuccess(savedPlante.body),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(savedPlante: ScrapedPlante | null): void {
    this.sessionContext.set('plante', savedPlante);
    this.router.navigate(['/feature/add-plante/fiche']);
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
    });
    if (plante.plante?.nomsVernaculaires) {
      for (const nv of plante.plante.nomsVernaculaires) {
        (this.editForm.controls['nomsVernaculaires'] as FormArray).push(this.fb.control(nv));
      }
    }
    if (plante.plante?.references) {
      for (const reference of plante.plante.references) {
        if (reference.type === ReferenceType.IMAGE) {
          (this.editForm.controls['images'] as FormArray).push(this.fb.control(reference));
        }
        if (reference.type === ReferenceType.SOURCE) {
          (this.editForm.controls['sources'] as FormArray).push(this.fb.control(reference));
        }
      }
      (this.editForm.controls['images'] as FormArray).disable();
      (this.editForm.controls['sources'] as FormArray).disable();
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
    plante.nomsVernaculaires = this.nomsVernaculairesFromForm();
    // TODO traiter les références, les images et les sources
    plante.references = this.referencesFromForm();
    return plante;
  }

  private nomsVernaculairesFromForm(): INomVernaculaire[] | null {
    const nomVernaculairesForm = this.editForm.get(['nomsVernaculaires']);
    if (nomVernaculairesForm!.value) {
      const nomVernaculairesExtractedFromForm: NomVernaculaire[] = [];
      // nomVernaculairesForm.value is not iterable
      // eslint-disable-next-line guard-for-in
      for (const nomVernaculairesFormKey in nomVernaculairesForm!.value) {
        nomVernaculairesExtractedFromForm.push(nomVernaculairesForm!.value[nomVernaculairesFormKey] as INomVernaculaire)
      }
      return nomVernaculairesExtractedFromForm;
    }
    return null;
  }

  private referencesFromForm(): IReference[] {
    const imagesForm = this.editForm.get(['images']);
    const referencesExtractedFromForm: IReference[] = [];
    if (imagesForm!.value) {
      // eslint-disable-next-line guard-for-in
      for (const imagesFormKey in imagesForm!.value) {
        referencesExtractedFromForm.push(imagesForm!.value[imagesFormKey] as IReference)
      }
    }
    const sourcesForm = this.editForm.get(['sources']);
    if (sourcesForm!.value) {
      // eslint-disable-next-line guard-for-in
      for (const sourcesFormKey in sourcesForm!.value) {
        referencesExtractedFromForm.push(sourcesForm!.value[sourcesFormKey] as IReference)
      }
    }
    return referencesExtractedFromForm;
  }
}
