import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ITakhtajan, Takhtajan } from '../takhtajan.model';
import { TakhtajanService } from '../service/takhtajan.service';

import { TakhtajanRoutingResolveService } from './takhtajan-routing-resolve.service';

describe('Takhtajan routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: TakhtajanRoutingResolveService;
  let service: TakhtajanService;
  let resultTakhtajan: ITakhtajan | undefined;

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
    routingResolveService = TestBed.inject(TakhtajanRoutingResolveService);
    service = TestBed.inject(TakhtajanService);
    resultTakhtajan = undefined;
  });

  describe('resolve', () => {
    it('should return ITakhtajan returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTakhtajan = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTakhtajan).toEqual({ id: 123 });
    });

    it('should return new ITakhtajan if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTakhtajan = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultTakhtajan).toEqual(new Takhtajan());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Takhtajan })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTakhtajan = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTakhtajan).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
