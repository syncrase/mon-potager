import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EnglerService } from '../service/engler.service';
import { IEngler, Engler } from '../engler.model';
import { IClassification } from 'app/entities/classification/classification.model';
import { ClassificationService } from 'app/entities/classification/service/classification.service';

import { EnglerUpdateComponent } from './engler-update.component';

describe('Engler Management Update Component', () => {
  let comp: EnglerUpdateComponent;
  let fixture: ComponentFixture<EnglerUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let englerService: EnglerService;
  let classificationService: ClassificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EnglerUpdateComponent],
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
      .overrideTemplate(EnglerUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EnglerUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    englerService = TestBed.inject(EnglerService);
    classificationService = TestBed.inject(ClassificationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call classification query and add missing value', () => {
      const engler: IEngler = { id: 456 };
      const classification: IClassification = { id: 31402 };
      engler.classification = classification;

      const classificationCollection: IClassification[] = [{ id: 42068 }];
      jest.spyOn(classificationService, 'query').mockReturnValue(of(new HttpResponse({ body: classificationCollection })));
      const expectedCollection: IClassification[] = [classification, ...classificationCollection];
      jest.spyOn(classificationService, 'addClassificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ engler });
      comp.ngOnInit();

      expect(classificationService.query).toHaveBeenCalled();
      expect(classificationService.addClassificationToCollectionIfMissing).toHaveBeenCalledWith(classificationCollection, classification);
      expect(comp.classificationsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const engler: IEngler = { id: 456 };
      const classification: IClassification = { id: 6335 };
      engler.classification = classification;

      activatedRoute.data = of({ engler });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(engler));
      expect(comp.classificationsCollection).toContain(classification);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Engler>>();
      const engler = { id: 123 };
      jest.spyOn(englerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ engler });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: engler }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(englerService.update).toHaveBeenCalledWith(engler);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Engler>>();
      const engler = new Engler();
      jest.spyOn(englerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ engler });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: engler }));
      saveSubject.complete();

      // THEN
      expect(englerService.create).toHaveBeenCalledWith(engler);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Engler>>();
      const engler = { id: 123 };
      jest.spyOn(englerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ engler });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(englerService.update).toHaveBeenCalledWith(engler);
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
