import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RessemblanceService } from '../service/ressemblance.service';
import { IRessemblance, Ressemblance } from '../ressemblance.model';
import { IPlante } from 'app/entities/plante/plante.model';
import { PlanteService } from 'app/entities/plante/service/plante.service';

import { RessemblanceUpdateComponent } from './ressemblance-update.component';

describe('Ressemblance Management Update Component', () => {
  let comp: RessemblanceUpdateComponent;
  let fixture: ComponentFixture<RessemblanceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let ressemblanceService: RessemblanceService;
  let planteService: PlanteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RessemblanceUpdateComponent],
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
      .overrideTemplate(RessemblanceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RessemblanceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ressemblanceService = TestBed.inject(RessemblanceService);
    planteService = TestBed.inject(PlanteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Plante query and add missing value', () => {
      const ressemblance: IRessemblance = { id: 456 };
      const planteRessemblant: IPlante = { id: 63187 };
      ressemblance.planteRessemblant = planteRessemblant;

      const planteCollection: IPlante[] = [{ id: 16561 }];
      jest.spyOn(planteService, 'query').mockReturnValue(of(new HttpResponse({ body: planteCollection })));
      const additionalPlantes = [planteRessemblant];
      const expectedCollection: IPlante[] = [...additionalPlantes, ...planteCollection];
      jest.spyOn(planteService, 'addPlanteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ressemblance });
      comp.ngOnInit();

      expect(planteService.query).toHaveBeenCalled();
      expect(planteService.addPlanteToCollectionIfMissing).toHaveBeenCalledWith(planteCollection, ...additionalPlantes);
      expect(comp.plantesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const ressemblance: IRessemblance = { id: 456 };
      const planteRessemblant: IPlante = { id: 74404 };
      ressemblance.planteRessemblant = planteRessemblant;

      activatedRoute.data = of({ ressemblance });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(ressemblance));
      expect(comp.plantesSharedCollection).toContain(planteRessemblant);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ressemblance>>();
      const ressemblance = { id: 123 };
      jest.spyOn(ressemblanceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ressemblance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ressemblance }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(ressemblanceService.update).toHaveBeenCalledWith(ressemblance);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ressemblance>>();
      const ressemblance = new Ressemblance();
      jest.spyOn(ressemblanceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ressemblance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ressemblance }));
      saveSubject.complete();

      // THEN
      expect(ressemblanceService.create).toHaveBeenCalledWith(ressemblance);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ressemblance>>();
      const ressemblance = { id: 123 };
      jest.spyOn(ressemblanceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ressemblance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ressemblanceService.update).toHaveBeenCalledWith(ressemblance);
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
  });
});
