import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PeriodeAnneeService } from '../service/periode-annee.service';
import { IPeriodeAnnee, PeriodeAnnee } from '../periode-annee.model';
import { IMois } from 'app/entities/mois/mois.model';
import { MoisService } from 'app/entities/mois/service/mois.service';

import { PeriodeAnneeUpdateComponent } from './periode-annee-update.component';

describe('PeriodeAnnee Management Update Component', () => {
  let comp: PeriodeAnneeUpdateComponent;
  let fixture: ComponentFixture<PeriodeAnneeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let periodeAnneeService: PeriodeAnneeService;
  let moisService: MoisService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PeriodeAnneeUpdateComponent],
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
      .overrideTemplate(PeriodeAnneeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PeriodeAnneeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    periodeAnneeService = TestBed.inject(PeriodeAnneeService);
    moisService = TestBed.inject(MoisService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Mois query and add missing value', () => {
      const periodeAnnee: IPeriodeAnnee = { id: 456 };
      const debut: IMois = { id: 8388 };
      periodeAnnee.debut = debut;
      const fin: IMois = { id: 33370 };
      periodeAnnee.fin = fin;

      const moisCollection: IMois[] = [{ id: 3740 }];
      jest.spyOn(moisService, 'query').mockReturnValue(of(new HttpResponse({ body: moisCollection })));
      const additionalMois = [debut, fin];
      const expectedCollection: IMois[] = [...additionalMois, ...moisCollection];
      jest.spyOn(moisService, 'addMoisToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ periodeAnnee });
      comp.ngOnInit();

      expect(moisService.query).toHaveBeenCalled();
      expect(moisService.addMoisToCollectionIfMissing).toHaveBeenCalledWith(moisCollection, ...additionalMois);
      expect(comp.moisSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const periodeAnnee: IPeriodeAnnee = { id: 456 };
      const debut: IMois = { id: 1317 };
      periodeAnnee.debut = debut;
      const fin: IMois = { id: 13355 };
      periodeAnnee.fin = fin;

      activatedRoute.data = of({ periodeAnnee });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(periodeAnnee));
      expect(comp.moisSharedCollection).toContain(debut);
      expect(comp.moisSharedCollection).toContain(fin);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<PeriodeAnnee>>();
      const periodeAnnee = { id: 123 };
      jest.spyOn(periodeAnneeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ periodeAnnee });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: periodeAnnee }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(periodeAnneeService.update).toHaveBeenCalledWith(periodeAnnee);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<PeriodeAnnee>>();
      const periodeAnnee = new PeriodeAnnee();
      jest.spyOn(periodeAnneeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ periodeAnnee });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: periodeAnnee }));
      saveSubject.complete();

      // THEN
      expect(periodeAnneeService.create).toHaveBeenCalledWith(periodeAnnee);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<PeriodeAnnee>>();
      const periodeAnnee = { id: 123 };
      jest.spyOn(periodeAnneeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ periodeAnnee });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(periodeAnneeService.update).toHaveBeenCalledWith(periodeAnnee);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackMoisById', () => {
      it('Should return tracked Mois primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackMoisById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
