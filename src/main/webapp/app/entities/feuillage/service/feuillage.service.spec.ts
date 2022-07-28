import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IFeuillage, Feuillage } from '../feuillage.model';

import { FeuillageService } from './feuillage.service';

describe('Feuillage Service', () => {
  let service: FeuillageService;
  let httpMock: HttpTestingController;
  let elemDefault: IFeuillage;
  let expectedResult: IFeuillage | IFeuillage[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FeuillageService);
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

    it('should create a Feuillage', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Feuillage()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Feuillage', () => {
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

    it('should partial update a Feuillage', () => {
      const patchObject = Object.assign(
        {
          type: 'BBBBBB',
        },
        new Feuillage()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Feuillage', () => {
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

    it('should delete a Feuillage', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addFeuillageToCollectionIfMissing', () => {
      it('should add a Feuillage to an empty array', () => {
        const feuillage: IFeuillage = { id: 123 };
        expectedResult = service.addFeuillageToCollectionIfMissing([], feuillage);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(feuillage);
      });

      it('should not add a Feuillage to an array that contains it', () => {
        const feuillage: IFeuillage = { id: 123 };
        const feuillageCollection: IFeuillage[] = [
          {
            ...feuillage,
          },
          { id: 456 },
        ];
        expectedResult = service.addFeuillageToCollectionIfMissing(feuillageCollection, feuillage);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Feuillage to an array that doesn't contain it", () => {
        const feuillage: IFeuillage = { id: 123 };
        const feuillageCollection: IFeuillage[] = [{ id: 456 }];
        expectedResult = service.addFeuillageToCollectionIfMissing(feuillageCollection, feuillage);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(feuillage);
      });

      it('should add only unique Feuillage to an array', () => {
        const feuillageArray: IFeuillage[] = [{ id: 123 }, { id: 456 }, { id: 25491 }];
        const feuillageCollection: IFeuillage[] = [{ id: 123 }];
        expectedResult = service.addFeuillageToCollectionIfMissing(feuillageCollection, ...feuillageArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const feuillage: IFeuillage = { id: 123 };
        const feuillage2: IFeuillage = { id: 456 };
        expectedResult = service.addFeuillageToCollectionIfMissing([], feuillage, feuillage2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(feuillage);
        expect(expectedResult).toContain(feuillage2);
      });

      it('should accept null and undefined values', () => {
        const feuillage: IFeuillage = { id: 123 };
        expectedResult = service.addFeuillageToCollectionIfMissing([], null, feuillage, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(feuillage);
      });

      it('should return initial array if no Feuillage is added', () => {
        const feuillageCollection: IFeuillage[] = [{ id: 123 }];
        expectedResult = service.addFeuillageToCollectionIfMissing(feuillageCollection, undefined, null);
        expect(expectedResult).toEqual(feuillageCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
