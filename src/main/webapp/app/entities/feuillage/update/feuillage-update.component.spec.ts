import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FeuillageService } from '../service/feuillage.service';
import { IFeuillage, Feuillage } from '../feuillage.model';

import { FeuillageUpdateComponent } from './feuillage-update.component';

describe('Feuillage Management Update Component', () => {
  let comp: FeuillageUpdateComponent;
  let fixture: ComponentFixture<FeuillageUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let feuillageService: FeuillageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FeuillageUpdateComponent],
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
      .overrideTemplate(FeuillageUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FeuillageUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    feuillageService = TestBed.inject(FeuillageService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const feuillage: IFeuillage = { id: 456 };

      activatedRoute.data = of({ feuillage });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(feuillage));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Feuillage>>();
      const feuillage = { id: 123 };
      jest.spyOn(feuillageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ feuillage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: feuillage }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(feuillageService.update).toHaveBeenCalledWith(feuillage);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Feuillage>>();
      const feuillage = new Feuillage();
      jest.spyOn(feuillageService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ feuillage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: feuillage }));
      saveSubject.complete();

      // THEN
      expect(feuillageService.create).toHaveBeenCalledWith(feuillage);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Feuillage>>();
      const feuillage = { id: 123 };
      jest.spyOn(feuillageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ feuillage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(feuillageService.update).toHaveBeenCalledWith(feuillage);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
