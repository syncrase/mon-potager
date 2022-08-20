import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TakhtajanService } from '../service/takhtajan.service';
import { ITakhtajan, Takhtajan } from '../takhtajan.model';
import { IClassification } from 'app/entities/classification/classification.model';
import { ClassificationService } from 'app/entities/classification/service/classification.service';

import { TakhtajanUpdateComponent } from './takhtajan-update.component';

describe('Takhtajan Management Update Component', () => {
  let comp: TakhtajanUpdateComponent;
  let fixture: ComponentFixture<TakhtajanUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let takhtajanService: TakhtajanService;
  let classificationService: ClassificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TakhtajanUpdateComponent],
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
      .overrideTemplate(TakhtajanUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TakhtajanUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    takhtajanService = TestBed.inject(TakhtajanService);
    classificationService = TestBed.inject(ClassificationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call classification query and add missing value', () => {
      const takhtajan: ITakhtajan = { id: 456 };
      const classification: IClassification = { id: 52615 };
      takhtajan.classification = classification;

      const classificationCollection: IClassification[] = [{ id: 23742 }];
      jest.spyOn(classificationService, 'query').mockReturnValue(of(new HttpResponse({ body: classificationCollection })));
      const expectedCollection: IClassification[] = [classification, ...classificationCollection];
      jest.spyOn(classificationService, 'addClassificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ takhtajan });
      comp.ngOnInit();

      expect(classificationService.query).toHaveBeenCalled();
      expect(classificationService.addClassificationToCollectionIfMissing).toHaveBeenCalledWith(classificationCollection, classification);
      expect(comp.classificationsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const takhtajan: ITakhtajan = { id: 456 };
      const classification: IClassification = { id: 85178 };
      takhtajan.classification = classification;

      activatedRoute.data = of({ takhtajan });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(takhtajan));
      expect(comp.classificationsCollection).toContain(classification);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Takhtajan>>();
      const takhtajan = { id: 123 };
      jest.spyOn(takhtajanService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ takhtajan });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: takhtajan }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(takhtajanService.update).toHaveBeenCalledWith(takhtajan);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Takhtajan>>();
      const takhtajan = new Takhtajan();
      jest.spyOn(takhtajanService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ takhtajan });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: takhtajan }));
      saveSubject.complete();

      // THEN
      expect(takhtajanService.create).toHaveBeenCalledWith(takhtajan);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Takhtajan>>();
      const takhtajan = { id: 123 };
      jest.spyOn(takhtajanService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ takhtajan });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(takhtajanService.update).toHaveBeenCalledWith(takhtajan);
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
