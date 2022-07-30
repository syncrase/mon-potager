import { ComponentFixture, TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

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

import { PlanteUpdateComponent } from './plante-update.component';
import {IPlante, Plante} from "../../../entities/plante/plante.model";
import {AddPlanteService} from "../service/add-plante.service";

describe('Plante Management Update Component', () => {
  let comp: PlanteUpdateComponent;
  let fixture: ComponentFixture<PlanteUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let planteService: AddPlanteService;
  let cycleDeVieService: CycleDeVieService;
  let solService: SolService;
  let temperatureService: TemperatureService;
  let racineService: RacineService;
  let strateService: StrateService;
  let feuillageService: FeuillageService;
  let nomVernaculaireService: NomVernaculaireService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PlanteUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PlanteUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlanteUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    planteService = TestBed.inject(AddPlanteService);
    cycleDeVieService = TestBed.inject(CycleDeVieService);
    solService = TestBed.inject(SolService);
    temperatureService = TestBed.inject(TemperatureService);
    racineService = TestBed.inject(RacineService);
    strateService = TestBed.inject(StrateService);
    feuillageService = TestBed.inject(FeuillageService);
    nomVernaculaireService = TestBed.inject(NomVernaculaireService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Plante query and add missing value', () => {
      const plante: IPlante = { id: 456 };
      const planteBotanique: IPlante = { id: 85393 };
      plante.planteBotanique = planteBotanique;

      const planteCollection: IPlante[] = [{ id: 40948 }];
      jest.spyOn(planteService, 'query').mockReturnValue(of(new HttpResponse({ body: planteCollection })));
      const additionalPlantes = [planteBotanique];
      const expectedCollection: IPlante[] = [...additionalPlantes, ...planteCollection];
      jest.spyOn(planteService, 'addPlanteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      expect(planteService.query).toHaveBeenCalled();
      expect(planteService.addPlanteToCollectionIfMissing).toHaveBeenCalledWith(planteCollection, ...additionalPlantes);
      expect(comp.plantesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call CycleDeVie query and add missing value', () => {
      const plante: IPlante = { id: 456 };
      const cycleDeVie: ICycleDeVie = { id: 23114 };
      plante.cycleDeVie = cycleDeVie;

      const cycleDeVieCollection: ICycleDeVie[] = [{ id: 34038 }];
      jest.spyOn(cycleDeVieService, 'query').mockReturnValue(of(new HttpResponse({ body: cycleDeVieCollection })));
      const additionalCycleDeVies = [cycleDeVie];
      const expectedCollection: ICycleDeVie[] = [...additionalCycleDeVies, ...cycleDeVieCollection];
      jest.spyOn(cycleDeVieService, 'addCycleDeVieToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      expect(cycleDeVieService.query).toHaveBeenCalled();
      expect(cycleDeVieService.addCycleDeVieToCollectionIfMissing).toHaveBeenCalledWith(cycleDeVieCollection, ...additionalCycleDeVies);
      expect(comp.cycleDeViesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Sol query and add missing value', () => {
      const plante: IPlante = { id: 456 };
      const sol: ISol = { id: 26354 };
      plante.sol = sol;

      const solCollection: ISol[] = [{ id: 38568 }];
      jest.spyOn(solService, 'query').mockReturnValue(of(new HttpResponse({ body: solCollection })));
      const additionalSols = [sol];
      const expectedCollection: ISol[] = [...additionalSols, ...solCollection];
      jest.spyOn(solService, 'addSolToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      expect(solService.query).toHaveBeenCalled();
      expect(solService.addSolToCollectionIfMissing).toHaveBeenCalledWith(solCollection, ...additionalSols);
      expect(comp.solsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Temperature query and add missing value', () => {
      const plante: IPlante = { id: 456 };
      const temperature: ITemperature = { id: 46526 };
      plante.temperature = temperature;

      const temperatureCollection: ITemperature[] = [{ id: 29603 }];
      jest.spyOn(temperatureService, 'query').mockReturnValue(of(new HttpResponse({ body: temperatureCollection })));
      const additionalTemperatures = [temperature];
      const expectedCollection: ITemperature[] = [...additionalTemperatures, ...temperatureCollection];
      jest.spyOn(temperatureService, 'addTemperatureToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      expect(temperatureService.query).toHaveBeenCalled();
      expect(temperatureService.addTemperatureToCollectionIfMissing).toHaveBeenCalledWith(temperatureCollection, ...additionalTemperatures);
      expect(comp.temperaturesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Racine query and add missing value', () => {
      const plante: IPlante = { id: 456 };
      const racine: IRacine = { id: 44460 };
      plante.racine = racine;

      const racineCollection: IRacine[] = [{ id: 93803 }];
      jest.spyOn(racineService, 'query').mockReturnValue(of(new HttpResponse({ body: racineCollection })));
      const additionalRacines = [racine];
      const expectedCollection: IRacine[] = [...additionalRacines, ...racineCollection];
      jest.spyOn(racineService, 'addRacineToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      expect(racineService.query).toHaveBeenCalled();
      expect(racineService.addRacineToCollectionIfMissing).toHaveBeenCalledWith(racineCollection, ...additionalRacines);
      expect(comp.racinesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Strate query and add missing value', () => {
      const plante: IPlante = { id: 456 };
      const strate: IStrate = { id: 3328 };
      plante.strate = strate;

      const strateCollection: IStrate[] = [{ id: 91423 }];
      jest.spyOn(strateService, 'query').mockReturnValue(of(new HttpResponse({ body: strateCollection })));
      const additionalStrates = [strate];
      const expectedCollection: IStrate[] = [...additionalStrates, ...strateCollection];
      jest.spyOn(strateService, 'addStrateToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      expect(strateService.query).toHaveBeenCalled();
      expect(strateService.addStrateToCollectionIfMissing).toHaveBeenCalledWith(strateCollection, ...additionalStrates);
      expect(comp.stratesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Feuillage query and add missing value', () => {
      const plante: IPlante = { id: 456 };
      const feuillage: IFeuillage = { id: 51118 };
      plante.feuillage = feuillage;

      const feuillageCollection: IFeuillage[] = [{ id: 75829 }];
      jest.spyOn(feuillageService, 'query').mockReturnValue(of(new HttpResponse({ body: feuillageCollection })));
      const additionalFeuillages = [feuillage];
      const expectedCollection: IFeuillage[] = [...additionalFeuillages, ...feuillageCollection];
      jest.spyOn(feuillageService, 'addFeuillageToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      expect(feuillageService.query).toHaveBeenCalled();
      expect(feuillageService.addFeuillageToCollectionIfMissing).toHaveBeenCalledWith(feuillageCollection, ...additionalFeuillages);
      expect(comp.feuillagesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call NomVernaculaire query and add missing value', () => {
      const plante: IPlante = { id: 456 };
      const nomsVernaculaires: INomVernaculaire[] = [{ id: 6138 }];
      plante.nomsVernaculaires = nomsVernaculaires;

      const nomVernaculaireCollection: INomVernaculaire[] = [{ id: 35825 }];
      jest.spyOn(nomVernaculaireService, 'query').mockReturnValue(of(new HttpResponse({ body: nomVernaculaireCollection })));
      const additionalNomVernaculaires = [...nomsVernaculaires];
      const expectedCollection: INomVernaculaire[] = [...additionalNomVernaculaires, ...nomVernaculaireCollection];
      jest.spyOn(nomVernaculaireService, 'addNomVernaculaireToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      expect(nomVernaculaireService.query).toHaveBeenCalled();
      expect(nomVernaculaireService.addNomVernaculaireToCollectionIfMissing).toHaveBeenCalledWith(
        nomVernaculaireCollection,
        ...additionalNomVernaculaires
      );
      expect(comp.nomVernaculairesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const plante: IPlante = { id: 456 };
      const planteBotanique: IPlante = { id: 67581 };
      plante.planteBotanique = planteBotanique;
      const cycleDeVie: ICycleDeVie = { id: 2310 };
      plante.cycleDeVie = cycleDeVie;
      const sol: ISol = { id: 687 };
      plante.sol = sol;
      const temperature: ITemperature = { id: 91456 };
      plante.temperature = temperature;
      const racine: IRacine = { id: 45074 };
      plante.racine = racine;
      const strate: IStrate = { id: 92710 };
      plante.strate = strate;
      const feuillage: IFeuillage = { id: 23100 };
      plante.feuillage = feuillage;
      const nomsVernaculaires: INomVernaculaire = { id: 18705 };
      plante.nomsVernaculaires = [nomsVernaculaires];

      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      // expect(comp.editForm.value).toEqual(expect.objectContaining(plante));
      expect(comp.plantesSharedCollection).toContain(planteBotanique);
      expect(comp.cycleDeViesSharedCollection).toContain(cycleDeVie);
      expect(comp.solsSharedCollection).toContain(sol);
      expect(comp.temperaturesSharedCollection).toContain(temperature);
      expect(comp.racinesSharedCollection).toContain(racine);
      expect(comp.stratesSharedCollection).toContain(strate);
      expect(comp.feuillagesSharedCollection).toContain(feuillage);
      expect(comp.nomVernaculairesSharedCollection).toContain(nomsVernaculaires);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Plante>>();
      const plante = { id: 123 };
      jest.spyOn(planteService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: plante }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(planteService.update).toHaveBeenCalledWith(plante);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Plante>>();
      const plante = new Plante();
      jest.spyOn(planteService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: plante }));
      saveSubject.complete();

      // THEN
      expect(planteService.create).toHaveBeenCalledWith(plante);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Plante>>();
      const plante = { id: 123 };
      jest.spyOn(planteService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(planteService.update).toHaveBeenCalledWith(plante);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackPlanteById', () => {
      it('Should return tracked Plante primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPlanteById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackCycleDeVieById', () => {
      it('Should return tracked CycleDeVie primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackCycleDeVieById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackSolById', () => {
      it('Should return tracked Sol primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackSolById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackTemperatureById', () => {
      it('Should return tracked Temperature primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTemperatureById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackRacineById', () => {
      it('Should return tracked Racine primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackRacineById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackStrateById', () => {
      it('Should return tracked Strate primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackStrateById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackFeuillageById', () => {
      it('Should return tracked Feuillage primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackFeuillageById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackNomVernaculaireById', () => {
      it('Should return tracked NomVernaculaire primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackNomVernaculaireById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedNomVernaculaire', () => {
      it('Should return option if no NomVernaculaire is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedNomVernaculaire(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected NomVernaculaire for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedNomVernaculaire(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this NomVernaculaire is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedNomVernaculaire(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
