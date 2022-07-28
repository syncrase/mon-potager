import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TemperatureService } from '../service/temperature.service';
import { ITemperature, Temperature } from '../temperature.model';

import { TemperatureUpdateComponent } from './temperature-update.component';

describe('Temperature Management Update Component', () => {
  let comp: TemperatureUpdateComponent;
  let fixture: ComponentFixture<TemperatureUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let temperatureService: TemperatureService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TemperatureUpdateComponent],
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
      .overrideTemplate(TemperatureUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TemperatureUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    temperatureService = TestBed.inject(TemperatureService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const temperature: ITemperature = { id: 456 };

      activatedRoute.data = of({ temperature });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(temperature));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Temperature>>();
      const temperature = { id: 123 };
      jest.spyOn(temperatureService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ temperature });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: temperature }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(temperatureService.update).toHaveBeenCalledWith(temperature);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Temperature>>();
      const temperature = new Temperature();
      jest.spyOn(temperatureService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ temperature });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: temperature }));
      saveSubject.complete();

      // THEN
      expect(temperatureService.create).toHaveBeenCalledWith(temperature);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Temperature>>();
      const temperature = { id: 123 };
      jest.spyOn(temperatureService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ temperature });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(temperatureService.update).toHaveBeenCalledWith(temperature);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
