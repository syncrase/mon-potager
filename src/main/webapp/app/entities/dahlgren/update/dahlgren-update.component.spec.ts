import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {from, of, Subject} from 'rxjs';

import {DahlgrenService} from '../service/dahlgren.service';
import {Dahlgren, IDahlgren} from '../dahlgren.model';
import {IClassification} from 'app/entities/classification/classification.model';
import {ClassificationService} from 'app/entities/classification/service/classification.service';

import {DahlgrenUpdateComponent} from './dahlgren-update.component';

describe('Dahlgren Management Update Component', () => {
  let comp: DahlgrenUpdateComponent;
  let fixture: ComponentFixture<DahlgrenUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let dahlgrenService: DahlgrenService;
  let classificationService: ClassificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DahlgrenUpdateComponent],
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
      .overrideTemplate(DahlgrenUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DahlgrenUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    dahlgrenService = TestBed.inject(DahlgrenService);
    classificationService = TestBed.inject(ClassificationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call classification query and add missing value', () => {
      const dahlgren: IDahlgren = { id: 456 };
      const classification: IClassification = { id: 77758 };
      dahlgren.classification = classification;

      const classificationCollection: IClassification[] = [{ id: 11125 }];
      jest.spyOn(classificationService, 'query').mockReturnValue(of(new HttpResponse({ body: classificationCollection })));
      const expectedCollection: IClassification[] = [classification, ...classificationCollection];
      jest.spyOn(classificationService, 'addClassificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ dahlgren });
      comp.ngOnInit();

      expect(classificationService.query).toHaveBeenCalled();
      expect(classificationService.addClassificationToCollectionIfMissing).toHaveBeenCalledWith(classificationCollection, classification);
      expect(comp.classificationsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const dahlgren: IDahlgren = { id: 456 };
      const classification: IClassification = { id: 36272 };
      dahlgren.classification = classification;

      activatedRoute.data = of({ dahlgren });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(dahlgren));
      expect(comp.classificationsCollection).toContain(classification);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Dahlgren>>();
      const dahlgren = { id: 123 };
      jest.spyOn(dahlgrenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dahlgren });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dahlgren }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(dahlgrenService.update).toHaveBeenCalledWith(dahlgren);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Dahlgren>>();
      const dahlgren = new Dahlgren();
      jest.spyOn(dahlgrenService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dahlgren });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dahlgren }));
      saveSubject.complete();

      // THEN
      expect(dahlgrenService.create).toHaveBeenCalledWith(dahlgren);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Dahlgren>>();
      const dahlgren = { id: 123 };
      jest.spyOn(dahlgrenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dahlgren });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(dahlgrenService.update).toHaveBeenCalledWith(dahlgren);
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
