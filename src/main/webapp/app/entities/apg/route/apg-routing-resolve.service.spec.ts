import {TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, Router} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {of} from 'rxjs';

import {APG, IAPG} from '../apg.model';
import {APGService} from '../service/apg.service';

import {APGRoutingResolveService} from './apg-routing-resolve.service';

describe('APG routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: APGRoutingResolveService;
  let service: APGService;
  let resultAPG: IAPG | undefined;

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
    routingResolveService = TestBed.inject(APGRoutingResolveService);
    service = TestBed.inject(APGService);
    resultAPG = undefined;
  });

  describe('resolve', () => {
    it('should return IAPG returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultAPG = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultAPG).toEqual({ id: 123 });
    });

    it('should return new IAPG if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultAPG = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultAPG).toEqual(new APG());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as APG })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultAPG = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultAPG).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
