import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { WettsteinService } from '../service/wettstein.service';
import { IWettstein, Wettstein } from '../wettstein.model';
import { IClassification } from 'app/entities/classification/classification.model';
import { ClassificationService } from 'app/entities/classification/service/classification.service';

import { WettsteinUpdateComponent } from './wettstein-update.component';

describe('Wettstein Management Update Component', () => {
  let comp: WettsteinUpdateComponent;
  let fixture: ComponentFixture<WettsteinUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let wettsteinService: WettsteinService;
  let classificationService: ClassificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [WettsteinUpdateComponent],
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
      .overrideTemplate(WettsteinUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WettsteinUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    wettsteinService = TestBed.inject(WettsteinService);
    classificationService = TestBed.inject(ClassificationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call classification query and add missing value', () => {
      const wettstein: IWettstein = { id: 456 };
      const classification: IClassification = { id: 45043 };
      wettstein.classification = classification;

      const classificationCollection: IClassification[] = [{ id: 45554 }];
      jest.spyOn(classificationService, 'query').mockReturnValue(of(new HttpResponse({ body: classificationCollection })));
      const expectedCollection: IClassification[] = [classification, ...classificationCollection];
      jest.spyOn(classificationService, 'addClassificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ wettstein });
      comp.ngOnInit();

      expect(classificationService.query).toHaveBeenCalled();
      expect(classificationService.addClassificationToCollectionIfMissing).toHaveBeenCalledWith(classificationCollection, classification);
      expect(comp.classificationsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const wettstein: IWettstein = { id: 456 };
      const classification: IClassification = { id: 23795 };
      wettstein.classification = classification;

      activatedRoute.data = of({ wettstein });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(wettstein));
      expect(comp.classificationsCollection).toContain(classification);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Wettstein>>();
      const wettstein = { id: 123 };
      jest.spyOn(wettsteinService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ wettstein });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: wettstein }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(wettsteinService.update).toHaveBeenCalledWith(wettstein);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Wettstein>>();
      const wettstein = new Wettstein();
      jest.spyOn(wettsteinService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ wettstein });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: wettstein }));
      saveSubject.complete();

      // THEN
      expect(wettsteinService.create).toHaveBeenCalledWith(wettstein);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Wettstein>>();
      const wettstein = { id: 123 };
      jest.spyOn(wettsteinService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ wettstein });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(wettsteinService.update).toHaveBeenCalledWith(wettstein);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackClassificationById', () => {
      it('Should return tracked Classification primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackClassificationById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
