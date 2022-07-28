import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TypeSemisService } from '../service/type-semis.service';
import { ITypeSemis, TypeSemis } from '../type-semis.model';

import { TypeSemisUpdateComponent } from './type-semis-update.component';

describe('TypeSemis Management Update Component', () => {
  let comp: TypeSemisUpdateComponent;
  let fixture: ComponentFixture<TypeSemisUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let typeSemisService: TypeSemisService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TypeSemisUpdateComponent],
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
      .overrideTemplate(TypeSemisUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TypeSemisUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    typeSemisService = TestBed.inject(TypeSemisService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const typeSemis: ITypeSemis = { id: 456 };

      activatedRoute.data = of({ typeSemis });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(typeSemis));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TypeSemis>>();
      const typeSemis = { id: 123 };
      jest.spyOn(typeSemisService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ typeSemis });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: typeSemis }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(typeSemisService.update).toHaveBeenCalledWith(typeSemis);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TypeSemis>>();
      const typeSemis = new TypeSemis();
      jest.spyOn(typeSemisService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ typeSemis });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: typeSemis }));
      saveSubject.complete();

      // THEN
      expect(typeSemisService.create).toHaveBeenCalledWith(typeSemis);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TypeSemis>>();
      const typeSemis = { id: 123 };
      jest.spyOn(typeSemisService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ typeSemis });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(typeSemisService.update).toHaveBeenCalledWith(typeSemis);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
