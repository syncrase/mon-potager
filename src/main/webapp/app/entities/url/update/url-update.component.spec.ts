import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UrlService } from '../service/url.service';
import { IUrl, Url } from '../url.model';
import { ICronquistRank } from 'app/entities/cronquist-rank/cronquist-rank.model';
import { CronquistRankService } from 'app/entities/cronquist-rank/service/cronquist-rank.service';

import { UrlUpdateComponent } from './url-update.component';

describe('Url Management Update Component', () => {
  let comp: UrlUpdateComponent;
  let fixture: ComponentFixture<UrlUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let urlService: UrlService;
  let cronquistRankService: CronquistRankService;

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
    cronquistRankService = TestBed.inject(CronquistRankService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call CronquistRank query and add missing value', () => {
      const url: IUrl = { id: 456 };
      const cronquistRank: ICronquistRank = { id: 84812 };
      url.cronquistRank = cronquistRank;

      const cronquistRankCollection: ICronquistRank[] = [{ id: 68528 }];
      jest.spyOn(cronquistRankService, 'query').mockReturnValue(of(new HttpResponse({ body: cronquistRankCollection })));
      const additionalCronquistRanks = [cronquistRank];
      const expectedCollection: ICronquistRank[] = [...additionalCronquistRanks, ...cronquistRankCollection];
      jest.spyOn(cronquistRankService, 'addCronquistRankToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ url });
      comp.ngOnInit();

      expect(cronquistRankService.query).toHaveBeenCalled();
      expect(cronquistRankService.addCronquistRankToCollectionIfMissing).toHaveBeenCalledWith(
        cronquistRankCollection,
        ...additionalCronquistRanks
      );
      expect(comp.cronquistRanksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const url: IUrl = { id: 456 };
      const cronquistRank: ICronquistRank = { id: 99250 };
      url.cronquistRank = cronquistRank;

      activatedRoute.data = of({ url });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(url));
      expect(comp.cronquistRanksSharedCollection).toContain(cronquistRank);
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

  describe('Tracking relationships identifiers', () => {
    describe('trackCronquistRankById', () => {
      it('Should return tracked CronquistRank primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackCronquistRankById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
