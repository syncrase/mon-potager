import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAllelopathie, Allelopathie } from '../allelopathie.model';

import { AllelopathieService } from './allelopathie.service';

describe('Allelopathie Service', () => {
  let service: AllelopathieService;
  let httpMock: HttpTestingController;
  let elemDefault: IAllelopathie;
  let expectedResult: IAllelopathie | IAllelopathie[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AllelopathieService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      type: 'AAAAAAA',
      description: 'AAAAAAA',
      impact: 0,
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

    it('should create a Allelopathie', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Allelopathie()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Allelopathie', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          type: 'BBBBBB',
          description: 'BBBBBB',
          impact: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Allelopathie', () => {
      const patchObject = Object.assign(
        {
          type: 'BBBBBB',
          impact: 1,
        },
        new Allelopathie()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Allelopathie', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          type: 'BBBBBB',
          description: 'BBBBBB',
          impact: 1,
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

    it('should delete a Allelopathie', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAllelopathieToCollectionIfMissing', () => {
      it('should add a Allelopathie to an empty array', () => {
        const allelopathie: IAllelopathie = { id: 123 };
        expectedResult = service.addAllelopathieToCollectionIfMissing([], allelopathie);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(allelopathie);
      });

      it('should not add a Allelopathie to an array that contains it', () => {
        const allelopathie: IAllelopathie = { id: 123 };
        const allelopathieCollection: IAllelopathie[] = [
          {
            ...allelopathie,
          },
          { id: 456 },
        ];
        expectedResult = service.addAllelopathieToCollectionIfMissing(allelopathieCollection, allelopathie);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Allelopathie to an array that doesn't contain it", () => {
        const allelopathie: IAllelopathie = { id: 123 };
        const allelopathieCollection: IAllelopathie[] = [{ id: 456 }];
        expectedResult = service.addAllelopathieToCollectionIfMissing(allelopathieCollection, allelopathie);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(allelopathie);
      });

      it('should add only unique Allelopathie to an array', () => {
        const allelopathieArray: IAllelopathie[] = [{ id: 123 }, { id: 456 }, { id: 14593 }];
        const allelopathieCollection: IAllelopathie[] = [{ id: 123 }];
        expectedResult = service.addAllelopathieToCollectionIfMissing(allelopathieCollection, ...allelopathieArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const allelopathie: IAllelopathie = { id: 123 };
        const allelopathie2: IAllelopathie = { id: 456 };
        expectedResult = service.addAllelopathieToCollectionIfMissing([], allelopathie, allelopathie2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(allelopathie);
        expect(expectedResult).toContain(allelopathie2);
      });

      it('should accept null and undefined values', () => {
        const allelopathie: IAllelopathie = { id: 123 };
        expectedResult = service.addAllelopathieToCollectionIfMissing([], null, allelopathie, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(allelopathie);
      });

      it('should return initial array if no Allelopathie is added', () => {
        const allelopathieCollection: IAllelopathie[] = [{ id: 123 }];
        expectedResult = service.addAllelopathieToCollectionIfMissing(allelopathieCollection, undefined, null);
        expect(expectedResult).toEqual(allelopathieCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
