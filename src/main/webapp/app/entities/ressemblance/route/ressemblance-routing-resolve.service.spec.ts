import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IRessemblance, Ressemblance } from '../ressemblance.model';
import { RessemblanceService } from '../service/ressemblance.service';

import { RessemblanceRoutingResolveService } from './ressemblance-routing-resolve.service';

describe('Ressemblance routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: RessemblanceRoutingResolveService;
  let service: RessemblanceService;
  let resultRessemblance: IRessemblance | undefined;

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
    routingResolveService = TestBed.inject(RessemblanceRoutingResolveService);
    service = TestBed.inject(RessemblanceService);
    resultRessemblance = undefined;
  });

  describe('resolve', () => {
    it('should return IRessemblance returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultRessemblance = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultRessemblance).toEqual({ id: 123 });
    });

    it('should return new IRessemblance if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultRessemblance = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultRessemblance).toEqual(new Ressemblance());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Ressemblance })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultRessemblance = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultRessemblance).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
