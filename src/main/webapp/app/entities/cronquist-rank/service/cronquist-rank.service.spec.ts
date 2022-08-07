import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { CronquistTaxonomicRank } from 'app/entities/enumerations/cronquist-taxonomic-rank.model';
import { ICronquistRank, CronquistRank } from '../cronquist-rank.model';

import { CronquistRankService } from './cronquist-rank.service';

describe('CronquistRank Service', () => {
  let service: CronquistRankService;
  let httpMock: HttpTestingController;
  let elemDefault: ICronquistRank;
  let expectedResult: ICronquistRank | ICronquistRank[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CronquistRankService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      rank: CronquistTaxonomicRank.DOMAINE,
      nom: 'AAAAAAA',
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

    it('should create a CronquistRank', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new CronquistRank()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CronquistRank', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          rank: 'BBBBBB',
          nom: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CronquistRank', () => {
      const patchObject = Object.assign(
        {
          nom: 'BBBBBB',
        },
        new CronquistRank()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CronquistRank', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          rank: 'BBBBBB',
          nom: 'BBBBBB',
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

    it('should delete a CronquistRank', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCronquistRankToCollectionIfMissing', () => {
      it('should add a CronquistRank to an empty array', () => {
        const cronquistRank: ICronquistRank = { id: 123 };
        expectedResult = service.addCronquistRankToCollectionIfMissing([], cronquistRank);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cronquistRank);
      });

      it('should not add a CronquistRank to an array that contains it', () => {
        const cronquistRank: ICronquistRank = { id: 123 };
        const cronquistRankCollection: ICronquistRank[] = [
          {
            ...cronquistRank,
          },
          { id: 456 },
        ];
        expectedResult = service.addCronquistRankToCollectionIfMissing(cronquistRankCollection, cronquistRank);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CronquistRank to an array that doesn't contain it", () => {
        const cronquistRank: ICronquistRank = { id: 123 };
        const cronquistRankCollection: ICronquistRank[] = [{ id: 456 }];
        expectedResult = service.addCronquistRankToCollectionIfMissing(cronquistRankCollection, cronquistRank);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cronquistRank);
      });

      it('should add only unique CronquistRank to an array', () => {
        const cronquistRankArray: ICronquistRank[] = [{ id: 123 }, { id: 456 }, { id: 65980 }];
        const cronquistRankCollection: ICronquistRank[] = [{ id: 123 }];
        expectedResult = service.addCronquistRankToCollectionIfMissing(cronquistRankCollection, ...cronquistRankArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cronquistRank: ICronquistRank = { id: 123 };
        const cronquistRank2: ICronquistRank = { id: 456 };
        expectedResult = service.addCronquistRankToCollectionIfMissing([], cronquistRank, cronquistRank2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cronquistRank);
        expect(expectedResult).toContain(cronquistRank2);
      });

      it('should accept null and undefined values', () => {
        const cronquistRank: ICronquistRank = { id: 123 };
        expectedResult = service.addCronquistRankToCollectionIfMissing([], null, cronquistRank, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cronquistRank);
      });

      it('should return initial array if no CronquistRank is added', () => {
        const cronquistRankCollection: ICronquistRank[] = [{ id: 123 }];
        expectedResult = service.addCronquistRankToCollectionIfMissing(cronquistRankCollection, undefined, null);
        expect(expectedResult).toEqual(cronquistRankCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
