import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ReproductionService } from '../service/reproduction.service';
import { IReproduction, Reproduction } from '../reproduction.model';

import { ReproductionUpdateComponent } from './reproduction-update.component';

describe('Reproduction Management Update Component', () => {
  let comp: ReproductionUpdateComponent;
  let fixture: ComponentFixture<ReproductionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let reproductionService: ReproductionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ReproductionUpdateComponent],
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
      .overrideTemplate(ReproductionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReproductionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    reproductionService = TestBed.inject(ReproductionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const reproduction: IReproduction = { id: 456 };

      activatedRoute.data = of({ reproduction });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(reproduction));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Reproduction>>();
      const reproduction = { id: 123 };
      jest.spyOn(reproductionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reproduction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reproduction }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(reproductionService.update).toHaveBeenCalledWith(reproduction);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Reproduction>>();
      const reproduction = new Reproduction();
      jest.spyOn(reproductionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reproduction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reproduction }));
      saveSubject.complete();

      // THEN
      expect(reproductionService.create).toHaveBeenCalledWith(reproduction);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Reproduction>>();
      const reproduction = { id: 123 };
      jest.spyOn(reproductionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reproduction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(reproductionService.update).toHaveBeenCalledWith(reproduction);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
