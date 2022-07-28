import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ICycleDeVie, CycleDeVie } from '../cycle-de-vie.model';
import { CycleDeVieService } from '../service/cycle-de-vie.service';

import { CycleDeVieRoutingResolveService } from './cycle-de-vie-routing-resolve.service';

describe('CycleDeVie routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CycleDeVieRoutingResolveService;
  let service: CycleDeVieService;
  let resultCycleDeVie: ICycleDeVie | undefined;

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
    routingResolveService = TestBed.inject(CycleDeVieRoutingResolveService);
    service = TestBed.inject(CycleDeVieService);
    resultCycleDeVie = undefined;
  });

  describe('resolve', () => {
    it('should return ICycleDeVie returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCycleDeVie = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCycleDeVie).toEqual({ id: 123 });
    });

    it('should return new ICycleDeVie if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCycleDeVie = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCycleDeVie).toEqual(new CycleDeVie());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as CycleDeVie })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCycleDeVie = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCycleDeVie).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
