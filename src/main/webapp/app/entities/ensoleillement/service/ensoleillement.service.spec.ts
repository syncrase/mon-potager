import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEnsoleillement, Ensoleillement } from '../ensoleillement.model';

import { EnsoleillementService } from './ensoleillement.service';

describe('Ensoleillement Service', () => {
  let service: EnsoleillementService;
  let httpMock: HttpTestingController;
  let elemDefault: IEnsoleillement;
  let expectedResult: IEnsoleillement | IEnsoleillement[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EnsoleillementService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      orientation: 'AAAAAAA',
      ensoleilement: 0,
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

    it('should create a Ensoleillement', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Ensoleillement()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Ensoleillement', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          orientation: 'BBBBBB',
          ensoleilement: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Ensoleillement', () => {
      const patchObject = Object.assign({}, new Ensoleillement());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Ensoleillement', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          orientation: 'BBBBBB',
          ensoleilement: 1,
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

    it('should delete a Ensoleillement', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addEnsoleillementToCollectionIfMissing', () => {
      it('should add a Ensoleillement to an empty array', () => {
        const ensoleillement: IEnsoleillement = { id: 123 };
        expectedResult = service.addEnsoleillementToCollectionIfMissing([], ensoleillement);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ensoleillement);
      });

      it('should not add a Ensoleillement to an array that contains it', () => {
        const ensoleillement: IEnsoleillement = { id: 123 };
        const ensoleillementCollection: IEnsoleillement[] = [
          {
            ...ensoleillement,
          },
          { id: 456 },
        ];
        expectedResult = service.addEnsoleillementToCollectionIfMissing(ensoleillementCollection, ensoleillement);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Ensoleillement to an array that doesn't contain it", () => {
        const ensoleillement: IEnsoleillement = { id: 123 };
        const ensoleillementCollection: IEnsoleillement[] = [{ id: 456 }];
        expectedResult = service.addEnsoleillementToCollectionIfMissing(ensoleillementCollection, ensoleillement);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ensoleillement);
      });

      it('should add only unique Ensoleillement to an array', () => {
        const ensoleillementArray: IEnsoleillement[] = [{ id: 123 }, { id: 456 }, { id: 7899 }];
        const ensoleillementCollection: IEnsoleillement[] = [{ id: 123 }];
        expectedResult = service.addEnsoleillementToCollectionIfMissing(ensoleillementCollection, ...ensoleillementArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ensoleillement: IEnsoleillement = { id: 123 };
        const ensoleillement2: IEnsoleillement = { id: 456 };
        expectedResult = service.addEnsoleillementToCollectionIfMissing([], ensoleillement, ensoleillement2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ensoleillement);
        expect(expectedResult).toContain(ensoleillement2);
      });

      it('should accept null and undefined values', () => {
        const ensoleillement: IEnsoleillement = { id: 123 };
        expectedResult = service.addEnsoleillementToCollectionIfMissing([], null, ensoleillement, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ensoleillement);
      });

      it('should return initial array if no Ensoleillement is added', () => {
        const ensoleillementCollection: IEnsoleillement[] = [{ id: 123 }];
        expectedResult = service.addEnsoleillementToCollectionIfMissing(ensoleillementCollection, undefined, null);
        expect(expectedResult).toEqual(ensoleillementCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
