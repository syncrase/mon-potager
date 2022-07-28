import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RacineService } from '../service/racine.service';
import { IRacine, Racine } from '../racine.model';

import { RacineUpdateComponent } from './racine-update.component';

describe('Racine Management Update Component', () => {
  let comp: RacineUpdateComponent;
  let fixture: ComponentFixture<RacineUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let racineService: RacineService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RacineUpdateComponent],
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
      .overrideTemplate(RacineUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RacineUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    racineService = TestBed.inject(RacineService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const racine: IRacine = { id: 456 };

      activatedRoute.data = of({ racine });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(racine));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Racine>>();
      const racine = { id: 123 };
      jest.spyOn(racineService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ racine });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: racine }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(racineService.update).toHaveBeenCalledWith(racine);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Racine>>();
      const racine = new Racine();
      jest.spyOn(racineService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ racine });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: racine }));
      saveSubject.complete();

      // THEN
      expect(racineService.create).toHaveBeenCalledWith(racine);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Racine>>();
      const racine = { id: 123 };
      jest.spyOn(racineService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ racine });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(racineService.update).toHaveBeenCalledWith(racine);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
