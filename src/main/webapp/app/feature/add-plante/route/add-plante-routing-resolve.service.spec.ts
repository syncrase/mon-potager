import {TestBed} from '@angular/core/testing';
import { expect } from '@jest/globals';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, Router} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {of} from 'rxjs';
import {PlanteService} from "../../../entities/plante/service/plante.service";
import {IPlante, Plante} from "../../../entities/plante/plante.model";
import {AddPlanteRoutingResolveService} from "./add-plante-routing-resolve.service";


describe('Plante routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: AddPlanteRoutingResolveService;
  let service: PlanteService;
  let resultPlante: IPlante | undefined;

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
    routingResolveService = TestBed.inject(AddPlanteRoutingResolveService);
    service = TestBed.inject(PlanteService);
    resultPlante = undefined;
  });

  describe('resolve', () => {
    it('should return IPlante returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPlante = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultPlante).toEqual({ id: 123 });
    });

    it('should return new IPlante if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPlante = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultPlante).toEqual(new Plante());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Plante })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPlante = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultPlante).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
