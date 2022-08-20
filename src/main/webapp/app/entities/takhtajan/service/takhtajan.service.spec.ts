import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITakhtajan, Takhtajan } from '../takhtajan.model';

import { TakhtajanService } from './takhtajan.service';

describe('Takhtajan Service', () => {
  let service: TakhtajanService;
  let httpMock: HttpTestingController;
  let elemDefault: ITakhtajan;
  let expectedResult: ITakhtajan | ITakhtajan[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TakhtajanService);
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

    it('should create a Takhtajan', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Takhtajan()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Takhtajan', () => {
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

    it('should partial update a Takhtajan', () => {
      const patchObject = Object.assign({}, new Takhtajan());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Takhtajan', () => {
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

    it('should delete a Takhtajan', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTakhtajanToCollectionIfMissing', () => {
      it('should add a Takhtajan to an empty array', () => {
        const takhtajan: ITakhtajan = { id: 123 };
        expectedResult = service.addTakhtajanToCollectionIfMissing([], takhtajan);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(takhtajan);
      });

      it('should not add a Takhtajan to an array that contains it', () => {
        const takhtajan: ITakhtajan = { id: 123 };
        const takhtajanCollection: ITakhtajan[] = [
          {
            ...takhtajan,
          },
          { id: 456 },
        ];
        expectedResult = service.addTakhtajanToCollectionIfMissing(takhtajanCollection, takhtajan);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Takhtajan to an array that doesn't contain it", () => {
        const takhtajan: ITakhtajan = { id: 123 };
        const takhtajanCollection: ITakhtajan[] = [{ id: 456 }];
        expectedResult = service.addTakhtajanToCollectionIfMissing(takhtajanCollection, takhtajan);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(takhtajan);
      });

      it('should add only unique Takhtajan to an array', () => {
        const takhtajanArray: ITakhtajan[] = [{ id: 123 }, { id: 456 }, { id: 9720 }];
        const takhtajanCollection: ITakhtajan[] = [{ id: 123 }];
        expectedResult = service.addTakhtajanToCollectionIfMissing(takhtajanCollection, ...takhtajanArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const takhtajan: ITakhtajan = { id: 123 };
        const takhtajan2: ITakhtajan = { id: 456 };
        expectedResult = service.addTakhtajanToCollectionIfMissing([], takhtajan, takhtajan2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(takhtajan);
        expect(expectedResult).toContain(takhtajan2);
      });

      it('should accept null and undefined values', () => {
        const takhtajan: ITakhtajan = { id: 123 };
        expectedResult = service.addTakhtajanToCollectionIfMissing([], null, takhtajan, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(takhtajan);
      });

      it('should return initial array if no Takhtajan is added', () => {
        const takhtajanCollection: ITakhtajan[] = [{ id: 123 }];
        expectedResult = service.addTakhtajanToCollectionIfMissing(takhtajanCollection, undefined, null);
        expect(expectedResult).toEqual(takhtajanCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
