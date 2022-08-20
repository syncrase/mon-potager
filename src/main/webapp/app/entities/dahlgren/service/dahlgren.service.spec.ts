import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {Dahlgren, IDahlgren} from '../dahlgren.model';

import {DahlgrenService} from './dahlgren.service';

describe('Dahlgren Service', () => {
  let service: DahlgrenService;
  let httpMock: HttpTestingController;
  let elemDefault: IDahlgren;
  let expectedResult: IDahlgren | IDahlgren[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DahlgrenService);
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

    it('should create a Dahlgren', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Dahlgren()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Dahlgren', () => {
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

    it('should partial update a Dahlgren', () => {
      const patchObject = Object.assign({}, new Dahlgren());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Dahlgren', () => {
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

    it('should delete a Dahlgren', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addDahlgrenToCollectionIfMissing', () => {
      it('should add a Dahlgren to an empty array', () => {
        const dahlgren: IDahlgren = { id: 123 };
        expectedResult = service.addDahlgrenToCollectionIfMissing([], dahlgren);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dahlgren);
      });

      it('should not add a Dahlgren to an array that contains it', () => {
        const dahlgren: IDahlgren = { id: 123 };
        const dahlgrenCollection: IDahlgren[] = [
          {
            ...dahlgren,
          },
          { id: 456 },
        ];
        expectedResult = service.addDahlgrenToCollectionIfMissing(dahlgrenCollection, dahlgren);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Dahlgren to an array that doesn't contain it", () => {
        const dahlgren: IDahlgren = { id: 123 };
        const dahlgrenCollection: IDahlgren[] = [{ id: 456 }];
        expectedResult = service.addDahlgrenToCollectionIfMissing(dahlgrenCollection, dahlgren);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dahlgren);
      });

      it('should add only unique Dahlgren to an array', () => {
        const dahlgrenArray: IDahlgren[] = [{ id: 123 }, { id: 456 }, { id: 49855 }];
        const dahlgrenCollection: IDahlgren[] = [{ id: 123 }];
        expectedResult = service.addDahlgrenToCollectionIfMissing(dahlgrenCollection, ...dahlgrenArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const dahlgren: IDahlgren = { id: 123 };
        const dahlgren2: IDahlgren = { id: 456 };
        expectedResult = service.addDahlgrenToCollectionIfMissing([], dahlgren, dahlgren2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dahlgren);
        expect(expectedResult).toContain(dahlgren2);
      });

      it('should accept null and undefined values', () => {
        const dahlgren: IDahlgren = { id: 123 };
        expectedResult = service.addDahlgrenToCollectionIfMissing([], null, dahlgren, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dahlgren);
      });

      it('should return initial array if no Dahlgren is added', () => {
        const dahlgrenCollection: IDahlgren[] = [{ id: 123 }];
        expectedResult = service.addDahlgrenToCollectionIfMissing(dahlgrenCollection, undefined, null);
        expect(expectedResult).toEqual(dahlgrenCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
