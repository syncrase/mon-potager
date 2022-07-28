import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGermination, Germination } from '../germination.model';

import { GerminationService } from './germination.service';

describe('Germination Service', () => {
  let service: GerminationService;
  let httpMock: HttpTestingController;
  let elemDefault: IGermination;
  let expectedResult: IGermination | IGermination[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GerminationService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      tempsDeGermination: 'AAAAAAA',
      conditionDeGermination: 'AAAAAAA',
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

    it('should create a Germination', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Germination()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Germination', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          tempsDeGermination: 'BBBBBB',
          conditionDeGermination: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Germination', () => {
      const patchObject = Object.assign(
        {
          tempsDeGermination: 'BBBBBB',
          conditionDeGermination: 'BBBBBB',
        },
        new Germination()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Germination', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          tempsDeGermination: 'BBBBBB',
          conditionDeGermination: 'BBBBBB',
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

    it('should delete a Germination', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addGerminationToCollectionIfMissing', () => {
      it('should add a Germination to an empty array', () => {
        const germination: IGermination = { id: 123 };
        expectedResult = service.addGerminationToCollectionIfMissing([], germination);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(germination);
      });

      it('should not add a Germination to an array that contains it', () => {
        const germination: IGermination = { id: 123 };
        const germinationCollection: IGermination[] = [
          {
            ...germination,
          },
          { id: 456 },
        ];
        expectedResult = service.addGerminationToCollectionIfMissing(germinationCollection, germination);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Germination to an array that doesn't contain it", () => {
        const germination: IGermination = { id: 123 };
        const germinationCollection: IGermination[] = [{ id: 456 }];
        expectedResult = service.addGerminationToCollectionIfMissing(germinationCollection, germination);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(germination);
      });

      it('should add only unique Germination to an array', () => {
        const germinationArray: IGermination[] = [{ id: 123 }, { id: 456 }, { id: 53324 }];
        const germinationCollection: IGermination[] = [{ id: 123 }];
        expectedResult = service.addGerminationToCollectionIfMissing(germinationCollection, ...germinationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const germination: IGermination = { id: 123 };
        const germination2: IGermination = { id: 456 };
        expectedResult = service.addGerminationToCollectionIfMissing([], germination, germination2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(germination);
        expect(expectedResult).toContain(germination2);
      });

      it('should accept null and undefined values', () => {
        const germination: IGermination = { id: 123 };
        expectedResult = service.addGerminationToCollectionIfMissing([], null, germination, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(germination);
      });

      it('should return initial array if no Germination is added', () => {
        const germinationCollection: IGermination[] = [{ id: 123 }];
        expectedResult = service.addGerminationToCollectionIfMissing(germinationCollection, undefined, null);
        expect(expectedResult).toEqual(germinationCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
