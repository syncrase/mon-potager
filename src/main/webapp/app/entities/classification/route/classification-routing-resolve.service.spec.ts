import {TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, Router} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {of} from 'rxjs';

import {Classification, IClassification} from '../classification.model';
import {ClassificationService} from '../service/classification.service';

import {ClassificationRoutingResolveService} from './classification-routing-resolve.service';

describe('Classification routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ClassificationRoutingResolveService;
  let service: ClassificationService;
  let resultClassification: IClassification | undefined;

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
    routingResolveService = TestBed.inject(ClassificationRoutingResolveService);
    service = TestBed.inject(ClassificationService);
    resultClassification = undefined;
  });

  describe('resolve', () => {
    it('should return IClassification returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultClassification = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultClassification).toEqual({ id: 123 });
    });

    it('should return new IClassification if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultClassification = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultClassification).toEqual(new Classification());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Classification })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultClassification = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultClassification).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
