import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EnsoleillementService } from '../service/ensoleillement.service';
import { IEnsoleillement, Ensoleillement } from '../ensoleillement.model';
import { IPlante } from 'app/entities/plante/plante.model';
import { PlanteService } from 'app/entities/plante/service/plante.service';

import { EnsoleillementUpdateComponent } from './ensoleillement-update.component';

describe('Ensoleillement Management Update Component', () => {
  let comp: EnsoleillementUpdateComponent;
  let fixture: ComponentFixture<EnsoleillementUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let ensoleillementService: EnsoleillementService;
  let planteService: PlanteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EnsoleillementUpdateComponent],
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
      .overrideTemplate(EnsoleillementUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EnsoleillementUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ensoleillementService = TestBed.inject(EnsoleillementService);
    planteService = TestBed.inject(PlanteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Plante query and add missing value', () => {
      const ensoleillement: IEnsoleillement = { id: 456 };
      const plante: IPlante = { id: 57804 };
      ensoleillement.plante = plante;

      const planteCollection: IPlante[] = [{ id: 46074 }];
      jest.spyOn(planteService, 'query').mockReturnValue(of(new HttpResponse({ body: planteCollection })));
      const additionalPlantes = [plante];
      const expectedCollection: IPlante[] = [...additionalPlantes, ...planteCollection];
      jest.spyOn(planteService, 'addPlanteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ensoleillement });
      comp.ngOnInit();

      expect(planteService.query).toHaveBeenCalled();
      expect(planteService.addPlanteToCollectionIfMissing).toHaveBeenCalledWith(planteCollection, ...additionalPlantes);
      expect(comp.plantesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const ensoleillement: IEnsoleillement = { id: 456 };
      const plante: IPlante = { id: 4311 };
      ensoleillement.plante = plante;

      activatedRoute.data = of({ ensoleillement });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(ensoleillement));
      expect(comp.plantesSharedCollection).toContain(plante);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ensoleillement>>();
      const ensoleillement = { id: 123 };
      jest.spyOn(ensoleillementService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ensoleillement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ensoleillement }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(ensoleillementService.update).toHaveBeenCalledWith(ensoleillement);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ensoleillement>>();
      const ensoleillement = new Ensoleillement();
      jest.spyOn(ensoleillementService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ensoleillement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ensoleillement }));
      saveSubject.complete();

      // THEN
      expect(ensoleillementService.create).toHaveBeenCalledWith(ensoleillement);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ensoleillement>>();
      const ensoleillement = { id: 123 };
      jest.spyOn(ensoleillementService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ensoleillement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ensoleillementService.update).toHaveBeenCalledWith(ensoleillement);
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
