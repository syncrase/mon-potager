import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICycleDeVie, CycleDeVie } from '../cycle-de-vie.model';
import { CycleDeVieService } from '../service/cycle-de-vie.service';
import { ISemis } from 'app/entities/semis/semis.model';
import { SemisService } from 'app/entities/semis/service/semis.service';
import { IPeriodeAnnee } from 'app/entities/periode-annee/periode-annee.model';
import { PeriodeAnneeService } from 'app/entities/periode-annee/service/periode-annee.service';
import { IReproduction } from 'app/entities/reproduction/reproduction.model';
import { ReproductionService } from 'app/entities/reproduction/service/reproduction.service';

@Component({
  selector: 'jhi-cycle-de-vie-update',
  templateUrl: './cycle-de-vie-update.component.html',
})
export class CycleDeVieUpdateComponent implements OnInit {
  isSaving = false;

  semisSharedCollection: ISemis[] = [];
  periodeAnneesSharedCollection: IPeriodeAnnee[] = [];
  reproductionsSharedCollection: IReproduction[] = [];

  editForm = this.fb.group({
    id: [],
    semis: [],
    apparitionFeuilles: [],
    floraison: [],
    recolte: [],
    croissance: [],
    maturite: [],
    plantation: [],
    rempotage: [],
    reproduction: [],
  });

  constructor(
    protected cycleDeVieService: CycleDeVieService,
    protected semisService: SemisService,
    protected periodeAnneeService: PeriodeAnneeService,
    protected reproductionService: ReproductionService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cycleDeVie }) => {
      this.updateForm(cycleDeVie);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cycleDeVie = this.createFromForm();
    if (cycleDeVie.id !== undefined) {
      this.subscribeToSaveResponse(this.cycleDeVieService.update(cycleDeVie));
    } else {
      this.subscribeToSaveResponse(this.cycleDeVieService.create(cycleDeVie));
    }
  }

  trackSemisById(_index: number, item: ISemis): number {
    return item.id!;
  }

  trackPeriodeAnneeById(_index: number, item: IPeriodeAnnee): number {
    return item.id!;
  }

  trackReproductionById(_index: number, item: IReproduction): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICycleDeVie>>): void {
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

  protected updateForm(cycleDeVie: ICycleDeVie): void {
    this.editForm.patchValue({
      id: cycleDeVie.id,
      semis: cycleDeVie.semis,
      apparitionFeuilles: cycleDeVie.apparitionFeuilles,
      floraison: cycleDeVie.floraison,
      recolte: cycleDeVie.recolte,
      croissance: cycleDeVie.croissance,
      maturite: cycleDeVie.maturite,
      plantation: cycleDeVie.plantation,
      rempotage: cycleDeVie.rempotage,
      reproduction: cycleDeVie.reproduction,
    });

    this.semisSharedCollection = this.semisService.addSemisToCollectionIfMissing(this.semisSharedCollection, cycleDeVie.semis);
    this.periodeAnneesSharedCollection = this.periodeAnneeService.addPeriodeAnneeToCollectionIfMissing(
      this.periodeAnneesSharedCollection,
      cycleDeVie.apparitionFeuilles,
      cycleDeVie.floraison,
      cycleDeVie.recolte,
      cycleDeVie.croissance,
      cycleDeVie.maturite,
      cycleDeVie.plantation,
      cycleDeVie.rempotage
    );
    this.reproductionsSharedCollection = this.reproductionService.addReproductionToCollectionIfMissing(
      this.reproductionsSharedCollection,
      cycleDeVie.reproduction
    );
  }

  protected loadRelationshipsOptions(): void {
    this.semisService
      .query()
      .pipe(map((res: HttpResponse<ISemis[]>) => res.body ?? []))
      .pipe(map((semis: ISemis[]) => this.semisService.addSemisToCollectionIfMissing(semis, this.editForm.get('semis')!.value)))
      .subscribe((semis: ISemis[]) => (this.semisSharedCollection = semis));

    this.periodeAnneeService
      .query()
      .pipe(map((res: HttpResponse<IPeriodeAnnee[]>) => res.body ?? []))
      .pipe(
        map((periodeAnnees: IPeriodeAnnee[]) =>
          this.periodeAnneeService.addPeriodeAnneeToCollectionIfMissing(
            periodeAnnees,
            this.editForm.get('apparitionFeuilles')!.value,
            this.editForm.get('floraison')!.value,
            this.editForm.get('recolte')!.value,
            this.editForm.get('croissance')!.value,
            this.editForm.get('maturite')!.value,
            this.editForm.get('plantation')!.value,
            this.editForm.get('rempotage')!.value
          )
        )
      )
      .subscribe((periodeAnnees: IPeriodeAnnee[]) => (this.periodeAnneesSharedCollection = periodeAnnees));

    this.reproductionService
      .query()
      .pipe(map((res: HttpResponse<IReproduction[]>) => res.body ?? []))
      .pipe(
        map((reproductions: IReproduction[]) =>
          this.reproductionService.addReproductionToCollectionIfMissing(reproductions, this.editForm.get('reproduction')!.value)
        )
      )
      .subscribe((reproductions: IReproduction[]) => (this.reproductionsSharedCollection = reproductions));
  }

  protected createFromForm(): ICycleDeVie {
    return {
      ...new CycleDeVie(),
      id: this.editForm.get(['id'])!.value,
      semis: this.editForm.get(['semis'])!.value,
      apparitionFeuilles: this.editForm.get(['apparitionFeuilles'])!.value,
      floraison: this.editForm.get(['floraison'])!.value,
      recolte: this.editForm.get(['recolte'])!.value,
      croissance: this.editForm.get(['croissance'])!.value,
      maturite: this.editForm.get(['maturite'])!.value,
      plantation: this.editForm.get(['plantation'])!.value,
      rempotage: this.editForm.get(['rempotage'])!.value,
      reproduction: this.editForm.get(['reproduction'])!.value,
    };
  }
}
