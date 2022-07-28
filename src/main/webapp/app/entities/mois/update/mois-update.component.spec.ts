import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MoisService } from '../service/mois.service';
import { IMois, Mois } from '../mois.model';

import { MoisUpdateComponent } from './mois-update.component';

describe('Mois Management Update Component', () => {
  let comp: MoisUpdateComponent;
  let fixture: ComponentFixture<MoisUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let moisService: MoisService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MoisUpdateComponent],
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
      .overrideTemplate(MoisUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MoisUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    moisService = TestBed.inject(MoisService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const mois: IMois = { id: 456 };

      activatedRoute.data = of({ mois });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(mois));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Mois>>();
      const mois = { id: 123 };
      jest.spyOn(moisService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mois });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mois }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(moisService.update).toHaveBeenCalledWith(mois);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Mois>>();
      const mois = new Mois();
      jest.spyOn(moisService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mois });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mois }));
      saveSubject.complete();

      // THEN
      expect(moisService.create).toHaveBeenCalledWith(mois);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Mois>>();
      const mois = { id: 123 };
      jest.spyOn(moisService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mois });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(moisService.update).toHaveBeenCalledWith(mois);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
