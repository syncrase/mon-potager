import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPlante, Plante } from '../plante.model';
import { PlanteService } from '../service/plante.service';
import { ICycleDeVie } from 'app/entities/cycle-de-vie/cycle-de-vie.model';
import { CycleDeVieService } from 'app/entities/cycle-de-vie/service/cycle-de-vie.service';
import { ISol } from 'app/entities/sol/sol.model';
import { SolService } from 'app/entities/sol/service/sol.service';
import { ITemperature } from 'app/entities/temperature/temperature.model';
import { TemperatureService } from 'app/entities/temperature/service/temperature.service';
import { IRacine } from 'app/entities/racine/racine.model';
import { RacineService } from 'app/entities/racine/service/racine.service';
import { IStrate } from 'app/entities/strate/strate.model';
import { StrateService } from 'app/entities/strate/service/strate.service';
import { IFeuillage } from 'app/entities/feuillage/feuillage.model';
import { FeuillageService } from 'app/entities/feuillage/service/feuillage.service';
import { INomVernaculaire } from 'app/entities/nom-vernaculaire/nom-vernaculaire.model';
import { NomVernaculaireService } from 'app/entities/nom-vernaculaire/service/nom-vernaculaire.service';

@Component({
  selector: 'jhi-plante-update',
  templateUrl: './plante-update.component.html',
})
export class PlanteUpdateComponent implements OnInit {
  isSaving = false;

  plantesSharedCollection: IPlante[] = [];
  cycleDeViesSharedCollection: ICycleDeVie[] = [];
  solsSharedCollection: ISol[] = [];
  temperaturesSharedCollection: ITemperature[] = [];
  racinesSharedCollection: IRacine[] = [];
  stratesSharedCollection: IStrate[] = [];
  feuillagesSharedCollection: IFeuillage[] = [];
  nomVernaculairesSharedCollection: INomVernaculaire[] = [];

  editForm = this.fb.group({
    id: [],
    entretien: [],
    histoire: [],
    vitesseCroissance: [],
    exposition: [],
    cycleDeVie: [],
    sol: [],
    temperature: [],
    racine: [],
    strate: [],
    feuillage: [],
    nomsVernaculaires: [],
    planteBotanique: [],
  });

  constructor(
    protected planteService: PlanteService,
    protected cycleDeVieService: CycleDeVieService,
    protected solService: SolService,
    protected temperatureService: TemperatureService,
    protected racineService: RacineService,
    protected strateService: StrateService,
    protected feuillageService: FeuillageService,
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

  trackPlanteById(_index: number, item: IPlante): number {
    return item.id!;
  }

  trackCycleDeVieById(_index: number, item: ICycleDeVie): number {
    return item.id!;
  }

  trackSolById(_index: number, item: ISol): number {
    return item.id!;
  }

  trackTemperatureById(_index: number, item: ITemperature): number {
    return item.id!;
  }

  trackRacineById(_index: number, item: IRacine): number {
    return item.id!;
  }

  trackStrateById(_index: number, item: IStrate): number {
    return item.id!;
  }

  trackFeuillageById(_index: number, item: IFeuillage): number {
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
      entretien: plante.entretien,
      histoire: plante.histoire,
      vitesseCroissance: plante.vitesseCroissance,
      exposition: plante.exposition,
      cycleDeVie: plante.cycleDeVie,
      sol: plante.sol,
      temperature: plante.temperature,
      racine: plante.racine,
      strate: plante.strate,
      feuillage: plante.feuillage,
      nomsVernaculaires: plante.nomsVernaculaires,
      planteBotanique: plante.planteBotanique,
    });

