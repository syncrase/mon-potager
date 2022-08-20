import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {APG, IAPG} from '../apg.model';

import {APGService} from './apg.service';

describe('APG Service', () => {
  let service: APGService;
  let httpMock: HttpTestingController;
  let elemDefault: IAPG;
  let expectedResult: IAPG | IAPG[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(APGService);
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

    it('should create a APG', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new APG()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a APG', () => {
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

    it('should partial update a APG', () => {
      const patchObject = Object.assign({}, new APG());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of APG', () => {
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

    it('should delete a APG', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAPGToCollectionIfMissing', () => {
      it('should add a APG to an empty array', () => {
        const aPG: IAPG = { id: 123 };
        expectedResult = service.addAPGToCollectionIfMissing([], aPG);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(aPG);
      });

      it('should not add a APG to an array that contains it', () => {
        const aPG: IAPG = { id: 123 };
        const aPGCollection: IAPG[] = [
          {
            ...aPG,
          },
          { id: 456 },
        ];
        expectedResult = service.addAPGToCollectionIfMissing(aPGCollection, aPG);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a APG to an array that doesn't contain it", () => {
        const aPG: IAPG = { id: 123 };
        const aPGCollection: IAPG[] = [{ id: 456 }];
        expectedResult = service.addAPGToCollectionIfMissing(aPGCollection, aPG);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(aPG);
      });

      it('should add only unique APG to an array', () => {
        const aPGArray: IAPG[] = [{ id: 123 }, { id: 456 }, { id: 62748 }];
        const aPGCollection: IAPG[] = [{ id: 123 }];
        expectedResult = service.addAPGToCollectionIfMissing(aPGCollection, ...aPGArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const aPG: IAPG = { id: 123 };
        const aPG2: IAPG = { id: 456 };
        expectedResult = service.addAPGToCollectionIfMissing([], aPG, aPG2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(aPG);
        expect(expectedResult).toContain(aPG2);
      });

      it('should accept null and undefined values', () => {
        const aPG: IAPG = { id: 123 };
        expectedResult = service.addAPGToCollectionIfMissing([], null, aPG, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(aPG);
      });

      it('should return initial array if no APG is added', () => {
        const aPGCollection: IAPG[] = [{ id: 123 }];
        expectedResult = service.addAPGToCollectionIfMissing(aPGCollection, undefined, null);
        expect(expectedResult).toEqual(aPGCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
