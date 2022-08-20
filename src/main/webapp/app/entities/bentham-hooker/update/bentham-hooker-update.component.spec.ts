import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {from, of, Subject} from 'rxjs';

import {BenthamHookerService} from '../service/bentham-hooker.service';
import {BenthamHooker, IBenthamHooker} from '../bentham-hooker.model';
import {IClassification} from 'app/entities/classification/classification.model';
import {ClassificationService} from 'app/entities/classification/service/classification.service';

import {BenthamHookerUpdateComponent} from './bentham-hooker-update.component';

describe('BenthamHooker Management Update Component', () => {
  let comp: BenthamHookerUpdateComponent;
  let fixture: ComponentFixture<BenthamHookerUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let benthamHookerService: BenthamHookerService;
  let classificationService: ClassificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BenthamHookerUpdateComponent],
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
      .overrideTemplate(BenthamHookerUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BenthamHookerUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    benthamHookerService = TestBed.inject(BenthamHookerService);
    classificationService = TestBed.inject(ClassificationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call classification query and add missing value', () => {
      const benthamHooker: IBenthamHooker = { id: 456 };
      const classification: IClassification = { id: 44293 };
      benthamHooker.classification = classification;

      const classificationCollection: IClassification[] = [{ id: 37257 }];
      jest.spyOn(classificationService, 'query').mockReturnValue(of(new HttpResponse({ body: classificationCollection })));
      const expectedCollection: IClassification[] = [classification, ...classificationCollection];
      jest.spyOn(classificationService, 'addClassificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ benthamHooker });
      comp.ngOnInit();

      expect(classificationService.query).toHaveBeenCalled();
      expect(classificationService.addClassificationToCollectionIfMissing).toHaveBeenCalledWith(classificationCollection, classification);
      expect(comp.classificationsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const benthamHooker: IBenthamHooker = { id: 456 };
      const classification: IClassification = { id: 53419 };
      benthamHooker.classification = classification;

      activatedRoute.data = of({ benthamHooker });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(benthamHooker));
      expect(comp.classificationsCollection).toContain(classification);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<BenthamHooker>>();
      const benthamHooker = { id: 123 };
      jest.spyOn(benthamHookerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ benthamHooker });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: benthamHooker }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(benthamHookerService.update).toHaveBeenCalledWith(benthamHooker);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<BenthamHooker>>();
      const benthamHooker = new BenthamHooker();
      jest.spyOn(benthamHookerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ benthamHooker });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: benthamHooker }));
      saveSubject.complete();

      // THEN
      expect(benthamHookerService.create).toHaveBeenCalledWith(benthamHooker);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<BenthamHooker>>();
      const benthamHooker = { id: 123 };
      jest.spyOn(benthamHookerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ benthamHooker });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(benthamHookerService.update).toHaveBeenCalledWith(benthamHooker);
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
