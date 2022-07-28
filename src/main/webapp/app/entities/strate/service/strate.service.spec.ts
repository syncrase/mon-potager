import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IStrate, Strate } from '../strate.model';

import { StrateService } from './strate.service';

describe('Strate Service', () => {
  let service: StrateService;
  let httpMock: HttpTestingController;
  let elemDefault: IStrate;
  let expectedResult: IStrate | IStrate[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(StrateService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      type: 'AAAAAAA',
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

    it('should create a Strate', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Strate()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Strate', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          type: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Strate', () => {
      const patchObject = Object.assign({}, new Strate());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Strate', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          type: 'BBBBBB',
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

    it('should delete a Strate', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addStrateToCollectionIfMissing', () => {
      it('should add a Strate to an empty array', () => {
        const strate: IStrate = { id: 123 };
        expectedResult = service.addStrateToCollectionIfMissing([], strate);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(strate);
      });

      it('should not add a Strate to an array that contains it', () => {
        const strate: IStrate = { id: 123 };
        const strateCollection: IStrate[] = [
          {
            ...strate,
          },
          { id: 456 },
        ];
        expectedResult = service.addStrateToCollectionIfMissing(strateCollection, strate);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Strate to an array that doesn't contain it", () => {
        const strate: IStrate = { id: 123 };
        const strateCollection: IStrate[] = [{ id: 456 }];
        expectedResult = service.addStrateToCollectionIfMissing(strateCollection, strate);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(strate);
      });

      it('should add only unique Strate to an array', () => {
        const strateArray: IStrate[] = [{ id: 123 }, { id: 456 }, { id: 46926 }];
        const strateCollection: IStrate[] = [{ id: 123 }];
        expectedResult = service.addStrateToCollectionIfMissing(strateCollection, ...strateArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const strate: IStrate = { id: 123 };
        const strate2: IStrate = { id: 456 };
        expectedResult = service.addStrateToCollectionIfMissing([], strate, strate2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(strate);
        expect(expectedResult).toContain(strate2);
      });

      it('should accept null and undefined values', () => {
        const strate: IStrate = { id: 123 };
        expectedResult = service.addStrateToCollectionIfMissing([], null, strate, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(strate);
      });

      it('should return initial array if no Strate is added', () => {
        const strateCollection: IStrate[] = [{ id: 123 }];
        expectedResult = service.addStrateToCollectionIfMissing(strateCollection, undefined, null);
        expect(expectedResult).toEqual(strateCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
