import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IAllelopathie, Allelopathie } from '../allelopathie.model';
import { AllelopathieService } from '../service/allelopathie.service';

import { AllelopathieRoutingResolveService } from './allelopathie-routing-resolve.service';

describe('Allelopathie routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: AllelopathieRoutingResolveService;
  let service: AllelopathieService;
  let resultAllelopathie: IAllelopathie | undefined;

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
    routingResolveService = TestBed.inject(AllelopathieRoutingResolveService);
    service = TestBed.inject(AllelopathieService);
    resultAllelopathie = undefined;
  });

  describe('resolve', () => {
    it('should return IAllelopathie returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultAllelopathie = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultAllelopathie).toEqual({ id: 123 });
    });

    it('should return new IAllelopathie if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultAllelopathie = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultAllelopathie).toEqual(new Allelopathie());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Allelopathie })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultAllelopathie = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultAllelopathie).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
