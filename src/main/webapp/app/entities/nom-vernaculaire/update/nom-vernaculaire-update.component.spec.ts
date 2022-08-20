import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { NomVernaculaireService } from '../service/nom-vernaculaire.service';
import { INomVernaculaire, NomVernaculaire } from '../nom-vernaculaire.model';

import { NomVernaculaireUpdateComponent } from './nom-vernaculaire-update.component';

describe('NomVernaculaire Management Update Component', () => {
  let comp: NomVernaculaireUpdateComponent;
  let fixture: ComponentFixture<NomVernaculaireUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let nomVernaculaireService: NomVernaculaireService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [NomVernaculaireUpdateComponent],
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
      .overrideTemplate(NomVernaculaireUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NomVernaculaireUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    nomVernaculaireService = TestBed.inject(NomVernaculaireService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const nomVernaculaire: INomVernaculaire = { id: 456 };

      activatedRoute.data = of({ nomVernaculaire });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(nomVernaculaire));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<NomVernaculaire>>();
      const nomVernaculaire = { id: 123 };
      jest.spyOn(nomVernaculaireService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ nomVernaculaire });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: nomVernaculaire }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(nomVernaculaireService.update).toHaveBeenCalledWith(nomVernaculaire);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<NomVernaculaire>>();
      const nomVernaculaire = new NomVernaculaire();
      jest.spyOn(nomVernaculaireService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ nomVernaculaire });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: nomVernaculaire }));
      saveSubject.complete();

      // THEN
      expect(nomVernaculaireService.create).toHaveBeenCalledWith(nomVernaculaire);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<NomVernaculaire>>();
      const nomVernaculaire = { id: 123 };
      jest.spyOn(nomVernaculaireService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ nomVernaculaire });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(nomVernaculaireService.update).toHaveBeenCalledWith(nomVernaculaire);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
