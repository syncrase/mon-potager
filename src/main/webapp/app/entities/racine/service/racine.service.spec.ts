import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRacine, Racine } from '../racine.model';

import { RacineService } from './racine.service';

describe('Racine Service', () => {
  let service: RacineService;
  let httpMock: HttpTestingController;
  let elemDefault: IRacine;
  let expectedResult: IRacine | IRacine[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RacineService);
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

    it('should create a Racine', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Racine()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Racine', () => {
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

    it('should partial update a Racine', () => {
      const patchObject = Object.assign({}, new Racine());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Racine', () => {
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

    it('should delete a Racine', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addRacineToCollectionIfMissing', () => {
      it('should add a Racine to an empty array', () => {
        const racine: IRacine = { id: 123 };
        expectedResult = service.addRacineToCollectionIfMissing([], racine);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(racine);
      });

      it('should not add a Racine to an array that contains it', () => {
        const racine: IRacine = { id: 123 };
        const racineCollection: IRacine[] = [
          {
            ...racine,
          },
          { id: 456 },
        ];
        expectedResult = service.addRacineToCollectionIfMissing(racineCollection, racine);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Racine to an array that doesn't contain it", () => {
        const racine: IRacine = { id: 123 };
        const racineCollection: IRacine[] = [{ id: 456 }];
        expectedResult = service.addRacineToCollectionIfMissing(racineCollection, racine);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(racine);
      });

      it('should add only unique Racine to an array', () => {
        const racineArray: IRacine[] = [{ id: 123 }, { id: 456 }, { id: 7076 }];
        const racineCollection: IRacine[] = [{ id: 123 }];
        expectedResult = service.addRacineToCollectionIfMissing(racineCollection, ...racineArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const racine: IRacine = { id: 123 };
        const racine2: IRacine = { id: 456 };
        expectedResult = service.addRacineToCollectionIfMissing([], racine, racine2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(racine);
        expect(expectedResult).toContain(racine2);
      });

      it('should accept null and undefined values', () => {
        const racine: IRacine = { id: 123 };
        expectedResult = service.addRacineToCollectionIfMissing([], null, racine, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(racine);
      });

      it('should return initial array if no Racine is added', () => {
        const racineCollection: IRacine[] = [{ id: 123 }];
        expectedResult = service.addRacineToCollectionIfMissing(racineCollection, undefined, null);
        expect(expectedResult).toEqual(racineCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
