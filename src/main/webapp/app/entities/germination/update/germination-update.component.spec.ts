import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GerminationService } from '../service/germination.service';
import { IGermination, Germination } from '../germination.model';

import { GerminationUpdateComponent } from './germination-update.component';

describe('Germination Management Update Component', () => {
  let comp: GerminationUpdateComponent;
  let fixture: ComponentFixture<GerminationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let germinationService: GerminationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GerminationUpdateComponent],
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
      .overrideTemplate(GerminationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GerminationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    germinationService = TestBed.inject(GerminationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const germination: IGermination = { id: 456 };

      activatedRoute.data = of({ germination });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(germination));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Germination>>();
      const germination = { id: 123 };
      jest.spyOn(germinationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ germination });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: germination }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(germinationService.update).toHaveBeenCalledWith(germination);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Germination>>();
      const germination = new Germination();
      jest.spyOn(germinationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ germination });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: germination }));
      saveSubject.complete();

      // THEN
      expect(germinationService.create).toHaveBeenCalledWith(germination);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Germination>>();
      const germination = { id: 123 };
      jest.spyOn(germinationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ germination });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(germinationService.update).toHaveBeenCalledWith(germination);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
