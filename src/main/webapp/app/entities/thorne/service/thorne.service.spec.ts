import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IThorne, Thorne } from '../thorne.model';

import { ThorneService } from './thorne.service';

describe('Thorne Service', () => {
  let service: ThorneService;
  let httpMock: HttpTestingController;
  let elemDefault: IThorne;
  let expectedResult: IThorne | IThorne[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ThorneService);
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

    it('should create a Thorne', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Thorne()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Thorne', () => {
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

    it('should partial update a Thorne', () => {
      const patchObject = Object.assign({}, new Thorne());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Thorne', () => {
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

    it('should delete a Thorne', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addThorneToCollectionIfMissing', () => {
      it('should add a Thorne to an empty array', () => {
        const thorne: IThorne = { id: 123 };
        expectedResult = service.addThorneToCollectionIfMissing([], thorne);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(thorne);
      });

      it('should not add a Thorne to an array that contains it', () => {
        const thorne: IThorne = { id: 123 };
        const thorneCollection: IThorne[] = [
          {
            ...thorne,
          },
          { id: 456 },
        ];
        expectedResult = service.addThorneToCollectionIfMissing(thorneCollection, thorne);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Thorne to an array that doesn't contain it", () => {
        const thorne: IThorne = { id: 123 };
        const thorneCollection: IThorne[] = [{ id: 456 }];
        expectedResult = service.addThorneToCollectionIfMissing(thorneCollection, thorne);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(thorne);
      });

      it('should add only unique Thorne to an array', () => {
        const thorneArray: IThorne[] = [{ id: 123 }, { id: 456 }, { id: 75990 }];
        const thorneCollection: IThorne[] = [{ id: 123 }];
        expectedResult = service.addThorneToCollectionIfMissing(thorneCollection, ...thorneArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const thorne: IThorne = { id: 123 };
        const thorne2: IThorne = { id: 456 };
        expectedResult = service.addThorneToCollectionIfMissing([], thorne, thorne2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(thorne);
        expect(expectedResult).toContain(thorne2);
      });

      it('should accept null and undefined values', () => {
        const thorne: IThorne = { id: 123 };
        expectedResult = service.addThorneToCollectionIfMissing([], null, thorne, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(thorne);
      });

      it('should return initial array if no Thorne is added', () => {
        const thorneCollection: IThorne[] = [{ id: 123 }];
        expectedResult = service.addThorneToCollectionIfMissing(thorneCollection, undefined, null);
        expect(expectedResult).toEqual(thorneCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
