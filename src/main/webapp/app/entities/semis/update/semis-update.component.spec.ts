import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SemisService } from '../service/semis.service';
import { ISemis, Semis } from '../semis.model';
import { IPeriodeAnnee } from 'app/entities/periode-annee/periode-annee.model';
import { PeriodeAnneeService } from 'app/entities/periode-annee/service/periode-annee.service';
import { ITypeSemis } from 'app/entities/type-semis/type-semis.model';
import { TypeSemisService } from 'app/entities/type-semis/service/type-semis.service';
import { IGermination } from 'app/entities/germination/germination.model';
import { GerminationService } from 'app/entities/germination/service/germination.service';

import { SemisUpdateComponent } from './semis-update.component';

describe('Semis Management Update Component', () => {
  let comp: SemisUpdateComponent;
  let fixture: ComponentFixture<SemisUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let semisService: SemisService;
  let periodeAnneeService: PeriodeAnneeService;
  let typeSemisService: TypeSemisService;
  let germinationService: GerminationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SemisUpdateComponent],
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
      .overrideTemplate(SemisUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SemisUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    semisService = TestBed.inject(SemisService);
    periodeAnneeService = TestBed.inject(PeriodeAnneeService);
    typeSemisService = TestBed.inject(TypeSemisService);
    germinationService = TestBed.inject(GerminationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call PeriodeAnnee query and add missing value', () => {
      const semis: ISemis = { id: 456 };
      const semisPleineTerre: IPeriodeAnnee = { id: 87356 };
      semis.semisPleineTerre = semisPleineTerre;
      const semisSousAbris: IPeriodeAnnee = { id: 55917 };
      semis.semisSousAbris = semisSousAbris;

      const periodeAnneeCollection: IPeriodeAnnee[] = [{ id: 17874 }];
      jest.spyOn(periodeAnneeService, 'query').mockReturnValue(of(new HttpResponse({ body: periodeAnneeCollection })));
      const additionalPeriodeAnnees = [semisPleineTerre, semisSousAbris];
      const expectedCollection: IPeriodeAnnee[] = [...additionalPeriodeAnnees, ...periodeAnneeCollection];
      jest.spyOn(periodeAnneeService, 'addPeriodeAnneeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ semis });
      comp.ngOnInit();

      expect(periodeAnneeService.query).toHaveBeenCalled();
      expect(periodeAnneeService.addPeriodeAnneeToCollectionIfMissing).toHaveBeenCalledWith(
        periodeAnneeCollection,
        ...additionalPeriodeAnnees
      );
      expect(comp.periodeAnneesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call TypeSemis query and add missing value', () => {
      const semis: ISemis = { id: 456 };
      const typeSemis: ITypeSemis = { id: 20363 };
      semis.typeSemis = typeSemis;

      const typeSemisCollection: ITypeSemis[] = [{ id: 49807 }];
      jest.spyOn(typeSemisService, 'query').mockReturnValue(of(new HttpResponse({ body: typeSemisCollection })));
      const additionalTypeSemis = [typeSemis];
      const expectedCollection: ITypeSemis[] = [...additionalTypeSemis, ...typeSemisCollection];
      jest.spyOn(typeSemisService, 'addTypeSemisToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ semis });
      comp.ngOnInit();

      expect(typeSemisService.query).toHaveBeenCalled();
      expect(typeSemisService.addTypeSemisToCollectionIfMissing).toHaveBeenCalledWith(typeSemisCollection, ...additionalTypeSemis);
      expect(comp.typeSemisSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Germination query and add missing value', () => {
      const semis: ISemis = { id: 456 };
      const germination: IGermination = { id: 2343 };
      semis.germination = germination;

      const germinationCollection: IGermination[] = [{ id: 62208 }];
      jest.spyOn(germinationService, 'query').mockReturnValue(of(new HttpResponse({ body: germinationCollection })));
      const additionalGerminations = [germination];
      const expectedCollection: IGermination[] = [...additionalGerminations, ...germinationCollection];
      jest.spyOn(germinationService, 'addGerminationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ semis });
      comp.ngOnInit();

      expect(germinationService.query).toHaveBeenCalled();
      expect(germinationService.addGerminationToCollectionIfMissing).toHaveBeenCalledWith(germinationCollection, ...additionalGerminations);
      expect(comp.germinationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const semis: ISemis = { id: 456 };
      const semisPleineTerre: IPeriodeAnnee = { id: 24283 };
      semis.semisPleineTerre = semisPleineTerre;
      const semisSousAbris: IPeriodeAnnee = { id: 44741 };
      semis.semisSousAbris = semisSousAbris;
      const typeSemis: ITypeSemis = { id: 70645 };
      semis.typeSemis = typeSemis;
      const germination: IGermination = { id: 13708 };
      semis.germination = germination;

      activatedRoute.data = of({ semis });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(semis));
      expect(comp.periodeAnneesSharedCollection).toContain(semisPleineTerre);
      expect(comp.periodeAnneesSharedCollection).toContain(semisSousAbris);
      expect(comp.typeSemisSharedCollection).toContain(typeSemis);
      expect(comp.germinationsSharedCollection).toContain(germination);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Semis>>();
      const semis = { id: 123 };
      jest.spyOn(semisService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ semis });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: semis }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(semisService.update).toHaveBeenCalledWith(semis);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Semis>>();
      const semis = new Semis();
      jest.spyOn(semisService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ semis });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: semis }));
      saveSubject.complete();

      // THEN
      expect(semisService.create).toHaveBeenCalledWith(semis);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Semis>>();
      const semis = { id: 123 };
      jest.spyOn(semisService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ semis });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(semisService.update).toHaveBeenCalledWith(semis);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackPeriodeAnneeById', () => {
      it('Should return tracked PeriodeAnnee primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPeriodeAnneeById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackTypeSemisById', () => {
      it('Should return tracked TypeSemis primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTypeSemisById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackGerminationById', () => {
      it('Should return tracked Germination primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackGerminationById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
