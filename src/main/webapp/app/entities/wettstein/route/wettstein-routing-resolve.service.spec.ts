import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IWettstein, Wettstein } from '../wettstein.model';
import { WettsteinService } from '../service/wettstein.service';

import { WettsteinRoutingResolveService } from './wettstein-routing-resolve.service';

describe('Wettstein routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: WettsteinRoutingResolveService;
  let service: WettsteinService;
  let resultWettstein: IWettstein | undefined;

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
    routingResolveService = TestBed.inject(WettsteinRoutingResolveService);
    service = TestBed.inject(WettsteinService);
    resultWettstein = undefined;
  });

  describe('resolve', () => {
    it('should return IWettstein returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultWettstein = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultWettstein).toEqual({ id: 123 });
    });

    it('should return new IWettstein if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultWettstein = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultWettstein).toEqual(new Wettstein());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Wettstein })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultWettstein = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultWettstein).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
