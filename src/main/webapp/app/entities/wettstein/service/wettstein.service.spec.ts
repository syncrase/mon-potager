import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IWettstein, Wettstein } from '../wettstein.model';

import { WettsteinService } from './wettstein.service';

describe('Wettstein Service', () => {
  let service: WettsteinService;
  let httpMock: HttpTestingController;
  let elemDefault: IWettstein;
  let expectedResult: IWettstein | IWettstein[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(WettsteinService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
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

    it('should create a Wettstein', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Wettstein()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Wettstein', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Wettstein', () => {
      const patchObject = Object.assign({}, new Wettstein());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Wettstein', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
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

    it('should delete a Wettstein', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addWettsteinToCollectionIfMissing', () => {
      it('should add a Wettstein to an empty array', () => {
        const wettstein: IWettstein = { id: 123 };
        expectedResult = service.addWettsteinToCollectionIfMissing([], wettstein);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(wettstein);
      });

      it('should not add a Wettstein to an array that contains it', () => {
        const wettstein: IWettstein = { id: 123 };
        const wettsteinCollection: IWettstein[] = [
          {
            ...wettstein,
          },
          { id: 456 },
        ];
        expectedResult = service.addWettsteinToCollectionIfMissing(wettsteinCollection, wettstein);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Wettstein to an array that doesn't contain it", () => {
        const wettstein: IWettstein = { id: 123 };
        const wettsteinCollection: IWettstein[] = [{ id: 456 }];
        expectedResult = service.addWettsteinToCollectionIfMissing(wettsteinCollection, wettstein);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(wettstein);
      });

      it('should add only unique Wettstein to an array', () => {
        const wettsteinArray: IWettstein[] = [{ id: 123 }, { id: 456 }, { id: 24729 }];
        const wettsteinCollection: IWettstein[] = [{ id: 123 }];
        expectedResult = service.addWettsteinToCollectionIfMissing(wettsteinCollection, ...wettsteinArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const wettstein: IWettstein = { id: 123 };
        const wettstein2: IWettstein = { id: 456 };
        expectedResult = service.addWettsteinToCollectionIfMissing([], wettstein, wettstein2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(wettstein);
        expect(expectedResult).toContain(wettstein2);
      });

      it('should accept null and undefined values', () => {
        const wettstein: IWettstein = { id: 123 };
        expectedResult = service.addWettsteinToCollectionIfMissing([], null, wettstein, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(wettstein);
      });

      it('should return initial array if no Wettstein is added', () => {
        const wettsteinCollection: IWettstein[] = [{ id: 123 }];
        expectedResult = service.addWettsteinToCollectionIfMissing(wettsteinCollection, undefined, null);
        expect(expectedResult).toEqual(wettsteinCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
