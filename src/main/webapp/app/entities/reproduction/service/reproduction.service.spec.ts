import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IReproduction, Reproduction } from '../reproduction.model';

import { ReproductionService } from './reproduction.service';

describe('Reproduction Service', () => {
  let service: ReproductionService;
  let httpMock: HttpTestingController;
  let elemDefault: IReproduction;
  let expectedResult: IReproduction | IReproduction[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ReproductionService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      vitesse: 'AAAAAAA',
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

    it('should create a Reproduction', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Reproduction()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Reproduction', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          vitesse: 'BBBBBB',
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

    it('should partial update a Reproduction', () => {
      const patchObject = Object.assign(
        {
          vitesse: 'BBBBBB',
          type: 'BBBBBB',
        },
        new Reproduction()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Reproduction', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          vitesse: 'BBBBBB',
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

    it('should delete a Reproduction', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addReproductionToCollectionIfMissing', () => {
      it('should add a Reproduction to an empty array', () => {
        const reproduction: IReproduction = { id: 123 };
        expectedResult = service.addReproductionToCollectionIfMissing([], reproduction);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(reproduction);
      });

      it('should not add a Reproduction to an array that contains it', () => {
        const reproduction: IReproduction = { id: 123 };
        const reproductionCollection: IReproduction[] = [
          {
            ...reproduction,
          },
          { id: 456 },
        ];
        expectedResult = service.addReproductionToCollectionIfMissing(reproductionCollection, reproduction);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Reproduction to an array that doesn't contain it", () => {
        const reproduction: IReproduction = { id: 123 };
        const reproductionCollection: IReproduction[] = [{ id: 456 }];
        expectedResult = service.addReproductionToCollectionIfMissing(reproductionCollection, reproduction);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(reproduction);
      });

      it('should add only unique Reproduction to an array', () => {
        const reproductionArray: IReproduction[] = [{ id: 123 }, { id: 456 }, { id: 62542 }];
        const reproductionCollection: IReproduction[] = [{ id: 123 }];
        expectedResult = service.addReproductionToCollectionIfMissing(reproductionCollection, ...reproductionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const reproduction: IReproduction = { id: 123 };
        const reproduction2: IReproduction = { id: 456 };
        expectedResult = service.addReproductionToCollectionIfMissing([], reproduction, reproduction2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(reproduction);
        expect(expectedResult).toContain(reproduction2);
      });

      it('should accept null and undefined values', () => {
        const reproduction: IReproduction = { id: 123 };
        expectedResult = service.addReproductionToCollectionIfMissing([], null, reproduction, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(reproduction);
      });

      it('should return initial array if no Reproduction is added', () => {
        const reproductionCollection: IReproduction[] = [{ id: 123 }];
        expectedResult = service.addReproductionToCollectionIfMissing(reproductionCollection, undefined, null);
        expect(expectedResult).toEqual(reproductionCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
