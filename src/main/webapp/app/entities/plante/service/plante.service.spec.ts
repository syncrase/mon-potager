import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPlante, Plante } from '../plante.model';

import { PlanteService } from './plante.service';

describe('Plante Service', () => {
  let service: PlanteService;
  let httpMock: HttpTestingController;
  let elemDefault: IPlante;
  let expectedResult: IPlante | IPlante[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PlanteService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Plante', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Plante()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Plante', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Plante', () => {
      const patchObject = Object.assign({}, new Plante());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Plante', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Plante', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPlanteToCollectionIfMissing', () => {
      it('should add a Plante to an empty array', () => {
        const plante: IPlante = { id: 123 };
        expectedResult = service.addPlanteToCollectionIfMissing([], plante);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(plante);
      });

      it('should not add a Plante to an array that contains it', () => {
        const plante: IPlante = { id: 123 };
        const planteCollection: IPlante[] = [
          {
            ...plante,
          },
          { id: 456 },
        ];
        expectedResult = service.addPlanteToCollectionIfMissing(planteCollection, plante);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Plante to an array that doesn't contain it", () => {
        const plante: IPlante = { id: 123 };
        const planteCollection: IPlante[] = [{ id: 456 }];
        expectedResult = service.addPlanteToCollectionIfMissing(planteCollection, plante);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(plante);
      });

      it('should add only unique Plante to an array', () => {
        const planteArray: IPlante[] = [{ id: 123 }, { id: 456 }, { id: 88050 }];
        const planteCollection: IPlante[] = [{ id: 123 }];
        expectedResult = service.addPlanteToCollectionIfMissing(planteCollection, ...planteArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const plante: IPlante = { id: 123 };
        const plante2: IPlante = { id: 456 };
        expectedResult = service.addPlanteToCollectionIfMissing([], plante, plante2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(plante);
        expect(expectedResult).toContain(plante2);
      });

      it('should accept null and undefined values', () => {
        const plante: IPlante = { id: 123 };
        expectedResult = service.addPlanteToCollectionIfMissing([], null, plante, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(plante);
      });

      it('should return initial array if no Plante is added', () => {
        const planteCollection: IPlante[] = [{ id: 123 }];
        expectedResult = service.addPlanteToCollectionIfMissing(planteCollection, undefined, null);
        expect(expectedResult).toEqual(planteCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
