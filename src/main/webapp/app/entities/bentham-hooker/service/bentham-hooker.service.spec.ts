import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {BenthamHooker, IBenthamHooker} from '../bentham-hooker.model';

import {BenthamHookerService} from './bentham-hooker.service';

describe('BenthamHooker Service', () => {
  let service: BenthamHookerService;
  let httpMock: HttpTestingController;
  let elemDefault: IBenthamHooker;
  let expectedResult: IBenthamHooker | IBenthamHooker[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BenthamHookerService);
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

    it('should create a BenthamHooker', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new BenthamHooker()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BenthamHooker', () => {
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

    it('should partial update a BenthamHooker', () => {
      const patchObject = Object.assign({}, new BenthamHooker());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BenthamHooker', () => {
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

    it('should delete a BenthamHooker', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addBenthamHookerToCollectionIfMissing', () => {
      it('should add a BenthamHooker to an empty array', () => {
        const benthamHooker: IBenthamHooker = { id: 123 };
        expectedResult = service.addBenthamHookerToCollectionIfMissing([], benthamHooker);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(benthamHooker);
      });

      it('should not add a BenthamHooker to an array that contains it', () => {
        const benthamHooker: IBenthamHooker = { id: 123 };
        const benthamHookerCollection: IBenthamHooker[] = [
          {
            ...benthamHooker,
          },
          { id: 456 },
        ];
        expectedResult = service.addBenthamHookerToCollectionIfMissing(benthamHookerCollection, benthamHooker);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BenthamHooker to an array that doesn't contain it", () => {
        const benthamHooker: IBenthamHooker = { id: 123 };
        const benthamHookerCollection: IBenthamHooker[] = [{ id: 456 }];
        expectedResult = service.addBenthamHookerToCollectionIfMissing(benthamHookerCollection, benthamHooker);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(benthamHooker);
      });

      it('should add only unique BenthamHooker to an array', () => {
        const benthamHookerArray: IBenthamHooker[] = [{ id: 123 }, { id: 456 }, { id: 23217 }];
        const benthamHookerCollection: IBenthamHooker[] = [{ id: 123 }];
        expectedResult = service.addBenthamHookerToCollectionIfMissing(benthamHookerCollection, ...benthamHookerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const benthamHooker: IBenthamHooker = { id: 123 };
        const benthamHooker2: IBenthamHooker = { id: 456 };
        expectedResult = service.addBenthamHookerToCollectionIfMissing([], benthamHooker, benthamHooker2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(benthamHooker);
        expect(expectedResult).toContain(benthamHooker2);
      });

      it('should accept null and undefined values', () => {
        const benthamHooker: IBenthamHooker = { id: 123 };
        expectedResult = service.addBenthamHookerToCollectionIfMissing([], null, benthamHooker, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(benthamHooker);
      });

      it('should return initial array if no BenthamHooker is added', () => {
        const benthamHookerCollection: IBenthamHooker[] = [{ id: 123 }];
        expectedResult = service.addBenthamHookerToCollectionIfMissing(benthamHookerCollection, undefined, null);
        expect(expectedResult).toEqual(benthamHookerCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
