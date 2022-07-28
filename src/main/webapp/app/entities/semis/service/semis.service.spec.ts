import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISemis, Semis } from '../semis.model';

import { SemisService } from './semis.service';

describe('Semis Service', () => {
  let service: SemisService;
  let httpMock: HttpTestingController;
  let elemDefault: ISemis;
  let expectedResult: ISemis | ISemis[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SemisService);
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

    it('should create a Semis', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Semis()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Semis', () => {
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

    it('should partial update a Semis', () => {
      const patchObject = Object.assign({}, new Semis());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Semis', () => {
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

    it('should delete a Semis', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSemisToCollectionIfMissing', () => {
      it('should add a Semis to an empty array', () => {
        const semis: ISemis = { id: 123 };
        expectedResult = service.addSemisToCollectionIfMissing([], semis);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(semis);
      });

      it('should not add a Semis to an array that contains it', () => {
        const semis: ISemis = { id: 123 };
        const semisCollection: ISemis[] = [
          {
            ...semis,
          },
          { id: 456 },
        ];
        expectedResult = service.addSemisToCollectionIfMissing(semisCollection, semis);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Semis to an array that doesn't contain it", () => {
        const semis: ISemis = { id: 123 };
        const semisCollection: ISemis[] = [{ id: 456 }];
        expectedResult = service.addSemisToCollectionIfMissing(semisCollection, semis);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(semis);
      });

      it('should add only unique Semis to an array', () => {
        const semisArray: ISemis[] = [{ id: 123 }, { id: 456 }, { id: 66293 }];
        const semisCollection: ISemis[] = [{ id: 123 }];
        expectedResult = service.addSemisToCollectionIfMissing(semisCollection, ...semisArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const semis: ISemis = { id: 123 };
        const semis2: ISemis = { id: 456 };
        expectedResult = service.addSemisToCollectionIfMissing([], semis, semis2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(semis);
        expect(expectedResult).toContain(semis2);
      });

      it('should accept null and undefined values', () => {
        const semis: ISemis = { id: 123 };
        expectedResult = service.addSemisToCollectionIfMissing([], null, semis, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(semis);
      });

      it('should return initial array if no Semis is added', () => {
        const semisCollection: ISemis[] = [{ id: 123 }];
        expectedResult = service.addSemisToCollectionIfMissing(semisCollection, undefined, null);
        expect(expectedResult).toEqual(semisCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
