import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {Candolle, ICandolle} from '../candolle.model';

import {CandolleService} from './candolle.service';

describe('Candolle Service', () => {
  let service: CandolleService;
  let httpMock: HttpTestingController;
  let elemDefault: ICandolle;
  let expectedResult: ICandolle | ICandolle[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CandolleService);
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

    it('should create a Candolle', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Candolle()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Candolle', () => {
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

    it('should partial update a Candolle', () => {
      const patchObject = Object.assign({}, new Candolle());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Candolle', () => {
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

    it('should delete a Candolle', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCandolleToCollectionIfMissing', () => {
      it('should add a Candolle to an empty array', () => {
        const candolle: ICandolle = { id: 123 };
        expectedResult = service.addCandolleToCollectionIfMissing([], candolle);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(candolle);
      });

      it('should not add a Candolle to an array that contains it', () => {
        const candolle: ICandolle = { id: 123 };
        const candolleCollection: ICandolle[] = [
          {
            ...candolle,
          },
          { id: 456 },
        ];
        expectedResult = service.addCandolleToCollectionIfMissing(candolleCollection, candolle);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Candolle to an array that doesn't contain it", () => {
        const candolle: ICandolle = { id: 123 };
        const candolleCollection: ICandolle[] = [{ id: 456 }];
        expectedResult = service.addCandolleToCollectionIfMissing(candolleCollection, candolle);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(candolle);
      });

      it('should add only unique Candolle to an array', () => {
        const candolleArray: ICandolle[] = [{ id: 123 }, { id: 456 }, { id: 70272 }];
        const candolleCollection: ICandolle[] = [{ id: 123 }];
        expectedResult = service.addCandolleToCollectionIfMissing(candolleCollection, ...candolleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const candolle: ICandolle = { id: 123 };
        const candolle2: ICandolle = { id: 456 };
        expectedResult = service.addCandolleToCollectionIfMissing([], candolle, candolle2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(candolle);
        expect(expectedResult).toContain(candolle2);
      });

      it('should accept null and undefined values', () => {
        const candolle: ICandolle = { id: 123 };
        expectedResult = service.addCandolleToCollectionIfMissing([], null, candolle, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(candolle);
      });

      it('should return initial array if no Candolle is added', () => {
        const candolleCollection: ICandolle[] = [{ id: 123 }];
        expectedResult = service.addCandolleToCollectionIfMissing(candolleCollection, undefined, null);
        expect(expectedResult).toEqual(candolleCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
