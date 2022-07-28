import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IPeriodeAnnee, PeriodeAnnee } from '../periode-annee.model';
import { PeriodeAnneeService } from '../service/periode-annee.service';

import { PeriodeAnneeRoutingResolveService } from './periode-annee-routing-resolve.service';

describe('PeriodeAnnee routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: PeriodeAnneeRoutingResolveService;
  let service: PeriodeAnneeService;
  let resultPeriodeAnnee: IPeriodeAnnee | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(PeriodeAnneeRoutingResolveService);
    service = TestBed.inject(PeriodeAnneeService);
    resultPeriodeAnnee = undefined;
  });

  describe('resolve', () => {
    it('should return IPeriodeAnnee returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPeriodeAnnee = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultPeriodeAnnee).toEqual({ id: 123 });
    });

    it('should return new IPeriodeAnnee if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPeriodeAnnee = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultPeriodeAnnee).toEqual(new PeriodeAnnee());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as PeriodeAnnee })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPeriodeAnnee = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultPeriodeAnnee).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
