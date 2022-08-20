import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ReferenceService } from '../service/reference.service';
import { IReference, Reference } from '../reference.model';
import { IUrl } from 'app/entities/url/url.model';
import { UrlService } from 'app/entities/url/service/url.service';

import { ReferenceUpdateComponent } from './reference-update.component';

describe('Reference Management Update Component', () => {
  let comp: ReferenceUpdateComponent;
  let fixture: ComponentFixture<ReferenceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let referenceService: ReferenceService;
  let urlService: UrlService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ReferenceUpdateComponent],
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
      .overrideTemplate(ReferenceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReferenceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    referenceService = TestBed.inject(ReferenceService);
    urlService = TestBed.inject(UrlService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Url query and add missing value', () => {
      const reference: IReference = { id: 456 };
      const url: IUrl = { id: 21251 };
      reference.url = url;

      const urlCollection: IUrl[] = [{ id: 91613 }];
      jest.spyOn(urlService, 'query').mockReturnValue(of(new HttpResponse({ body: urlCollection })));
      const additionalUrls = [url];
      const expectedCollection: IUrl[] = [...additionalUrls, ...urlCollection];
      jest.spyOn(urlService, 'addUrlToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reference });
      comp.ngOnInit();

      expect(urlService.query).toHaveBeenCalled();
      expect(urlService.addUrlToCollectionIfMissing).toHaveBeenCalledWith(urlCollection, ...additionalUrls);
      expect(comp.urlsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const reference: IReference = { id: 456 };
      const url: IUrl = { id: 79456 };
      reference.url = url;

      activatedRoute.data = of({ reference });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(reference));
      expect(comp.urlsSharedCollection).toContain(url);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Reference>>();
      const reference = { id: 123 };
      jest.spyOn(referenceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reference });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reference }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(referenceService.update).toHaveBeenCalledWith(reference);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Reference>>();
      const reference = new Reference();
      jest.spyOn(referenceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reference });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reference }));
      saveSubject.complete();

      // THEN
      expect(referenceService.create).toHaveBeenCalledWith(reference);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Reference>>();
      const reference = { id: 123 };
      jest.spyOn(referenceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reference });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(referenceService.update).toHaveBeenCalledWith(reference);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUrlById', () => {
      it('Should return tracked Url primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUrlById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
