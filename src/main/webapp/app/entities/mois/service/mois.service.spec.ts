import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMois, Mois } from '../mois.model';

import { MoisService } from './mois.service';

describe('Mois Service', () => {
  let service: MoisService;
  let httpMock: HttpTestingController;
  let elemDefault: IMois;
  let expectedResult: IMois | IMois[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MoisService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      numero: 0,
      nom: 'AAAAAAA',
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

    it('should create a Mois', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Mois()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Mois', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          numero: 1,
          nom: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Mois', () => {
      const patchObject = Object.assign({}, new Mois());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Mois', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          numero: 1,
          nom: 'BBBBBB',
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

    it('should delete a Mois', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addMoisToCollectionIfMissing', () => {
      it('should add a Mois to an empty array', () => {
        const mois: IMois = { id: 123 };
        expectedResult = service.addMoisToCollectionIfMissing([], mois);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mois);
      });

      it('should not add a Mois to an array that contains it', () => {
        const mois: IMois = { id: 123 };
        const moisCollection: IMois[] = [
          {
            ...mois,
          },
          { id: 456 },
        ];
        expectedResult = service.addMoisToCollectionIfMissing(moisCollection, mois);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Mois to an array that doesn't contain it", () => {
        const mois: IMois = { id: 123 };
        const moisCollection: IMois[] = [{ id: 456 }];
        expectedResult = service.addMoisToCollectionIfMissing(moisCollection, mois);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mois);
      });

      it('should add only unique Mois to an array', () => {
        const moisArray: IMois[] = [{ id: 123 }, { id: 456 }, { id: 3198 }];
        const moisCollection: IMois[] = [{ id: 123 }];
        expectedResult = service.addMoisToCollectionIfMissing(moisCollection, ...moisArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const mois: IMois = { id: 123 };
        const mois2: IMois = { id: 456 };
        expectedResult = service.addMoisToCollectionIfMissing([], mois, mois2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mois);
        expect(expectedResult).toContain(mois2);
      });

      it('should accept null and undefined values', () => {
        const mois: IMois = { id: 123 };
        expectedResult = service.addMoisToCollectionIfMissing([], null, mois, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mois);
      });

      it('should return initial array if no Mois is added', () => {
        const moisCollection: IMois[] = [{ id: 123 }];
        expectedResult = service.addMoisToCollectionIfMissing(moisCollection, undefined, null);
        expect(expectedResult).toEqual(moisCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
