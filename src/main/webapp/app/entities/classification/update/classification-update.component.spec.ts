import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {from, of, Subject} from 'rxjs';

import {ClassificationService} from '../service/classification.service';
import {Classification, IClassification} from '../classification.model';

import {ClassificationUpdateComponent} from './classification-update.component';

describe('Classification Management Update Component', () => {
  let comp: ClassificationUpdateComponent;
  let fixture: ComponentFixture<ClassificationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let classificationService: ClassificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ClassificationUpdateComponent],
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
      .overrideTemplate(ClassificationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClassificationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    classificationService = TestBed.inject(ClassificationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const classification: IClassification = { id: 456 };

      activatedRoute.data = of({ classification });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(classification));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Classification>>();
      const classification = { id: 123 };
      jest.spyOn(classificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ classification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: classification }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(classificationService.update).toHaveBeenCalledWith(classification);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Classification>>();
      const classification = new Classification();
      jest.spyOn(classificationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ classification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: classification }));
      saveSubject.complete();

      // THEN
      expect(classificationService.create).toHaveBeenCalledWith(classification);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Classification>>();
      const classification = { id: 123 };
      jest.spyOn(classificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ classification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(classificationService.update).toHaveBeenCalledWith(classification);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
