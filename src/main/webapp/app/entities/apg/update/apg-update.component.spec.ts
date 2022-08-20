import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {from, of, Subject} from 'rxjs';

import {APGService} from '../service/apg.service';
import {APG, IAPG} from '../apg.model';
import {IClassification} from 'app/entities/classification/classification.model';
import {ClassificationService} from 'app/entities/classification/service/classification.service';

import {APGUpdateComponent} from './apg-update.component';

describe('APG Management Update Component', () => {
  let comp: APGUpdateComponent;
  let fixture: ComponentFixture<APGUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let aPGService: APGService;
  let classificationService: ClassificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [APGUpdateComponent],
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
      .overrideTemplate(APGUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(APGUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    aPGService = TestBed.inject(APGService);
    classificationService = TestBed.inject(ClassificationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call classification query and add missing value', () => {
      const aPG: IAPG = { id: 456 };
      const classification: IClassification = { id: 71434 };
      aPG.classification = classification;

      const classificationCollection: IClassification[] = [{ id: 79734 }];
      jest.spyOn(classificationService, 'query').mockReturnValue(of(new HttpResponse({ body: classificationCollection })));
      const expectedCollection: IClassification[] = [classification, ...classificationCollection];
      jest.spyOn(classificationService, 'addClassificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ aPG });
      comp.ngOnInit();

      expect(classificationService.query).toHaveBeenCalled();
      expect(classificationService.addClassificationToCollectionIfMissing).toHaveBeenCalledWith(classificationCollection, classification);
      expect(comp.classificationsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const aPG: IAPG = { id: 456 };
      const classification: IClassification = { id: 87154 };
      aPG.classification = classification;

      activatedRoute.data = of({ aPG });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(aPG));
      expect(comp.classificationsCollection).toContain(classification);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<APG>>();
      const aPG = { id: 123 };
      jest.spyOn(aPGService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ aPG });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: aPG }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(aPGService.update).toHaveBeenCalledWith(aPG);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<APG>>();
      const aPG = new APG();
      jest.spyOn(aPGService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ aPG });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: aPG }));
      saveSubject.complete();

      // THEN
      expect(aPGService.create).toHaveBeenCalledWith(aPG);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<APG>>();
      const aPG = { id: 123 };
      jest.spyOn(aPGService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ aPG });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(aPGService.update).toHaveBeenCalledWith(aPG);
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
