import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITypeSemis, TypeSemis } from '../type-semis.model';

import { TypeSemisService } from './type-semis.service';

describe('TypeSemis Service', () => {
  let service: TypeSemisService;
  let httpMock: HttpTestingController;
  let elemDefault: ITypeSemis;
  let expectedResult: ITypeSemis | ITypeSemis[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TypeSemisService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      type: 'AAAAAAA',
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

    it('should create a TypeSemis', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new TypeSemis()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TypeSemis', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          type: 'BBBBBB',
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

    it('should partial update a TypeSemis', () => {
      const patchObject = Object.assign(
        {
          type: 'BBBBBB',
        },
        new TypeSemis()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TypeSemis', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          type: 'BBBBBB',
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

    it('should delete a TypeSemis', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTypeSemisToCollectionIfMissing', () => {
      it('should add a TypeSemis to an empty array', () => {
        const typeSemis: ITypeSemis = { id: 123 };
        expectedResult = service.addTypeSemisToCollectionIfMissing([], typeSemis);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(typeSemis);
      });

      it('should not add a TypeSemis to an array that contains it', () => {
        const typeSemis: ITypeSemis = { id: 123 };
        const typeSemisCollection: ITypeSemis[] = [
          {
            ...typeSemis,
          },
          { id: 456 },
        ];
        expectedResult = service.addTypeSemisToCollectionIfMissing(typeSemisCollection, typeSemis);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TypeSemis to an array that doesn't contain it", () => {
        const typeSemis: ITypeSemis = { id: 123 };
        const typeSemisCollection: ITypeSemis[] = [{ id: 456 }];
        expectedResult = service.addTypeSemisToCollectionIfMissing(typeSemisCollection, typeSemis);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(typeSemis);
      });

      it('should add only unique TypeSemis to an array', () => {
        const typeSemisArray: ITypeSemis[] = [{ id: 123 }, { id: 456 }, { id: 99196 }];
        const typeSemisCollection: ITypeSemis[] = [{ id: 123 }];
        expectedResult = service.addTypeSemisToCollectionIfMissing(typeSemisCollection, ...typeSemisArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const typeSemis: ITypeSemis = { id: 123 };
        const typeSemis2: ITypeSemis = { id: 456 };
        expectedResult = service.addTypeSemisToCollectionIfMissing([], typeSemis, typeSemis2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(typeSemis);
        expect(expectedResult).toContain(typeSemis2);
      });

      it('should accept null and undefined values', () => {
        const typeSemis: ITypeSemis = { id: 123 };
        expectedResult = service.addTypeSemisToCollectionIfMissing([], null, typeSemis, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(typeSemis);
      });

      it('should return initial array if no TypeSemis is added', () => {
        const typeSemisCollection: ITypeSemis[] = [{ id: 123 }];
        expectedResult = service.addTypeSemisToCollectionIfMissing(typeSemisCollection, undefined, null);
        expect(expectedResult).toEqual(typeSemisCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