    this.plantesSharedCollection = this.planteService.addPlanteToCollectionIfMissing(this.plantesSharedCollection, plante.planteBotanique);
    this.cycleDeViesSharedCollection = this.cycleDeVieService.addCycleDeVieToCollectionIfMissing(
      this.cycleDeViesSharedCollection,
      plante.cycleDeVie
    );
    this.solsSharedCollection = this.solService.addSolToCollectionIfMissing(this.solsSharedCollection, plante.sol);
    this.temperaturesSharedCollection = this.temperatureService.addTemperatureToCollectionIfMissing(
      this.temperaturesSharedCollection,
      plante.temperature
    );
    this.racinesSharedCollection = this.racineService.addRacineToCollectionIfMissing(this.racinesSharedCollection, plante.racine);
    this.stratesSharedCollection = this.strateService.addStrateToCollectionIfMissing(this.stratesSharedCollection, plante.strate);
    this.feuillagesSharedCollection = this.feuillageService.addFeuillageToCollectionIfMissing(
      this.feuillagesSharedCollection,
      plante.feuillage
    );
    this.nomVernaculairesSharedCollection = this.nomVernaculaireService.addNomVernaculaireToCollectionIfMissing(
      this.nomVernaculairesSharedCollection,
      ...(plante.nomsVernaculaires ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.planteService
      .query()
      .pipe(map((res: HttpResponse<IPlante[]>) => res.body ?? []))
      .pipe(
        map((plantes: IPlante[]) => this.planteService.addPlanteToCollectionIfMissing(plantes, this.editForm.get('planteBotanique')!.value))
      )
      .subscribe((plantes: IPlante[]) => (this.plantesSharedCollection = plantes));

    this.cycleDeVieService
      .query()
      .pipe(map((res: HttpResponse<ICycleDeVie[]>) => res.body ?? []))
      .pipe(
        map((cycleDeVies: ICycleDeVie[]) =>
          this.cycleDeVieService.addCycleDeVieToCollectionIfMissing(cycleDeVies, this.editForm.get('cycleDeVie')!.value)
        )
      )
      .subscribe((cycleDeVies: ICycleDeVie[]) => (this.cycleDeViesSharedCollection = cycleDeVies));

    this.solService
      .query()
      .pipe(map((res: HttpResponse<ISol[]>) => res.body ?? []))
      .pipe(map((sols: ISol[]) => this.solService.addSolToCollectionIfMissing(sols, this.editForm.get('sol')!.value)))
      .subscribe((sols: ISol[]) => (this.solsSharedCollection = sols));

    this.temperatureService
      .query()
      .pipe(map((res: HttpResponse<ITemperature[]>) => res.body ?? []))
      .pipe(
        map((temperatures: ITemperature[]) =>
          this.temperatureService.addTemperatureToCollectionIfMissing(temperatures, this.editForm.get('temperature')!.value)
        )
      )
      .subscribe((temperatures: ITemperature[]) => (this.temperaturesSharedCollection = temperatures));

    this.racineService
      .query()
      .pipe(map((res: HttpResponse<IRacine[]>) => res.body ?? []))
      .pipe(map((racines: IRacine[]) => this.racineService.addRacineToCollectionIfMissing(racines, this.editForm.get('racine')!.value)))
      .subscribe((racines: IRacine[]) => (this.racinesSharedCollection = racines));

    this.strateService
      .query()
      .pipe(map((res: HttpResponse<IStrate[]>) => res.body ?? []))
      .pipe(map((strates: IStrate[]) => this.strateService.addStrateToCollectionIfMissing(strates, this.editForm.get('strate')!.value)))
      .subscribe((strates: IStrate[]) => (this.stratesSharedCollection = strates));

    this.feuillageService
      .query()
      .pipe(map((res: HttpResponse<IFeuillage[]>) => res.body ?? []))
      .pipe(
        map((feuillages: IFeuillage[]) =>
          this.feuillageService.addFeuillageToCollectionIfMissing(feuillages, this.editForm.get('feuillage')!.value)
        )
      )
      .subscribe((feuillages: IFeuillage[]) => (this.feuillagesSharedCollection = feuillages));

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
      entretien: this.editForm.get(['entretien'])!.value,
      histoire: this.editForm.get(['histoire'])!.value,
      vitesseCroissance: this.editForm.get(['vitesseCroissance'])!.value,
      exposition: this.editForm.get(['exposition'])!.value,
      cycleDeVie: this.editForm.get(['cycleDeVie'])!.value,
      sol: this.editForm.get(['sol'])!.value,
      temperature: this.editForm.get(['temperature'])!.value,
      racine: this.editForm.get(['racine'])!.value,
      strate: this.editForm.get(['strate'])!.value,
      feuillage: this.editForm.get(['feuillage'])!.value,
      nomsVernaculaires: this.editForm.get(['nomsVernaculaires'])!.value,
      planteBotanique: this.editForm.get(['planteBotanique'])!.value,
    };
  }
}
