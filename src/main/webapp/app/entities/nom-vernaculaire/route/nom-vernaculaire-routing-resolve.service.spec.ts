import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { INomVernaculaire, NomVernaculaire } from '../nom-vernaculaire.model';
import { NomVernaculaireService } from '../service/nom-vernaculaire.service';

import { NomVernaculaireRoutingResolveService } from './nom-vernaculaire-routing-resolve.service';

describe('NomVernaculaire routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: NomVernaculaireRoutingResolveService;
  let service: NomVernaculaireService;
  let resultNomVernaculaire: INomVernaculaire | undefined;

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
    routingResolveService = TestBed.inject(NomVernaculaireRoutingResolveService);
    service = TestBed.inject(NomVernaculaireService);
    resultNomVernaculaire = undefined;
  });

  describe('resolve', () => {
    it('should return INomVernaculaire returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultNomVernaculaire = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultNomVernaculaire).toEqual({ id: 123 });
    });

    it('should return new INomVernaculaire if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultNomVernaculaire = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultNomVernaculaire).toEqual(new NomVernaculaire());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as NomVernaculaire })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultNomVernaculaire = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultNomVernaculaire).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
