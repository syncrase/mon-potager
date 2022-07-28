import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISol, Sol } from '../sol.model';

import { SolService } from './sol.service';

describe('Sol Service', () => {
  let service: SolService;
  let httpMock: HttpTestingController;
  let elemDefault: ISol;
  let expectedResult: ISol | ISol[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SolService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      phMin: 0,
      phMax: 0,
      type: 'AAAAAAA',
      richesse: 'AAAAAAA',
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

    it('should create a Sol', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Sol()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Sol', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          phMin: 1,
          phMax: 1,
          type: 'BBBBBB',
          richesse: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Sol', () => {
      const patchObject = Object.assign(
        {
          phMin: 1,
        },
        new Sol()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Sol', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          phMin: 1,
          phMax: 1,
          type: 'BBBBBB',
          richesse: 'BBBBBB',
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

    it('should delete a Sol', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSolToCollectionIfMissing', () => {
      it('should add a Sol to an empty array', () => {
        const sol: ISol = { id: 123 };
        expectedResult = service.addSolToCollectionIfMissing([], sol);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sol);
      });

      it('should not add a Sol to an array that contains it', () => {
        const sol: ISol = { id: 123 };
        const solCollection: ISol[] = [
          {
            ...sol,
          },
          { id: 456 },
        ];
        expectedResult = service.addSolToCollectionIfMissing(solCollection, sol);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Sol to an array that doesn't contain it", () => {
        const sol: ISol = { id: 123 };
        const solCollection: ISol[] = [{ id: 456 }];
        expectedResult = service.addSolToCollectionIfMissing(solCollection, sol);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sol);
      });

      it('should add only unique Sol to an array', () => {
        const solArray: ISol[] = [{ id: 123 }, { id: 456 }, { id: 4584 }];
        const solCollection: ISol[] = [{ id: 123 }];
        expectedResult = service.addSolToCollectionIfMissing(solCollection, ...solArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sol: ISol = { id: 123 };
        const sol2: ISol = { id: 456 };
        expectedResult = service.addSolToCollectionIfMissing([], sol, sol2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sol);
        expect(expectedResult).toContain(sol2);
      });

      it('should accept null and undefined values', () => {
        const sol: ISol = { id: 123 };
        expectedResult = service.addSolToCollectionIfMissing([], null, sol, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sol);
      });

      it('should return initial array if no Sol is added', () => {
        const solCollection: ISol[] = [{ id: 123 }];
        expectedResult = service.addSolToCollectionIfMissing(solCollection, undefined, null);
        expect(expectedResult).toEqual(solCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
