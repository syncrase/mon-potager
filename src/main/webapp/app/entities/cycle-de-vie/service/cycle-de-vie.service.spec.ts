import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICycleDeVie, CycleDeVie } from '../cycle-de-vie.model';

import { CycleDeVieService } from './cycle-de-vie.service';

describe('CycleDeVie Service', () => {
  let service: CycleDeVieService;
  let httpMock: HttpTestingController;
  let elemDefault: ICycleDeVie;
  let expectedResult: ICycleDeVie | ICycleDeVie[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CycleDeVieService);
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

    it('should create a CycleDeVie', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new CycleDeVie()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CycleDeVie', () => {
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

    it('should partial update a CycleDeVie', () => {
      const patchObject = Object.assign({}, new CycleDeVie());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CycleDeVie', () => {
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

    it('should delete a CycleDeVie', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCycleDeVieToCollectionIfMissing', () => {
      it('should add a CycleDeVie to an empty array', () => {
        const cycleDeVie: ICycleDeVie = { id: 123 };
        expectedResult = service.addCycleDeVieToCollectionIfMissing([], cycleDeVie);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cycleDeVie);
      });

      it('should not add a CycleDeVie to an array that contains it', () => {
        const cycleDeVie: ICycleDeVie = { id: 123 };
        const cycleDeVieCollection: ICycleDeVie[] = [
          {
            ...cycleDeVie,
          },
          { id: 456 },
        ];
        expectedResult = service.addCycleDeVieToCollectionIfMissing(cycleDeVieCollection, cycleDeVie);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CycleDeVie to an array that doesn't contain it", () => {
        const cycleDeVie: ICycleDeVie = { id: 123 };
        const cycleDeVieCollection: ICycleDeVie[] = [{ id: 456 }];
        expectedResult = service.addCycleDeVieToCollectionIfMissing(cycleDeVieCollection, cycleDeVie);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cycleDeVie);
      });

      it('should add only unique CycleDeVie to an array', () => {
        const cycleDeVieArray: ICycleDeVie[] = [{ id: 123 }, { id: 456 }, { id: 17004 }];
        const cycleDeVieCollection: ICycleDeVie[] = [{ id: 123 }];
        expectedResult = service.addCycleDeVieToCollectionIfMissing(cycleDeVieCollection, ...cycleDeVieArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cycleDeVie: ICycleDeVie = { id: 123 };
        const cycleDeVie2: ICycleDeVie = { id: 456 };
        expectedResult = service.addCycleDeVieToCollectionIfMissing([], cycleDeVie, cycleDeVie2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cycleDeVie);
        expect(expectedResult).toContain(cycleDeVie2);
      });

      it('should accept null and undefined values', () => {
        const cycleDeVie: ICycleDeVie = { id: 123 };
        expectedResult = service.addCycleDeVieToCollectionIfMissing([], null, cycleDeVie, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cycleDeVie);
      });

      it('should return initial array if no CycleDeVie is added', () => {
        const cycleDeVieCollection: ICycleDeVie[] = [{ id: 123 }];
        expectedResult = service.addCycleDeVieToCollectionIfMissing(cycleDeVieCollection, undefined, null);
        expect(expectedResult).toEqual(cycleDeVieCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
