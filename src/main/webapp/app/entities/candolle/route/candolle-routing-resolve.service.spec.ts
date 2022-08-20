import {TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, Router} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {of} from 'rxjs';

import {Candolle, ICandolle} from '../candolle.model';
import {CandolleService} from '../service/candolle.service';

import {CandolleRoutingResolveService} from './candolle-routing-resolve.service';

describe('Candolle routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CandolleRoutingResolveService;
  let service: CandolleService;
  let resultCandolle: ICandolle | undefined;

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
    routingResolveService = TestBed.inject(CandolleRoutingResolveService);
    service = TestBed.inject(CandolleService);
    resultCandolle = undefined;
  });

  describe('resolve', () => {
    it('should return ICandolle returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCandolle = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCandolle).toEqual({ id: 123 });
    });

    it('should return new ICandolle if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCandolle = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCandolle).toEqual(new Candolle());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Candolle })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCandolle = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCandolle).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
