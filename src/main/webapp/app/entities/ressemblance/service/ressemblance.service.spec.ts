import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRessemblance, Ressemblance } from '../ressemblance.model';

import { RessemblanceService } from './ressemblance.service';

describe('Ressemblance Service', () => {
  let service: RessemblanceService;
  let httpMock: HttpTestingController;
  let elemDefault: IRessemblance;
  let expectedResult: IRessemblance | IRessemblance[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RessemblanceService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      description: 'AAAAAAA',
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

    it('should create a Ressemblance', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Ressemblance()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Ressemblance', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Ressemblance', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
        },
        new Ressemblance()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Ressemblance', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          description: 'BBBBBB',
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

    it('should delete a Ressemblance', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addRessemblanceToCollectionIfMissing', () => {
      it('should add a Ressemblance to an empty array', () => {
        const ressemblance: IRessemblance = { id: 123 };
        expectedResult = service.addRessemblanceToCollectionIfMissing([], ressemblance);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ressemblance);
      });

      it('should not add a Ressemblance to an array that contains it', () => {
        const ressemblance: IRessemblance = { id: 123 };
        const ressemblanceCollection: IRessemblance[] = [
          {
            ...ressemblance,
          },
          { id: 456 },
        ];
        expectedResult = service.addRessemblanceToCollectionIfMissing(ressemblanceCollection, ressemblance);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Ressemblance to an array that doesn't contain it", () => {
        const ressemblance: IRessemblance = { id: 123 };
        const ressemblanceCollection: IRessemblance[] = [{ id: 456 }];
        expectedResult = service.addRessemblanceToCollectionIfMissing(ressemblanceCollection, ressemblance);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ressemblance);
      });

      it('should add only unique Ressemblance to an array', () => {
        const ressemblanceArray: IRessemblance[] = [{ id: 123 }, { id: 456 }, { id: 80524 }];
        const ressemblanceCollection: IRessemblance[] = [{ id: 123 }];
        expectedResult = service.addRessemblanceToCollectionIfMissing(ressemblanceCollection, ...ressemblanceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ressemblance: IRessemblance = { id: 123 };
        const ressemblance2: IRessemblance = { id: 456 };
        expectedResult = service.addRessemblanceToCollectionIfMissing([], ressemblance, ressemblance2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ressemblance);
        expect(expectedResult).toContain(ressemblance2);
      });

      it('should accept null and undefined values', () => {
        const ressemblance: IRessemblance = { id: 123 };
        expectedResult = service.addRessemblanceToCollectionIfMissing([], null, ressemblance, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ressemblance);
      });

      it('should return initial array if no Ressemblance is added', () => {
        const ressemblanceCollection: IRessemblance[] = [{ id: 123 }];
        expectedResult = service.addRessemblanceToCollectionIfMissing(ressemblanceCollection, undefined, null);
        expect(expectedResult).toEqual(ressemblanceCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
