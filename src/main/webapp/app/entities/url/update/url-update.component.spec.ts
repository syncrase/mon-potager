import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UrlService } from '../service/url.service';
import { IUrl, Url } from '../url.model';

import { UrlUpdateComponent } from './url-update.component';

describe('Url Management Update Component', () => {
  let comp: UrlUpdateComponent;
  let fixture: ComponentFixture<UrlUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let urlService: UrlService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UrlUpdateComponent],
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
      .overrideTemplate(UrlUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UrlUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    urlService = TestBed.inject(UrlService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const url: IUrl = { id: 456 };

      activatedRoute.data = of({ url });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(url));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Url>>();
      const url = { id: 123 };
      jest.spyOn(urlService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ url });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: url }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(urlService.update).toHaveBeenCalledWith(url);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Url>>();
      const url = new Url();
      jest.spyOn(urlService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ url });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: url }));
      saveSubject.complete();

      // THEN
      expect(urlService.create).toHaveBeenCalledWith(url);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Url>>();
      const url = { id: 123 };
      jest.spyOn(urlService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ url });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(urlService.update).toHaveBeenCalledWith(url);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
