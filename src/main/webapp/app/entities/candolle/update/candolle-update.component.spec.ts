import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {from, of, Subject} from 'rxjs';

import {CandolleService} from '../service/candolle.service';
import {Candolle, ICandolle} from '../candolle.model';
import {IClassification} from 'app/entities/classification/classification.model';
import {ClassificationService} from 'app/entities/classification/service/classification.service';

import {CandolleUpdateComponent} from './candolle-update.component';

describe('Candolle Management Update Component', () => {
  let comp: CandolleUpdateComponent;
  let fixture: ComponentFixture<CandolleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let candolleService: CandolleService;
  let classificationService: ClassificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CandolleUpdateComponent],
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
      .overrideTemplate(CandolleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CandolleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    candolleService = TestBed.inject(CandolleService);
    classificationService = TestBed.inject(ClassificationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call classification query and add missing value', () => {
      const candolle: ICandolle = { id: 456 };
      const classification: IClassification = { id: 49609 };
      candolle.classification = classification;

      const classificationCollection: IClassification[] = [{ id: 80410 }];
      jest.spyOn(classificationService, 'query').mockReturnValue(of(new HttpResponse({ body: classificationCollection })));
      const expectedCollection: IClassification[] = [classification, ...classificationCollection];
      jest.spyOn(classificationService, 'addClassificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ candolle });
      comp.ngOnInit();

      expect(classificationService.query).toHaveBeenCalled();
      expect(classificationService.addClassificationToCollectionIfMissing).toHaveBeenCalledWith(classificationCollection, classification);
      expect(comp.classificationsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const candolle: ICandolle = { id: 456 };
      const classification: IClassification = { id: 68983 };
      candolle.classification = classification;

      activatedRoute.data = of({ candolle });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(candolle));
      expect(comp.classificationsCollection).toContain(classification);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Candolle>>();
      const candolle = { id: 123 };
      jest.spyOn(candolleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ candolle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: candolle }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(candolleService.update).toHaveBeenCalledWith(candolle);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Candolle>>();
      const candolle = new Candolle();
      jest.spyOn(candolleService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ candolle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: candolle }));
      saveSubject.complete();

      // THEN
      expect(candolleService.create).toHaveBeenCalledWith(candolle);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Candolle>>();
      const candolle = { id: 123 };
      jest.spyOn(candolleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ candolle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(candolleService.update).toHaveBeenCalledWith(candolle);
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
