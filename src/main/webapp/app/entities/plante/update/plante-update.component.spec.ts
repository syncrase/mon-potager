import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PlanteService } from '../service/plante.service';
import { IPlante, Plante } from '../plante.model';
import { IClassification } from 'app/entities/classification/classification.model';
import { ClassificationService } from 'app/entities/classification/service/classification.service';
import { INomVernaculaire } from 'app/entities/nom-vernaculaire/nom-vernaculaire.model';
import { NomVernaculaireService } from 'app/entities/nom-vernaculaire/service/nom-vernaculaire.service';
import { IReference } from 'app/entities/reference/reference.model';
import { ReferenceService } from 'app/entities/reference/service/reference.service';

import { PlanteUpdateComponent } from './plante-update.component';

describe('Plante Management Update Component', () => {
  let comp: PlanteUpdateComponent;
  let fixture: ComponentFixture<PlanteUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let planteService: PlanteService;
  let classificationService: ClassificationService;
  let nomVernaculaireService: NomVernaculaireService;
  let referenceService: ReferenceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PlanteUpdateComponent],
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
      .overrideTemplate(PlanteUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlanteUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    planteService = TestBed.inject(PlanteService);
    classificationService = TestBed.inject(ClassificationService);
    nomVernaculaireService = TestBed.inject(NomVernaculaireService);
    referenceService = TestBed.inject(ReferenceService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Classification query and add missing value', () => {
      const plante: IPlante = { id: 456 };
      const classification: IClassification = { id: 65054 };
      plante.classification = classification;

      const classificationCollection: IClassification[] = [{ id: 69779 }];
      jest.spyOn(classificationService, 'query').mockReturnValue(of(new HttpResponse({ body: classificationCollection })));
      const additionalClassifications = [classification];
      const expectedCollection: IClassification[] = [...additionalClassifications, ...classificationCollection];
      jest.spyOn(classificationService, 'addClassificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      expect(classificationService.query).toHaveBeenCalled();
      expect(classificationService.addClassificationToCollectionIfMissing).toHaveBeenCalledWith(
        classificationCollection,
        ...additionalClassifications
      );
      expect(comp.classificationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call NomVernaculaire query and add missing value', () => {
      const plante: IPlante = { id: 456 };
      const nomsVernaculaires: INomVernaculaire[] = [{ id: 6138 }];
      plante.nomsVernaculaires = nomsVernaculaires;

      const nomVernaculaireCollection: INomVernaculaire[] = [{ id: 35825 }];
      jest.spyOn(nomVernaculaireService, 'query').mockReturnValue(of(new HttpResponse({ body: nomVernaculaireCollection })));
      const additionalNomVernaculaires = [...nomsVernaculaires];
      const expectedCollection: INomVernaculaire[] = [...additionalNomVernaculaires, ...nomVernaculaireCollection];
      jest.spyOn(nomVernaculaireService, 'addNomVernaculaireToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      expect(nomVernaculaireService.query).toHaveBeenCalled();
      expect(nomVernaculaireService.addNomVernaculaireToCollectionIfMissing).toHaveBeenCalledWith(
        nomVernaculaireCollection,
        ...additionalNomVernaculaires
      );
      expect(comp.nomVernaculairesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Reference query and add missing value', () => {
      const plante: IPlante = { id: 456 };
      const references: IReference[] = [{ id: 62351 }];
      plante.references = references;

      const referenceCollection: IReference[] = [{ id: 30472 }];
      jest.spyOn(referenceService, 'query').mockReturnValue(of(new HttpResponse({ body: referenceCollection })));
      const additionalReferences = [...references];
      const expectedCollection: IReference[] = [...additionalReferences, ...referenceCollection];
      jest.spyOn(referenceService, 'addReferenceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      expect(referenceService.query).toHaveBeenCalled();
      expect(referenceService.addReferenceToCollectionIfMissing).toHaveBeenCalledWith(referenceCollection, ...additionalReferences);
      expect(comp.referencesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const plante: IPlante = { id: 456 };
      const classification: IClassification = { id: 62985 };
      plante.classification = classification;
      const nomsVernaculaires: INomVernaculaire = { id: 18705 };
      plante.nomsVernaculaires = [nomsVernaculaires];
      const references: IReference = { id: 26694 };
      plante.references = [references];

      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(plante));
      expect(comp.classificationsSharedCollection).toContain(classification);
      expect(comp.nomVernaculairesSharedCollection).toContain(nomsVernaculaires);
      expect(comp.referencesSharedCollection).toContain(references);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Plante>>();
      const plante = { id: 123 };
      jest.spyOn(planteService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: plante }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(planteService.update).toHaveBeenCalledWith(plante);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Plante>>();
      const plante = new Plante();
      jest.spyOn(planteService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: plante }));
      saveSubject.complete();

      // THEN
      expect(planteService.create).toHaveBeenCalledWith(plante);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Plante>>();
      const plante = { id: 123 };
      jest.spyOn(planteService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ plante });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(planteService.update).toHaveBeenCalledWith(plante);
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

    describe('trackNomVernaculaireById', () => {
      it('Should return tracked NomVernaculaire primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackNomVernaculaireById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackReferenceById', () => {
      it('Should return tracked Reference primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackReferenceById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedNomVernaculaire', () => {
      it('Should return option if no NomVernaculaire is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedNomVernaculaire(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected NomVernaculaire for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedNomVernaculaire(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this NomVernaculaire is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedNomVernaculaire(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });

    describe('getSelectedReference', () => {
      it('Should return option if no Reference is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedReference(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Reference for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedReference(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Reference is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedReference(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
