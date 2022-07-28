import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { StrateService } from '../service/strate.service';
import { IStrate, Strate } from '../strate.model';

import { StrateUpdateComponent } from './strate-update.component';

describe('Strate Management Update Component', () => {
  let comp: StrateUpdateComponent;
  let fixture: ComponentFixture<StrateUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let strateService: StrateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [StrateUpdateComponent],
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
      .overrideTemplate(StrateUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StrateUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    strateService = TestBed.inject(StrateService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const strate: IStrate = { id: 456 };

      activatedRoute.data = of({ strate });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(strate));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Strate>>();
      const strate = { id: 123 };
      jest.spyOn(strateService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ strate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: strate }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(strateService.update).toHaveBeenCalledWith(strate);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Strate>>();
      const strate = new Strate();
      jest.spyOn(strateService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ strate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: strate }));
      saveSubject.complete();

      // THEN
      expect(strateService.create).toHaveBeenCalledWith(strate);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Strate>>();
      const strate = { id: 123 };
      jest.spyOn(strateService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ strate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(strateService.update).toHaveBeenCalledWith(strate);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
