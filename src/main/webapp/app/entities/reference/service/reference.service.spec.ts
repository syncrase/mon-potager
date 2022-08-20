import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ReferenceType } from 'app/entities/enumerations/reference-type.model';
import { IReference, Reference } from '../reference.model';

import { ReferenceService } from './reference.service';

describe('Reference Service', () => {
  let service: ReferenceService;
  let httpMock: HttpTestingController;
  let elemDefault: IReference;
  let expectedResult: IReference | IReference[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ReferenceService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      description: 'AAAAAAA',
      type: ReferenceType.IMAGE,
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

    it('should create a Reference', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Reference()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Reference', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          description: 'BBBBBB',
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

    it('should partial update a Reference', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
          type: 'BBBBBB',
        },
        new Reference()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Reference', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          description: 'BBBBBB',
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

    it('should delete a Reference', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addReferenceToCollectionIfMissing', () => {
      it('should add a Reference to an empty array', () => {
        const reference: IReference = { id: 123 };
        expectedResult = service.addReferenceToCollectionIfMissing([], reference);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(reference);
      });

      it('should not add a Reference to an array that contains it', () => {
        const reference: IReference = { id: 123 };
        const referenceCollection: IReference[] = [
          {
            ...reference,
          },
          { id: 456 },
        ];
        expectedResult = service.addReferenceToCollectionIfMissing(referenceCollection, reference);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Reference to an array that doesn't contain it", () => {
        const reference: IReference = { id: 123 };
        const referenceCollection: IReference[] = [{ id: 456 }];
        expectedResult = service.addReferenceToCollectionIfMissing(referenceCollection, reference);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(reference);
      });

      it('should add only unique Reference to an array', () => {
        const referenceArray: IReference[] = [{ id: 123 }, { id: 456 }, { id: 5088 }];
        const referenceCollection: IReference[] = [{ id: 123 }];
        expectedResult = service.addReferenceToCollectionIfMissing(referenceCollection, ...referenceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const reference: IReference = { id: 123 };
        const reference2: IReference = { id: 456 };
        expectedResult = service.addReferenceToCollectionIfMissing([], reference, reference2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(reference);
        expect(expectedResult).toContain(reference2);
      });

      it('should accept null and undefined values', () => {
        const reference: IReference = { id: 123 };
        expectedResult = service.addReferenceToCollectionIfMissing([], null, reference, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(reference);
      });

      it('should return initial array if no Reference is added', () => {
        const referenceCollection: IReference[] = [{ id: 123 }];
        expectedResult = service.addReferenceToCollectionIfMissing(referenceCollection, undefined, null);
        expect(expectedResult).toEqual(referenceCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
