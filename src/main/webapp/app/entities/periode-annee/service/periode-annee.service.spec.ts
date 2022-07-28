import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPeriodeAnnee, PeriodeAnnee } from '../periode-annee.model';

import { PeriodeAnneeService } from './periode-annee.service';

describe('PeriodeAnnee Service', () => {
  let service: PeriodeAnneeService;
  let httpMock: HttpTestingController;
  let elemDefault: IPeriodeAnnee;
  let expectedResult: IPeriodeAnnee | IPeriodeAnnee[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PeriodeAnneeService);
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

    it('should create a PeriodeAnnee', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new PeriodeAnnee()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PeriodeAnnee', () => {
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

    it('should partial update a PeriodeAnnee', () => {
      const patchObject = Object.assign({}, new PeriodeAnnee());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PeriodeAnnee', () => {
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

    it('should delete a PeriodeAnnee', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPeriodeAnneeToCollectionIfMissing', () => {
      it('should add a PeriodeAnnee to an empty array', () => {
        const periodeAnnee: IPeriodeAnnee = { id: 123 };
        expectedResult = service.addPeriodeAnneeToCollectionIfMissing([], periodeAnnee);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(periodeAnnee);
      });

      it('should not add a PeriodeAnnee to an array that contains it', () => {
        const periodeAnnee: IPeriodeAnnee = { id: 123 };
        const periodeAnneeCollection: IPeriodeAnnee[] = [
          {
            ...periodeAnnee,
          },
          { id: 456 },
        ];
        expectedResult = service.addPeriodeAnneeToCollectionIfMissing(periodeAnneeCollection, periodeAnnee);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PeriodeAnnee to an array that doesn't contain it", () => {
        const periodeAnnee: IPeriodeAnnee = { id: 123 };
        const periodeAnneeCollection: IPeriodeAnnee[] = [{ id: 456 }];
        expectedResult = service.addPeriodeAnneeToCollectionIfMissing(periodeAnneeCollection, periodeAnnee);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(periodeAnnee);
      });

      it('should add only unique PeriodeAnnee to an array', () => {
        const periodeAnneeArray: IPeriodeAnnee[] = [{ id: 123 }, { id: 456 }, { id: 59038 }];
        const periodeAnneeCollection: IPeriodeAnnee[] = [{ id: 123 }];
        expectedResult = service.addPeriodeAnneeToCollectionIfMissing(periodeAnneeCollection, ...periodeAnneeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const periodeAnnee: IPeriodeAnnee = { id: 123 };
        const periodeAnnee2: IPeriodeAnnee = { id: 456 };
        expectedResult = service.addPeriodeAnneeToCollectionIfMissing([], periodeAnnee, periodeAnnee2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(periodeAnnee);
        expect(expectedResult).toContain(periodeAnnee2);
      });

      it('should accept null and undefined values', () => {
        const periodeAnnee: IPeriodeAnnee = { id: 123 };
        expectedResult = service.addPeriodeAnneeToCollectionIfMissing([], null, periodeAnnee, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(periodeAnnee);
      });

      it('should return initial array if no PeriodeAnnee is added', () => {
        const periodeAnneeCollection: IPeriodeAnnee[] = [{ id: 123 }];
        expectedResult = service.addPeriodeAnneeToCollectionIfMissing(periodeAnneeCollection, undefined, null);
        expect(expectedResult).toEqual(periodeAnneeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
